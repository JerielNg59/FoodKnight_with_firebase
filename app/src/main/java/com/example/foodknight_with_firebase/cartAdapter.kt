package com.example.foodknight_with_firebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.cusFoodList
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class cartAdapter(private val cartList: ArrayList<cart>) :
    RecyclerView.Adapter<cartAdapter.MyViewHolder>() {

    private lateinit var cartListener: onItemClickListener

    var onItemClick:((cart)->Unit)?=null

    interface onItemClickListener {
        fun onButtonClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        cartListener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.cartitem, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: cart = cartList[position]

        holder.name.text = currentItem.foodName
        holder.qty.text = currentItem.qty.toString()

        lateinit var db : FirebaseFirestore
        db = FirebaseFirestore.getInstance()
        db.collection("seller").document(currentItem.restaurantEmail.toString()).collection("foodList").document(
            currentItem.foodName.toString()).get().addOnSuccessListener{it->
            if(it!=null){
                holder.price.text = "RM " + String.format("%.2f",it.get("foodPrice").toString().toDouble())
                Picasso.get().load(it.get("foodLink").toString()).into(holder.img)
            }

        }

        holder.del.setOnClickListener{
            cartListener.onButtonClick(position)
        }

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var name: TextView = itemView.findViewById(R.id.cartFoodName)
        var price: TextView = itemView.findViewById(R.id.cartPrice)
        var qty: TextView = itemView.findViewById(R.id.cartQty)
        var img :ImageView = itemView.findViewById(R.id.cartImg)

        var del:ImageButton = itemView.findViewById(R.id.delCart)



    }


}