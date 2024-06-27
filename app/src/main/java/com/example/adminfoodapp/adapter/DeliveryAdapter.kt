package com.example.adminfoodapp.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.adminfoodapp.databinding.DeliveryItemBinding

class DeliveryAdapter(
    private val customerNames: MutableList<String>,
    private val customerPaymentStatus: MutableList<Boolean>,
    private val itemClicked:onItemClicked
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    interface onItemClicked{
        fun onItemClickListener(position:Int)
    }
    inner class DeliveryViewHolder(private val binding: DeliveryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                customerName.text = customerNames[position]
                if (customerPaymentStatus[position] == true) {
                    paymentStatus.text = "Received"
                } else {
                    paymentStatus.text = "Not Received"
                }

                val colorMap = mapOf(true to Color.GREEN, false to Color.RED)
                statusColor.backgroundTintList =
                    ColorStateList.valueOf(colorMap[customerPaymentStatus[position]] ?: Color.BLACK)
                val currentColor = colorMap[customerPaymentStatus[position]] ?: Color.BLACK
                paymentStatus.setTextColor(currentColor)

                itemView.setOnClickListener{
                    itemClicked.onItemClickListener(position)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding =
            DeliveryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return customerNames.size
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }
}