package com.example.foodknight_with_firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class orderHisAdapter(private val ohList: ArrayList<history>) :
    RecyclerView.Adapter<orderHisAdapter.MyViewHolder>() {

    //var onItemClick:((restaurant)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.historyrvlay, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: history = ohList[position]

        holder.name.text = currentItem.foodName.toString()
        holder.price.text = "Price : " + String.format("%.2f", currentItem.price?.toDouble() ?: 0.00)
        holder.qty.text = "Qty : " + currentItem.qty.toString()

//        holder.itemView.setOnClickListener{
//            onItemClick?.invoke(currentItem)
//        }
    }

    override fun getItemCount(): Int {
        return ohList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var name: TextView = itemView.findViewById(R.id.his_foodName)
        var price: TextView = itemView.findViewById(R.id.his_foodPrice)
        var qty: TextView = itemView.findViewById(R.id.his_foodQty)
    }


}