package tochka.tochkatestovoe.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GithubUser(
    @SerializedName("login") @Expose var name: String,
    @SerializedName("avatar_url") @Expose var avatarUrl: String,
    @SerializedName("url") @Expose var pageUrl:String?,
    @SerializedName("score") @Expose var score: Double?)
