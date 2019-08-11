package tochka.tochkatestovoe.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GithubSearch {

    @SerializedName("total_count")
    @Expose
    var totalCount: Int = 0

    @SerializedName("incomplete_results")
    @Expose
    var incompleteResults: Boolean = false

    @SerializedName("items")
    @Expose
    var githubUserList: ArrayList<GithubUser> = ArrayList()



}