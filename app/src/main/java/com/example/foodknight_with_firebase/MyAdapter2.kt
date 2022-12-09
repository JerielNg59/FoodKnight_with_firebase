package com.example.foodknight_with_firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.restaurant


class MyAdapter2(private val restaurantList: ArrayList<restaurant>) :
    RecyclerView.Adapter<MyAdapter2.MyViewHolder>() {

    var onItemClick:((restaurant)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.restaurant, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: restaurant = restaurantList[position]

        holder.name.text = currentItem.shopname
        holder.information.text = currentItem.shopaddress

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.restaurantName)
        var information: TextView = itemView.findViewById(R.id.restaurantDesc)
    }


}