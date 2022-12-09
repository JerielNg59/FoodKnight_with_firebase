package com.example.foodknight_with_firebase.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.R
import com.squareup.picasso.Picasso

class HomeAdapter(private var foodList  : ArrayList<Foods>) : RecyclerView.Adapter< HomeAdapter.MyViewHolder>() {
    private  lateinit var oldList : ArrayList<Foods>
    private lateinit var mListener: HomeAdapter.onItemClickListener

    interface onItemClickListener {
        //fun onItemClick(position: Int)
        fun onButtonClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_food,parent,false)
        return MyViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val food:Foods = foodList[position]




        holder.foodDesc.text = food.foodDesc
        Picasso.get().load(food.foodLink).into(holder.foodLink)
        holder.foodName.text = food.foodName
        holder.foodPrice.text = food.foodPrice
        holder.foodStatus.text = food.foodStatus


    }

    override fun getItemCount(): Int {
        return foodList.size

    }

    class MyViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){

        var  foodDesc : TextView = itemView.findViewById(R.id.txtDesc)
        var  foodLink : ImageView = itemView.findViewById(R.id.foodPic)
        var  foodName : TextView = itemView.findViewById(R.id.txtName)
        var  foodPrice : TextView = itemView.findViewById(R.id.txtPrice)
        var foodStatus : TextView = itemView.findViewById(R.id.txtStatus)
        val myButton: Button = itemView.findViewById<Button>(R.id.btnEdit)


        init {
            // itemView.setOnClickListener{

            //listener.onItemClick(adapterPosition)
            // }
            myButton.setOnClickListener{

                listener.onButtonClick(adapterPosition)
            }

        }

    }


}