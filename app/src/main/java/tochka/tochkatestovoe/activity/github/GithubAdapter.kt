package tochka.tochkatestovoe.activity.github

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.github_profile_raw.view.userAvatarUrlIv
import kotlinx.android.synthetic.main.github_profile_raw.view.userNameTv
import kotlinx.android.synthetic.main.github_profile_raw.view.userPageUrlTv
import kotlinx.android.synthetic.main.github_profile_raw.view.userScoreTv
import tochka.tochkatestovoe.R.layout
import tochka.tochkatestovoe.activity.github.GithubAdapter.CustomViewHolder
import tochka.tochkatestovoe.model.GithubUser

class GithubAdapter(private val context: Context, private val items: ArrayList<GithubUser>): RecyclerView.Adapter<CustomViewHolder>() {

    class CustomViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameTv = view.userNameTv as TextView
        val userAvatarUrlIv = view.userAvatarUrlIv as CircleImageView
        val scoreTv = view.userScoreTv as TextView
        val githubLinkTv = view.userPageUrlTv as TextView
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(context).inflate(layout.github_profile_raw, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: CustomViewHolder, p1: Int) {
        p0.nameTv.text = items[p1].name
        Picasso.with(context).load(items[p1].avatarUrl).into(p0.userAvatarUrlIv)
        p0.scoreTv.text = items[p1].score.toString()
        p0.githubLinkTv.text = items[p1].pageUrl
    }

    fun updateData(data: ArrayList<GithubUser>) {
        items.addAll(data)
        context.mainLooper.run {
            notifyDataSetChanged()
        }
    }

    fun clearAll() {
        items.clear()
        context.mainLooper.run {
            notifyDataSetChanged()
        }
    }
}