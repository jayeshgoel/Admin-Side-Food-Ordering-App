package com.example.adminfoodapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import com.example.adminfoodapp.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdminProfile : AppCompatActivity() {
    private val binding:ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var adminRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        adminRef=database.reference.child("adminProfile")

        binding.adminname.isEnabled=false
        binding.adminaddress.isEnabled=false
        binding.adminemail.isEnabled=false
        binding.adminphone.isEnabled=false

        binding.adminProfileSaveInformation.isEnabled=false

        var isEnable = false
        binding.clickToEdit.setOnClickListener {

            isEnable=true
            binding.adminname.isEnabled=true
            binding.adminaddress.isEnabled=true
            binding.adminemail.isEnabled=true
            binding.adminphone.isEnabled=true

            binding.adminProfileSaveInformation.isEnabled=true

            if(isEnable){
                binding.adminname.requestFocus()
            }
        }
        binding.adminProfileSaveInformation.setOnClickListener {
            var name=binding.adminname.text.toString()
            var address=binding.adminaddress.text.toString()
            var email=binding.adminemail.text.toString()
            var phone=binding.adminphone.text.toString()
            if(name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty()){
                Toast.makeText(this,"Fill all the Details",Toast.LENGTH_LONG).show()
            }else{
                var data= hashMapOf("name" to name,"address" to address,"email" to email,"phone" to phone)
                adminRef.setValue(data).addOnCompleteListener {task->
                    if(task.isSuccessful) {
                        Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this, "Failure to Update Profile", Toast.LENGTH_LONG).show()
                    }
                }

                binding.adminname.isEnabled=false
                binding.adminaddress.isEnabled=false
                binding.adminemail.isEnabled=false
                binding.adminphone.isEnabled=false

                binding.adminProfileSaveInformation.isEnabled=false

            }


        }
        binding.backButton2.setOnClickListener{
            finish()
        }

        retrieveUserData()



    }

    private fun retrieveUserData() {
        adminRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var ownerName=snapshot.child("name").value.toString()
                    var address=snapshot.child("address").value.toString()
                    var email=snapshot.child("email").value.toString()
                    var phone =snapshot.child("phone").value.toString()
                    setDataToTextView(ownerName,address,email,phone)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDataToTextView(ownerName: String, address: String, email: String, phone: String) {
        binding.adminname.setText(ownerName)
        binding.adminaddress.setText(address)
        binding.adminemail.setText(email)
        binding.adminphone.setText(phone)

    }
}