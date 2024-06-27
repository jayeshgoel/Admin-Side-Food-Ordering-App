package com.example.adminfoodapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodapp.databinding.ActivityOrderDetailBinding
import com.example.adminfoodapp.databinding.OrderDetailsItemsBindingBinding

class OrderDetailAdapter(private val context:Context,
    private val foodNames:ArrayList<String>,
     private val foodImages:ArrayList<String>,
     private val foodPrices:ArrayList<String>,
     private val foodQuantity:ArrayList<Int>
    ) :RecyclerView.Adapter<OrderDetailAdapter.OrderDetailsViewHolder>(){


    inner class OrderDetailsViewHolder( private var binding: OrderDetailsItemsBindingBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                orderDetailFoodName.text=foodNames[position]
                orderDetailQuantity.text=foodQuantity[position].toString()
                orderDetailTotalPrice.text=foodPrices[position] + "$"

                // seting image
                val uriString=foodImages[position]
                val uri= Uri.parse(uriString)
                Glide.with(context).load(uri).into(orderDetailFoodImage)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding=OrderDetailsItemsBindingBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return OrderDetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return foodNames.size
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(position)
    }
}