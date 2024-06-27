package com.example.adminfoodapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminfoodapp.adapter.PendingOrderAdapter
import com.example.adminfoodapp.databinding.ActivityPendingBinding
import com.example.foodorderingapp.Model.OrderDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingActivity : AppCompatActivity() ,PendingOrderAdapter.onItemClicked{
    private var listOfName:MutableList<String> = mutableListOf()
    private var listOfTotalPrices:MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder:MutableList<String> = mutableListOf()

    private var listOfOrderItem:ArrayList<OrderDetails> = arrayListOf()

    private lateinit var database:FirebaseDatabase
    private lateinit var databaseOrderDetails:DatabaseReference
    private lateinit var adapter: PendingOrderAdapter
    private lateinit var valueEventListener: ValueEventListener

    private val binding:ActivityPendingBinding by lazy {
        ActivityPendingBinding.inflate(layoutInflater)
    }
    private var inProgress=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)


        // Initialization of database
        database=FirebaseDatabase.getInstance()
        databaseOrderDetails=database.reference.child("OrderDetails")

        setAdapter()
        getOrderDetails()

        binding.backButton3.setOnClickListener{
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        databaseOrderDetails.removeEventListener(valueEventListener)
    }
    private fun updateProgressBar(){
        if(inProgress) {
            binding.pendingOrderRecyclerView.visibility=View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.progressBar.visibility = View.INVISIBLE
            binding.pendingOrderRecyclerView.visibility=View.VISIBLE
        }
    }

    private fun getOrderDetails() {
        // retrieve order details from firebase database
        
        valueEventListener=(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                inProgress=true
                updateProgressBar()
                listOfOrderItem.clear()
                for(orderSnapshot in snapshot.children){

                    val orderDetails=orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let { listOfOrderItem.add(it) }
                }
                addDataToListForRecyclerView()

                inProgress=false
                updateProgressBar()

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        databaseOrderDetails.addValueEventListener(valueEventListener)

    }

    private fun addDataToListForRecyclerView() {
        listOfName.clear()
        listOfTotalPrices.clear()
        listOfImageFirstFoodOrder.clear()
        for(orderItem in listOfOrderItem){
            orderItem.userName?.let{ listOfName.add(it)}
            orderItem.totalPrice?.let{ listOfTotalPrices.add(it)}
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach{
                listOfImageFirstFoodOrder.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun setAdapter() {
        binding.pendingOrderRecyclerView.layoutManager=LinearLayoutManager(this)
        adapter=PendingOrderAdapter(this,listOfName,listOfTotalPrices, listOfImageFirstFoodOrder,this)
        binding.pendingOrderRecyclerView.adapter=adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent= Intent(this,OrderDetailActivity::class.java)
        val userOrderDetails=listOfOrderItem[position]
        intent.putExtra("userOrderDetails",userOrderDetails )
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        // handle item acceptance update database
        val childItemPushKey=listOfOrderItem[position].itemPushKey
        val clickItemOrderRef=childItemPushKey?.let {
            database.reference.child("OrderDetails").child(childItemPushKey)
        }
        clickItemOrderRef?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

    private fun updateOrderAcceptStatus(position: Int) {
        // update order acceptance in user's buy history and orderDetails
        val userIdOfClickedItem=listOfOrderItem[position].userId
        val pushKeyOfClickItem=listOfOrderItem[position].itemPushKey

        listOfOrderItem[position].orderAccepted=true

        // updating in user buy History
        val buyHistoryRef=database.reference.child("users").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickItem!!)
        buyHistoryRef.child("orderAccepted").setValue(true).addOnSuccessListener {
            Toast.makeText(this, "Order is Accepted",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Order is not Accepted",Toast.LENGTH_LONG).show()
        }
        // updating in Order Details
        databaseOrderDetails.child(pushKeyOfClickItem).child("orderAccepted").setValue(true)
    }

    override fun onItemDispatchClickListener(position: Int) {
        val dispatchItemPushKey=listOfOrderItem[position].itemPushKey
        val dispatchItemOrderRef=database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        val userIdOfClickedItem=listOfOrderItem[position].userId

        listOfOrderItem[position].orderDispatched=true

        // updating in user buy History
        val buyHistoryRef=database.reference.child("users").child(userIdOfClickedItem!!).child("BuyHistory").child(dispatchItemPushKey!!)
        buyHistoryRef.child("orderDispatched").setValue(true).addOnSuccessListener {
            Toast.makeText(this, "Order is Dispatched",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Order is not Dispatched",Toast.LENGTH_LONG).show()
        }

        dispatchItemOrderRef.setValue(listOfOrderItem[position]).addOnSuccessListener { // adding to CompletedOrders
            deleteThisItemFromOrderDetails(dispatchItemPushKey)  // deleting from OrderDetails
        }

    }

    override fun isItemAccepted(position: Int): Boolean {
        return listOfOrderItem[position].orderAccepted
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemRef=database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemRef.removeValue().addOnSuccessListener {
            Toast.makeText(this, "Order is Dispatched",Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Order is not Dispatched",Toast.LENGTH_LONG).show()
        }


    }


}