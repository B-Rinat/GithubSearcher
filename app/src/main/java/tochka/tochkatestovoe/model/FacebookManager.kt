package tochka.tochkatestovoe.model

import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest

data class FacebookUserData constructor(val name:String, val email:String, val isAvailable: Boolean)

interface FacebookProfileDataCallBack {
    fun onDataAvailable(data: FacebookUserData)
}

fun getFacebookProfileData(callBack: FacebookProfileDataCallBack) {
    val request = GraphRequest.newMeRequest(
        AccessToken.getCurrentAccessToken()) { `object`, response ->

        var name = ""
        var email = ""
        val data: FacebookUserData = if(`object` != null && !`object`.isNull("name")) {
            if(`object`.has("email")){
                email = `object`.getString("email")
            }
            if(`object`.has("name")){
                name = `object`.getString("name")
            }
            Log.e("TAG", "avatarUrl: $email name: $name")
            FacebookUserData(name, email, true)
        } else {
            FacebookUserData("", "", false)
        }

        callBack.onDataAvailable(data)
    }
    val parameters = Bundle()
    parameters.putString("fields", "id,name,link,email")
    request.parameters = parameters
    request.executeAsync()
}