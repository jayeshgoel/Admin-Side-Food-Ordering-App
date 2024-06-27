package com.example.adminfoodapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodapp.Model.UserModel
import com.example.adminfoodapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private lateinit var email:String
    private lateinit var password:String
    private lateinit var auth:FirebaseAuth
    private var name :String ?= null
    private var nameOfRestaurant :String ?= null
    private lateinit var database:DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient


    private val binding:ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)



        // Initialize Firebase Auth
        auth=Firebase.auth

        // Initialize Firebase Database
        database=Firebase.database.reference



        binding.loginButton.setOnClickListener {
            email= binding.loginEmail.text.toString().trim()
            password=binding.loginPassword.text.toString().trim()

            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all Details",Toast.LENGTH_LONG).show()
            }
            else{
                createUserAccount(email,password)
            }

        }


        //Initialize google sign in
        val googleSignInOptions=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient=GoogleSignIn.getClient(this,googleSignInOptions)
        binding.googleLoginButton.setOnClickListener {
            val signIntent=googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        binding.dontHaveAccountText.setOnClickListener{
            val intent=Intent(this,SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun createUserAccount(curremail: String, currpassword: String) {
        auth.signInWithEmailAndPassword(curremail,currpassword).addOnCompleteListener{task->

            if(task.isSuccessful){

                val user = auth.currentUser
                Toast.makeText(this,"Login Successful",Toast.LENGTH_LONG).show()
                updateUI(user)

            }
//             TO CREATE ACCOUNT IF NOT ALREADY SIGNED UP
            else{
                Toast.makeText(this,"Authentication Failed",Toast.LENGTH_LONG).show()
//                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
//                    if(task.isSuccessful){
//                        val user = auth.currentUser
//                        Toast.makeText(this,"User created and Login successful",Toast.LENGTH_LONG).show()
//                        saveUserData()
//                        updateUI(user)
//                    }
//                    else{
//                        Toast.makeText(this,"Authentication failed",Toast.LENGTH_LONG).show()
//                        Log.d("ACCOUNT","createUserAccount:Authentication failed ",task.exception)
//                    }
//                }
            }
        }
    }

    private fun saveUserData() {
        email= binding.loginEmail.toString().trim()
        password=binding.loginPassword.toString().trim()

        val user=UserModel(name,nameOfRestaurant,email,password)
        val userId=FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("users").child(it).setValue(user)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser=auth.currentUser
        if(currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

    }
    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result->
        if(result.resultCode== Activity.RESULT_OK){
            val task=GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if(task.isSuccessful){
                val account: GoogleSignInAccount? =task.result
                val credential=GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask->
                    if(authTask.isSuccessful){
                        // Successfully sign in with google
                        Toast.makeText(this,"Successfully Sign In With Google",Toast.LENGTH_LONG).show()
                        updateUI(authTask.result?.user )
                    }
                    else{
                        Toast.makeText(this,"Google Sign In Failed",Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Google Sign In Failed",Toast.LENGTH_LONG).show()
            }
        }
    }
}