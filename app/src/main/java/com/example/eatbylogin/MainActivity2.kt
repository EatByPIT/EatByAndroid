package com.example.eatbylogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eatbylogin.databinding.ActivityCadastroBinding
import com.example.eatbylogin.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)

        window.statusBarColor = Color.parseColor("#547cb2")

        val view = binding.root
        setContentView(view)
        auth = Firebase.auth

        binding.btnGoToMA.setOnClickListener() {

            val intent = Intent(this,
                CadastroActivity::class.java)

            startActivity(intent)

            finish()

        }
    }
}