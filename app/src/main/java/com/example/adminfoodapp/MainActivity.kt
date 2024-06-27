package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.example.adminfoodapp.databinding.ActivityMainBinding
import com.example.foodorderingapp.Model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var database:FirebaseDatabase
    private lateinit var auth:FirebaseAuth
    private lateinit var completeOrderRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        binding.addMenu.setOnClickListener{
            val intent= Intent(this,AddItemActivity::class.java)
            startActivity(intent)
        }
        binding.AllItemMenu.setOnClickListener{
            val intent= Intent(this,AllItem::class.java)
            startActivity(intent)
        }
        binding.orderDispatch.setOnClickListener{
            val intent= Intent(this,OutForDelivery::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener{
            val intent= Intent(this,AdminProfile::class.java)
            startActivity(intent)
        }
        binding.addUser.setOnClickListener{
            val intent= Intent(this,CreateUser::class.java)
            startActivity(intent)
        }
        binding.pendingOrderTextView.setOnClickListener{
            val intent= Intent(this,PendingActivity::class.java)
            startActivity(intent)
        }
        binding.mainActivityLogOut.setOnClickListener{

                auth.signOut()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()

        }


    }

    override fun onStart() {
        super.onStart()
        pendingOrder()
        completedOrder()
        wholeEarning()
    }

     private fun wholeEarning() {
        database=FirebaseDatabase.getInstance()
        var completedOrderRef=database.reference.child("CompletedOrder")
        val listPay: MutableList<Int> = mutableListOf()
        completedOrderRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(order in snapshot.children){
                    val completeOrder=order.getValue(OrderDetails::class.java)
                    completeOrder?.totalPrice?.replace("$","")?.toIntOrNull()?.let {
                        listPay.add(it)
                    }
                }
                binding.mainActivityTotalEarning.text=listPay.sum().toString()+"$"
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    private fun completedOrder() {
        database=FirebaseDatabase.getInstance()
        var completedOrderRef=database.reference.child("CompletedOrder")
        var completeOrderCount=0
        completedOrderRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                completeOrderCount=snapshot.childrenCount.toInt()
                binding.mainActivityCompletedOrderCount.text=completeOrderCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun pendingOrder() {
        database=FirebaseDatabase.getInstance()
        var pendingOrderRef=database.reference.child("OrderDetails")
        var pendingOrderItemCount=0
        pendingOrderRef.addListenerForSingleValueEvent(object:ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount=snapshot.childrenCount.toInt()
                binding.mainActivityPendingOrderCount.text=pendingOrderItemCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}