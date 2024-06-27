package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.adminfoodapp.adapter.DeliveryAdapter
import com.example.adminfoodapp.databinding.ActivityOutForDeliveryBinding
import com.example.foodorderingapp.Model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class OutForDelivery : AppCompatActivity(),DeliveryAdapter.onItemClicked {
    private val binding: ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private lateinit var database:FirebaseDatabase
    private var listOfCompletedOrderList:ArrayList<OrderDetails> = arrayListOf()
    private var inProgress=false
    private var customerName= mutableListOf<String>()
    private var paymentStatus= mutableListOf<Boolean>()

    private lateinit var adapter:DeliveryAdapter
    private lateinit var valueEventListener: ValueEventListener
    private lateinit var completedOrderRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.outForDeliveryBackButton.setOnClickListener{
            finish()
        }
        setDataIntoRecyclerView()
        retrieveCompleteOrderDetails()



    }

    override fun onDestroy() {
        super.onDestroy()
        completedOrderRef.removeEventListener(valueEventListener)
    }

    private fun updateProgressBar(){
        if(inProgress) {
            binding.outForDeliveryRecyclerView.visibility= View.INVISIBLE
            binding.progressBar3.visibility = View.VISIBLE
        }
        else{
            binding.progressBar3.visibility = View.INVISIBLE
            binding.outForDeliveryRecyclerView.visibility= View.VISIBLE
        }
    }
    private fun retrieveCompleteOrderDetails() {

        database= FirebaseDatabase.getInstance()
        val databaseRef=database.reference
        completedOrderRef=databaseRef.child("CompletedOrder")

        valueEventListener=(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inProgress=true
                updateProgressBar()

                listOfCompletedOrderList.clear()
                customerName.clear()
                paymentStatus.clear()
                for(foodSnapshot in snapshot.children){
                    val currentOrder=foodSnapshot.getValue(OrderDetails::class.java)
                    currentOrder?.let { listOfCompletedOrderList.add(it) }

                }
                listOfCompletedOrderList.reverse()
                for(order in listOfCompletedOrderList){
                    order.userName?.let{customerName.add(it)}
                    order.paymentReceived?.let{paymentStatus.add(it)}
                }
                adapter.notifyDataSetChanged()

                inProgress=false
                updateProgressBar()


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        completedOrderRef.addValueEventListener(valueEventListener)
    }

    private fun setDataIntoRecyclerView() {

        adapter=DeliveryAdapter(customerName,paymentStatus,this)
        binding.outForDeliveryRecyclerView.layoutManager=LinearLayoutManager(this)
        binding.outForDeliveryRecyclerView.adapter=adapter




    }

    override fun onItemClickListener(position: Int) {
        val intent= Intent(this,OrderDetailActivity::class.java)
        val userOrderDetails=listOfCompletedOrderList[position]
        intent.putExtra("userOrderDetails",userOrderDetails )
        startActivity(intent)
    }
}