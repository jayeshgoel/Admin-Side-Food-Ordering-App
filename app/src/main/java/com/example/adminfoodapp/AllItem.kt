package com.example.adminfoodapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.Model.AllMenu
import com.example.adminfoodapp.adapter.MenuItemAdapter
import com.example.adminfoodapp.databinding.ActivityAllItemBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AllItem : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: MenuItemAdapter
    private var menuItems:ArrayList<AllMenu> = ArrayList()
    private var inProgress=false
    private lateinit var foodRef:DatabaseReference
    private lateinit var valueEventListener: ValueEventListener

    private val binding:ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        databaseReference=FirebaseDatabase.getInstance().reference
        setAdapter()
        retrieveMenuItems()
//
        binding.backButton1.setOnClickListener{
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        foodRef.removeEventListener(valueEventListener)
    }
    private fun updateProgressBar(){
        if(inProgress) {
            binding.allItemRecyclerView.visibility= View.INVISIBLE
            binding.progressBar2.visibility = View.VISIBLE


        }
        else{
            binding.progressBar2.visibility = View.INVISIBLE
            binding.allItemRecyclerView.visibility= View.VISIBLE
        }
    }
    private fun retrieveMenuItems() {
        database=FirebaseDatabase.getInstance()
        foodRef=database.reference.child("menu")
        // fetch data from database

        valueEventListener=(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inProgress=true
                updateProgressBar()
                // Clear existing data before populating
                menuItems.clear()
                for(foodSnapshot in snapshot.children){
                    val menuItem=foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let{
                        menuItems.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
                inProgress=false
                updateProgressBar()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError","Error : ${error.message}")
            }
        })
        foodRef.addValueEventListener(valueEventListener)
    }

    private fun setAdapter() {
        adapter=MenuItemAdapter(this, menuItems,databaseReference){position ->
            deleteMenuItem(position)
        }
        binding.allItemRecyclerView.layoutManager=LinearLayoutManager(this)
        binding.allItemRecyclerView.adapter=adapter
    }

    private fun deleteMenuItem(position: Int) {
        val menuItemToDelete=menuItems[position]
        val menuItemkey=menuItemToDelete.key
        val foodMenuRef=databaseReference.child("menu").child(menuItemkey!!)
        foodMenuRef.removeValue().addOnCompleteListener {task->
            if(task.isSuccessful){
                menuItems.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this,"Item Deleted Successfully",Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(this,"Failure to Delete Item",Toast.LENGTH_LONG).show()
            }

        }

    }
}