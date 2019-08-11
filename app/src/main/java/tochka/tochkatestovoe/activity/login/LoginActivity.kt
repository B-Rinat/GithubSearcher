package tochka.tochkatestovoe.activity.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import tochka.tochkatestovoe.R.id
import tochka.tochkatestovoe.R.layout
import tochka.tochkatestovoe.activity.github.GithubActivity
import java.util.Arrays

class LoginActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    private var loginButton: LoginButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layout.activity_login)
        callbackManager = CallbackManager.Factory.create()

        val EMAIL = "email"
        val PUBLIC_PROFILE = "public_profile"

        loginButton = findViewById<View>(id.login_button) as LoginButton
        loginButton!!.setPermissions(Arrays.asList(EMAIL, PUBLIC_PROFILE))

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

        if(isLoggedIn) {
            startActivity(Intent(this@LoginActivity, GithubActivity::class.java))
            finish()
        }

        // Callback registration
        loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                startActivity(Intent(this@LoginActivity, GithubActivity::class.java))
                finish()
            }
            override fun onCancel() {
                // App code
            }
            override fun onError(exception: FacebookException) {
                // App code
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
