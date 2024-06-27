package com.example.adminfoodapp.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adminfoodapp.Model.AllMenu
import com.example.adminfoodapp.databinding.FoodItemBinding
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener:(position:Int)->Unit
) : RecyclerView.Adapter<MenuItemAdapter.AllItemViewHolder>() {

    private val itemQuantitites = IntArray(menuList.size){1}

    inner class AllItemViewHolder(private val binding:FoodItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {


//                val quantity=itemQuantitites[position]
                val menuItem=menuList[position]
                val uriString=menuItem.foodImage
                val uri= Uri.parse(uriString)

                foodItemName.text=menuItem.foodName
                Glide.with(context).load(uri).into(foodItemImage)
                ItemPrice.text=menuItem.foodPrice
//                ItemQuantity.text=itemQuantitites[position].toString()

                addItemButton.setOnClickListener {
//                    increaseQuantity(position)
                }
                ReduceItem.setOnClickListener {
//                    decreaseQuantity(position)
                }
                DeleteItem.setOnClickListener{
                    onDeleteClickListener(position)
                }
            }
        }



        private fun decreaseQuantity(position: Int) {
            if(itemQuantitites[position]>1){
                itemQuantitites[position]--
                binding.ItemQuantity.text=itemQuantitites[position].toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            if(itemQuantitites[position]<10){
                itemQuantitites[position]++
                binding.ItemQuantity.text=itemQuantitites[position].toString()
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllItemViewHolder {
        val binding=FoodItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return AllItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: AllItemViewHolder, position: Int) {
        holder.bind(position)
    }
}