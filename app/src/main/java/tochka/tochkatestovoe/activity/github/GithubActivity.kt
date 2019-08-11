package tochka.tochkatestovoe.activity.github

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import kotlinx.android.synthetic.main.activity_github.drawer_layout
import kotlinx.android.synthetic.main.activity_github.nav_view
import kotlinx.android.synthetic.main.app_bar_github.fab
import kotlinx.android.synthetic.main.app_bar_github.toolbar
import kotlinx.android.synthetic.main.content_github.githubRecyclerView
import kotlinx.android.synthetic.main.content_github.searchProfileEt
import tochka.tochkatestovoe.R
import tochka.tochkatestovoe.R.id
import tochka.tochkatestovoe.R.layout
import tochka.tochkatestovoe.R.string
import tochka.tochkatestovoe.main.MainActivity
import tochka.tochkatestovoe.model.FacebookProfileDataCallBack
import tochka.tochkatestovoe.model.FacebookUserData
import tochka.tochkatestovoe.model.GithubUser
import tochka.tochkatestovoe.model.getFacebookProfileData
import tochka.tochkatestovoe.service.GithubService
import tochka.tochkatestovoe.util.isNetworkAvailable
import java.util.Arrays
import java.util.Timer
import java.util.TimerTask

class GithubActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    FacebookProfileDataCallBack {

    private var githubProfile: ArrayList<GithubUser> = ArrayList()

    private lateinit var customAdapter: GithubAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var paginator: PublishProcessor<Int>

    private lateinit var compositeDisposable: CompositeDisposable

    private lateinit var progressBar: ProgressBar

    private lateinit var currentUserQuery: String

    private var isLoadingPage: Boolean = false

    private var pageNumber: Int = 1

    private var VISIBLE_THRESHOLD: Int = 1

    private var lastVisibleItem: Int = 0

    private var totalItemCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_github)
        setSupportActionBar(toolbar)

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if (!isLoggedIn) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        } else {
            getFacebookProfileData(this)
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            string.navigation_drawer_open,
            string.navigation_drawer_close
        )

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)

        currentUserQuery = "Rinat"
        paginator = PublishProcessor.create()
        compositeDisposable = CompositeDisposable()
        progressBar = findViewById(id.progressBar)


        linearLayoutManager = LinearLayoutManager(this)
        githubRecyclerView.layoutManager = linearLayoutManager
        customAdapter = GithubAdapter(this, githubProfile)
        githubRecyclerView.adapter = customAdapter
        githubRecyclerView.setHasFixedSize(true)

        setUpLoadMoreListener()
        subscribeForData()

        searchProfileEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                val timer = Timer()
                timer.schedule(object: TimerTask() {
                    override fun run() {
                        try {
                            if(isNetworkAvailable(this@GithubActivity)) {
                                val q = searchProfileEt.text.toString()
                                if(q.isEmpty()) return
                                if(q == currentUserQuery) return
                                Log.e("TAG", "q: $q")
                                currentUserQuery = q
                                pageNumber = 0
                                mainLooper.run {
                                    customAdapter.clearAll()
                                    paginator.onNext(pageNumber)
                                }
                            } else {
                                Toast.makeText(applicationContext, "Network unavailable", Toast.LENGTH_SHORT).show()
                            }
                        } catch (exception: Exception) {
                            Log.e("Timer", "Exception: $exception")
                        }
                    }
                }, 600)
            }
        })
    }

    override fun onDataAvailable(data: FacebookUserData) {
        val url =  "https://graph.facebook.com/${AccessToken.getCurrentAccessToken().userId}/picture?type=large"
        val navigationView = findViewById<View>(id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        val profileImage = headerView.findViewById(id.imageView) as ImageView
        val profileName = headerView.findViewById<TextView>(id.nameTv)
        val profileLogin = headerView.findViewById<TextView>(id.loginTv)

        if (data.isAvailable) {
            profileName.text = data.name
            profileLogin.text = data.email
        }
        Picasso.with(applicationContext).load(url).into(profileImage)
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.github, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            id.nav_camera -> {
                // Handle the camera action
            }
            id.nav_gallery -> {

            }
            id.nav_slideshow -> {

            }
            id.nav_manage -> {

            }
            id.nav_share -> {

            }
            id.nav_send -> {

            }
            id.nav_logout -> {
                LoginManager.getInstance().logOut()
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

      private fun setUpLoadMoreListener() {
        githubRecyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView ,
                                   dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                totalItemCount = recyclerView.layoutManager?.itemCount!!
                lastVisibleItem = linearLayoutManager
                        .findLastVisibleItemPosition()
                if (!isLoadingPage
                        && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    if(isNetworkAvailable(this@GithubActivity)) {
                        pageNumber++
                        paginator.onNext(pageNumber)
                        isLoadingPage = true
                    } else {
                        Toast.makeText(applicationContext, "Network unavailable", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

     private fun subscribeForData() {
         val disposable = paginator
             .onBackpressureDrop()
             .doOnNext {
                 try {
                     isLoadingPage = true
                     mainLooper.run {
                         progressBar.visibility = View.VISIBLE
                     }
                 } catch (e: Exception) {}
             }
             .concatMap { GithubService.getUsersAfterSearch(pageNumber, currentUserQuery) }
             .observeOn(AndroidSchedulers.mainThread())
             .doOnError{
                 Log.d("TAG", it.message)
             }
             .subscribe { items ->
                 try {
                     isLoadingPage = false
                     mainLooper.run {
                         progressBar.visibility = View.INVISIBLE
                     }
                 } catch (e: Exception) {}
                 if (items.isSuccessful) {
                     items.body()?.githubUserList?.let { customAdapter.updateData(it) }
                 } else {
                     Log.e("TAG", "Error: ${items.errorBody()} \n" +
                             " ${items.message()} \n ${items.code()} \n ${items.headers()} \n")
                     mainLooper.run {
                         Toast.makeText(applicationContext, "Exceeded number of requests!", Toast.LENGTH_LONG).show()
                     }
                 }
             }
         compositeDisposable.add(disposable)
         if(isNetworkAvailable(this@GithubActivity)) {
             paginator.onNext(pageNumber)
         } else {
             Toast.makeText(applicationContext, "Network unavailable", Toast.LENGTH_SHORT).show()
         }
    }
}
