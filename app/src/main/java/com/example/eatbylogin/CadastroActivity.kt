package com.example.eatbylogin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.eatbylogin.databinding.ActivityCadastroBinding
import com.example.eatbylogin.databinding.ActivityPrincipalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.parseColor("#FFFCF6")

        binding = ActivityCadastroBinding.inflate(layoutInflater)

        val view = binding.root
        setContentView(view)
        auth = Firebase.auth

    binding.btnCadastrar.setOnClickListener() {
        if (TextUtils.isEmpty(binding.edtNome.text)){
            binding.edtNome.error =
                " Informe o nome para o cadastro "
        } else if(TextUtils.isEmpty(binding.edtUser.text)){
            binding.edtUser.error =
                " Informe o usuário para o cadastro "
        } else if (TextUtils.isEmpty(binding.edtTel.text)){
            binding.edtTel.error =
                " Informe o telefone para o cadastro "
        } else if (TextUtils.isEmpty(binding.edtSenha.text)){
            binding.edtSenha.error =
                " Informe a senha para o cadastro "
        } else {
            criarUsuarioESenha(
                binding.edtUser.text.toString(),
                binding.edtSenha.text.toString()
            )
        }

    }

        binding.tLogin2.setOnClickListener() {

            val intent = Intent(this,
                MainActivity::class.java)

            startActivity(intent)

            finish()
        }

    }
    private fun criarUsuarioESenha(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this)
        {
                task ->
            if(task.isSuccessful) {
                Toast.makeText(baseContext, "Criação de usuário efetuada com sucesso.",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    baseContext, "Erro de criação de usuário.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}