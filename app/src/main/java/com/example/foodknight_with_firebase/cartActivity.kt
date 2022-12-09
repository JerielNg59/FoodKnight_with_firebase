package com.example.foodknight_with_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.databinding.ActivityCartBinding
import com.example.foodknight_with_firebase.databinding.ActivityResShowFoodBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

class cartActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityCartBinding

    private lateinit var db : FirebaseFirestore
    private lateinit var cartRV : RecyclerView
    private lateinit var cartArrayList :ArrayList<cart>
    private lateinit var cartAdapter2: cartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartRV = binding.cartRVL
        cartRV.layoutManager = LinearLayoutManager(this)
        cartRV.setHasFixedSize(true)

        cartArrayList = arrayListOf()
        cartAdapter2 = cartAdapter(cartArrayList)
        cartRV.adapter= cartAdapter2

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        getCartData(email)

        cartAdapter2.setOnItemClickListener(object: cartAdapter.onItemClickListener{

            override fun onButtonClick(position: Int) {
                var toDel = cartArrayList[position].foodName

                if (email != null) {
                    if (toDel != null) {
                        db.collection("cart").document(email).collection("item").document(toDel).delete().addOnSuccessListener{
                            recreate()
                            Toast.makeText(this@cartActivity, "Item had been deleted", Toast.LENGTH_SHORT).show()
                        }
                        db.collection("cart").document(email).collection("item").get().addOnSuccessListener {
                            if(it.size()<=0){
                                db.collection("cart").document(email).delete()
                            }
                        }
                    }
                }
            }
        })

        binding.checkout.setOnClickListener {
            if (email != null) {
                db.collection("cart").document(email).collection("item").get().addOnSuccessListener {
                    if (it.size() <= 0) {
                        Toast.makeText(this, "Your cart is empty ! ", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(this, payment::class.java)
                        startActivity(intent)
                    }


                }
            }
        }
    }

    private fun getCartData(email: String?) {
        db = FirebaseFirestore.getInstance()
        if (email!= null) {
            db.collection("cart").document(email).collection("item").
            addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("firestore Error", error.message.toString())
                        return
                    }
                    var total:Double = 0.00

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
//                            var item:cusFoodList = dc.document.toObject(cusFoodList::class.java)
//                            foodArrayList.add(dc.document.toObject(cusFoodList::class.java))
//                            item.restaurantEmail = sellerEmail.toString()
//                            foodArrayList.add(item)
                            var item:cart = dc.document.toObject(cart::class.java)
                            cartArrayList.add(item)

                            total += item.foodPrice.toString().toDouble() * item.qty.toString().toDouble()
                            binding.cartTotal.text = "Total : RM " + String.format("%.2f",total)
                        }
                    }
                    cartAdapter2.notifyDataSetChanged()

                }

            })
        }else{
            Toast.makeText(this, "Something went wrong,please try again later", Toast.LENGTH_SHORT).show()
        }

    }



}