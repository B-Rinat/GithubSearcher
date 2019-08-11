package tochka.tochkatestovoe.model

import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GithubApi {

    // curl https://api.github.com/search/users?q=tom+repos:%3E42+followers:%3E1000

    @Headers("Content-Type: application/json")
    @GET("search/users")
    fun getSearchedReposCall(@Query("q") userName: String, @Query("page") pageNumber: Int): Call<GithubSearch>

    @Headers("Content-Type: application/json")
    @GET("search/users")
    fun getSearchedReposResponseFlowable(@Query("q") userName: String, @Query("page") pageNumber: Int): Flowable<Response<GithubSearch>>

}