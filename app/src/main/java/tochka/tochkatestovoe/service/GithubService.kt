package tochka.tochkatestovoe.service

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import tochka.tochkatestovoe.model.GithubApi
import tochka.tochkatestovoe.model.GithubSearch

class GithubService {

    companion object {
        private const val BASE_URL: String = "https://api.github.com/"
        private var githubSearchApi: GithubApi? = null
        private var retrofit: Retrofit? = null
        private fun getGithubSearcher(): GithubApi {
            if (githubSearchApi != null) {
                return githubSearchApi as GithubApi
            }
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            githubSearchApi = retrofit!!.create(
                GithubApi::class.java)
            return githubSearchApi as GithubApi
        }

        fun getUsersAfterSearch(pageNumber: Int, userName: String): Flowable<Response<GithubSearch>> {
            return getGithubSearcher()
                .getSearchedReposResponseFlowable(userName, pageNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError{
                    Log.d("TAG", it.message)
                }
        }
    }
}