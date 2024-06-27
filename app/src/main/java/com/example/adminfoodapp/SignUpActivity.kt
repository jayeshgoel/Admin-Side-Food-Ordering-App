package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodapp.Model.UserModel
import com.example.adminfoodapp.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var email:String
    private lateinit var password:String
    private lateinit var userName:String
    private lateinit var nameOfRestaurant :String
    private lateinit var database:DatabaseReference




    private val binding:ActivitySignUpBinding by lazy{
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialization Firebase Auth
        auth=Firebase.auth
        //Initialize Database
        database=Firebase.database.reference





        binding.CreateButton.setOnClickListener{

            email=binding.emailOfNewUser.text.toString().trim()
            password=binding.passwordOfNewUser.text.toString().trim()
            userName=binding.nameOfUser.text.toString().trim()
            nameOfRestaurant=binding.nameOfRestaurant.text.toString().trim()

            if(userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this,"Please fill all Details ",Toast.LENGTH_LONG).show()
            }
            else{
                createAccountFunction(email,password)
            }

        }

        binding.alreadyHaveAccountButton.setOnClickListener{
            val intent=Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()

        }



    }

    private fun createAccountFunction(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            if(task.isSuccessful){
                Toast.makeText(this,"Account created Successfully",Toast.LENGTH_LONG).show()

                saveUserDataFunction()
                val intent=Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                Toast.makeText(this,"Account creation Failed",Toast.LENGTH_LONG).show()
                Log.d("ACCOUNT","createAccount:Failure",task.exception)
            }
        }
    }

    private fun saveUserDataFunction() {
        email=binding.emailOfNewUser.text.toString().trim()
        password=binding.passwordOfNewUser.text.toString().trim()
        userName=binding.nameOfUser.text.toString().trim()
        nameOfRestaurant=binding.nameOfRestaurant.text.toString().trim()
        val user=UserModel(userName,nameOfRestaurant,email,password)
        val userId=FirebaseAuth.getInstance().currentUser!!.uid
        // save user data Firebase database
        database.child("admins").child(userId).setValue(user)

    }
}