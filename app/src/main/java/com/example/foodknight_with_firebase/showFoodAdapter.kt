package com.example.foodknight_with_firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.cusFoodList
import com.squareup.picasso.Picasso


class showFoodAdapter(private val cus_showFoodList: ArrayList<cusFoodList>) :
    RecyclerView.Adapter<showFoodAdapter.MyViewHolder>() {

    var onItemClick:((cusFoodList)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.cus_food_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: cusFoodList = cus_showFoodList[position]

        holder.name.text = currentItem.foodName
        holder.information.text = currentItem.foodDesc
        holder.price.text = "RM " + String.format("%.2f", currentItem.foodPrice?.toDouble() ?: 0.00)
        Picasso.get().load(currentItem.foodLink.toString()).into(holder.img)

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return cus_showFoodList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var name: TextView = itemView.findViewById(R.id.cus_foodName)
        var information: TextView = itemView.findViewById(R.id.cus_foodDesc)
        var price: TextView = itemView.findViewById(R.id.cus_foodPrice)
        var img :ImageView = itemView.findViewById(R.id.cus_foodImg)


    }


}