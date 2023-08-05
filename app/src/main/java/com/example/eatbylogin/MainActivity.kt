package com.example.eatbylogin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.eatbylogin.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.parseColor("#FFDC62")

        binding = ActivityMainBinding.inflate(layoutInflater)

        val view = binding.root

        setContentView(view)

        auth = Firebase.auth


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("744355781743-9gs3n1kb77nhq9jd7n54bvcsv6329ud8.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)



       binding.botaoEntrar.setOnClickListener {

           if(TextUtils.isEmpty(binding.editTextUser.text)){
               binding.editTextUser.error =
                   " Campo de usuário não pode estar em branco "
           } else if (TextUtils.isEmpty(binding.editTextPw.text)){
               binding.editTextPw.error =
                   " Campo de senha não pode estar em branco "
           } else {
               loginUsuarioESenha(binding.editTextUser.text.toString(),
                   binding.editTextPw.text.toString())
           }


       }

        binding.recuperarTxt.setOnClickListener {
            if(TextUtils.isEmpty(binding.editTextUser.text)){
                binding.editTextUser.error =
                    " Insira seu email para poder recuperar a sua senha "
            } else {

                enviarEmail(binding.editTextUser.text.toString())
            }
        }

        binding.signInButton.setOnClickListener {
            singIn()
        }

    binding.textCadastro.setOnClickListener {

        val intent2 = Intent(this,
            CadastroActivity::class.java)

        startActivity(intent2)

        finish()

    }

    }

    private fun singIn(){
        val intent = mGoogleSignInClient.signInIntent
        abreActivity.launch(intent)
    }

    val abreActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
            result: ActivityResult ->

        if(result.resultCode ==  RESULT_OK)  {
            val intent = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            try{
                val conta = task.getResult(ApiException::class.java)
                loginComGoogle(conta.idToken!!)
            } catch (_: ApiException) {

            }
        }

    }

    private fun loginComGoogle(token: String) {
        val credencial = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credencial).addOnCompleteListener(this)
        {
            task: Task<AuthResult> ->
            if(task.isSuccessful){
                Toast.makeText(baseContext, "Autenticação efetuada com o Google.",
                    Toast.LENGTH_LONG).show()
                abrePrincipal()
            } else {
                Toast.makeText(
                    baseContext, "Erro de autenticação com o Google.",
                    Toast.LENGTH_LONG
                ).show()

            }
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

    private fun loginUsuarioESenha(usuario: String, senha:String) {
        auth.signInWithEmailAndPassword(
            usuario,
             senha
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext, "Autenticação efetuada .",
                        Toast.LENGTH_LONG).show()
                    abrePrincipal()
                    //  updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext, "Erro de autenticação.",
                        Toast.LENGTH_LONG
                    ).show()
                    // updateUI(null)
                }
            }
    }

    fun abrePrincipal(){

        binding.editTextUser.text.clear()
        binding.editTextPw.text.clear()

        val intent = Intent(this,
            PrincipalActivity::class.java)

        startActivity(intent)

        finish()
    }


    private fun enviarEmail(usuario:String) {
        auth.sendPasswordResetEmail(usuario).addOnCompleteListener(this)
        {
                task ->
            if(task.isSuccessful) {
                Toast.makeText(baseContext, "Enviamos uma mensagem ao seu email com o link para você redefinir a sua senha.",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    baseContext, "Erro ao enviar email.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser


        if (currentUser != null) {
            if (currentUser.email?.isNotEmpty() == true) {
                Toast.makeText(
                    baseContext, "Usuário" + currentUser.email + "Logado",
                    Toast.LENGTH_LONG
                ).show()
                abrePrincipal()
            }
        }
      //  updateUI(currentUser)
    }



}


