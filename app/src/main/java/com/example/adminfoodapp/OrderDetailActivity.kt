package com.example.adminfoodapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager

import com.example.adminfoodapp.adapter.OrderDetailAdapter
import com.example.adminfoodapp.databinding.ActivityOrderDetailBinding
import com.example.foodorderingapp.Model.OrderDetails

class OrderDetailActivity : AppCompatActivity() {

    private val binding:ActivityOrderDetailBinding by lazy {
        ActivityOrderDetailBinding.inflate(layoutInflater)
    }

    private var userName:String? = null
    private var address:String? = null
    private var phoneNumber:String? = null

    private var totalPrice:String? = null

    private var foodNames:ArrayList<String> = arrayListOf()
    private var foodImages:ArrayList<String> = arrayListOf()
    private var foodPrices:ArrayList<String> = arrayListOf()
    private var foodQuantity:ArrayList<Int> = arrayListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.orderDetailBackButton.setOnClickListener{
            finish()
        }
        getDetailsFromIntent()

    }

    private fun getDetailsFromIntent() {
        val receivedOrderDetails=intent.getSerializableExtra("userOrderDetails") as OrderDetails
        receivedOrderDetails?.let {orderDetails ->

            userName=receivedOrderDetails.userName
            address=receivedOrderDetails.address
            phoneNumber=receivedOrderDetails.phoneNumber
            totalPrice=receivedOrderDetails.totalPrice

            foodNames= (receivedOrderDetails.foodName as ArrayList<String>?)!!
            foodImages= (receivedOrderDetails.foodImages as ArrayList<String>?)!!
            foodPrices= (receivedOrderDetails.foodPrices as ArrayList<String>?)!!
            foodQuantity= (receivedOrderDetails.foodQuantities as ArrayList<Int>?)!!

            setUserDetails()
            setAdapter()
        }

    }

    private fun setAdapter() {
        binding.orderDetailsRecyclerView.layoutManager=LinearLayoutManager(this)
        val adapter=OrderDetailAdapter(this,foodNames,foodImages,foodPrices,foodQuantity)
        binding.orderDetailsRecyclerView.adapter=adapter
    }

    private fun setUserDetails() {
        binding.orderDetailUserName.text=userName
        binding.orderDetailUserAddress.text=address
        binding.orderDetailPhoneNumber.text=phoneNumber
        binding.orderDetailTotalPrice.text=totalPrice


    }
}