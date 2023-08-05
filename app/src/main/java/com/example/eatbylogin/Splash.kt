package com.example.eatbylogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#5E8BC8")

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this,
                MainActivity2::class.java)

            startActivity(intent)

            finish()
        },3000)

        setContentView(R.layout.activity_splash)
    }
}