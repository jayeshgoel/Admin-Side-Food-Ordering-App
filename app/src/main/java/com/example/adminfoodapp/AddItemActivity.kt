package com.example.adminfoodapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.parseAsHtml
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.adminfoodapp.Model.AllMenu
import com.example.adminfoodapp.databinding.ActivityAddItemBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddItemActivity : AppCompatActivity() {

    private lateinit var foodName:String
    private lateinit var foodPrice:String
    private lateinit var foodDescription:String
    private var foodImageUri:Uri?=null
    private lateinit var foodIngredient:String


    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)




        binding.backButton.setOnClickListener{
            finish()
        }

        binding.selectImage.setOnClickListener {
            pickImage.launch("image/*")
        }


        // Initialize Firebase
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        binding.AddItemButton.setOnClickListener {
            foodName=binding.foodName.text.toString().trim()
            foodPrice=binding.foodPrice.text.toString().trim()
            foodDescription=binding.foodDescription.text.toString().trim()
            foodIngredient=binding.foodIngredient.text.toString().trim()
            if(!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredient.isBlank())){
                uploadData()
                Toast.makeText(this,"Item Added Succesfully",Toast.LENGTH_LONG).show()
                finish()
            }
            else{
                Toast.makeText(this,"Fill all the details",Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun uploadData() {
        // get a reference to the menu node in the database
         val menuRef=database.getReference("menu")
        // Generate a unique key for the new menu item
        val newItemKey=menuRef.push().key
        if(foodImageUri != null){
            val storageRef=FirebaseStorage.getInstance().reference
            val imageRef=storageRef.child("menu_images/${newItemKey}.jpg")
            val uploadTask=imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {downloadUrl->
                    // Create a new Menu item
                    val newItem= AllMenu(newItemKey,foodName,foodPrice,foodDescription,foodIngredient,downloadUrl.toString())
                    newItemKey?.let{
                        key->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this,"data uploaded Successfully",Toast.LENGTH_LONG).show()
                        }
                            .addOnFailureListener{
                                Toast.makeText(this,"data uploaded Failed",Toast.LENGTH_LONG).show()
                            }
                    }
                }

            }.addOnFailureListener{
                    Toast.makeText(this,"Image upload Failed",Toast.LENGTH_LONG).show()
                }

        }else{
            Toast.makeText(this,"Please Select an Image",Toast.LENGTH_LONG).show()
        }

    }

    private val pickImage=registerForActivityResult(ActivityResultContracts.GetContent()) {uri->
        if(uri!=null){
            binding.selectedImage.setImageURI(uri)
            binding.cardView3.visibility=View.VISIBLE
            foodImageUri=uri
        }

    }
}


