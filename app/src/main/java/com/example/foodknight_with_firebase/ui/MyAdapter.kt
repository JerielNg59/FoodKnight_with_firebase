package com.example.foodknight_with_firebase.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.R
import com.squareup.picasso.Picasso

class MyAdapter(private val foodList  : ArrayList<Foods>) : RecyclerView.Adapter< MyAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener


    interface onItemClickListener {
        //fun onItemClick(position: Int)
        fun onButtonClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener

    }









    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.all,parent,false)
        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int ) {
        val food:Foods = foodList[position]


        holder.foodDesc.text = food.foodDesc
        Picasso.get().load(food.foodLink).into(holder.foodLink)
        holder.foodName.text = food.foodName
        holder.foodPrice.text = food.foodPrice
        holder.foodQty.text = food.foodQty
        val item =
            holder.myButton.setOnClickListener{
                mListener.onButtonClick(position)
            }

    }

    override fun getItemCount(): Int {
        return foodList.size


    }

    class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var  foodDesc : TextView = itemView.findViewById(R.id.txtDesc)
        var  foodLink : ImageView = itemView.findViewById(R.id.foodPic)
        var  foodName : TextView = itemView.findViewById(R.id.txtName)
        var  foodPrice : TextView = itemView.findViewById(R.id.txtPrice)
        var  foodQty : TextView = itemView.findViewById(R.id.txtQty)
        val myButton: Button = itemView.findViewById<Button>(R.id.btnEdit)


    }


}