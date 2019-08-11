package tochka.tochkatestovoe.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import tochka.tochkatestovoe.R.layout
import tochka.tochkatestovoe.activity.login.LoginActivity

class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        finish()
    }
}
