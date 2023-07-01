package com.example.e_catapp.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.e_catapp.R

class SplashActivity  : AppCompatActivity() {
    private val SPLASH_TIME_OUT:Long = 1500 //delay 3s

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },SPLASH_TIME_OUT)
    }
}