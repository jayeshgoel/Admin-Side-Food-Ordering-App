package com.example.adminfoodapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodapp.databinding.PendingOrderItemBinding


class PendingOrderAdapter(private val context: Context,
                          private val customerName:MutableList<String>,
                          private val quantities:MutableList<String>,
                          private val images:MutableList<String>,
                            private val itemClicked:onItemClicked)  : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {


       interface onItemClicked{

           fun onItemClickListener(position: Int)
           fun onItemAcceptClickListener(position: Int)
           fun onItemDispatchClickListener(position: Int)
           fun isItemAccepted(position: Int):Boolean

       }
        inner class PendingOrderViewHolder(private val binding:PendingOrderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private var isAccepted=false
        fun bind(position: Int) {
            binding.apply{
                pendingOrderCustomerName.text=customerName[position]
                PendingOrderQuantity.text=quantities[position]
                var uriString=images[position]
                var uri=Uri.parse(uriString)
                Glide.with(context).load(uri).into(pendingOrderImage)
                isAccepted=itemClicked.isItemAccepted(position)
                pendingOrderAcceptButton.apply {
                    if(!isAccepted){
                        text="Accept"
                    }
                    else{
                        text="Dispatch"
                    }
                    setOnClickListener{
                        if(!isAccepted){
                            text="Dispatch"
                            isAccepted=true
                            itemClicked.onItemAcceptClickListener(position)
                        }
                        else{
                            customerName.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }
                itemView.setOnClickListener{
                    itemClicked.onItemClickListener(position)
                }
            }
        }
        fun showToast( message:String){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding= PendingOrderItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PendingOrderViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return customerName.size
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }
}