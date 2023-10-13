package com.example.eatbylogin

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.eatbylogin.databinding.ActivityLoginBinding
import com.example.eatbylogin.databinding.ActivityPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity() {

        private lateinit var binding: ActivityPrincipalBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrincipalBinding.inflate(layoutInflater)

        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference()

        fun replaceFragment(fragment : Fragment){

            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frame_layout,fragment)
            fragmentTransaction.commit()


        }


        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(Home())
                R.id.delete -> replaceFragment(Delete())
                R.id.logout -> replaceFragment(Logout())

                else ->{



                }

            }

            true

        }



        window.statusBarColor = Color.parseColor("#5E8BC8")

        val view = binding.root
        setContentView(view)

        auth = Firebase.auth


    }

}