package com.example.foodknight_with_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.foodknight_with_firebase.databinding.ActivityCusShowFoodDetailBinding
import com.example.foodknight_with_firebase.databinding.ActivityResShowFoodBinding
import kotlinx.coroutines.awaitAll
import android.view.View;
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class cus_showFoodDetail : AppCompatActivity() {
    private lateinit var binding: ActivityCusShowFoodDetailBinding

    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCusShowFoodDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var minus = binding.less
        var add = binding.more
        ///var qtyNum = binding.prnumber.text.toString().toInt()
        var qty = binding.prnumber

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        val food = intent.getParcelableExtra<cusFoodList>("foodInfo")
        db = FirebaseFirestore.getInstance()
        if (food != null) {
            var resEmail = food.uid
            var foodName = food.foodName
            var foodPrice = food.foodPrice

//            var resEmail = "satishs@gmail.com"
//            var foodName = "MCBurger"

            if (resEmail != null) {
                if (foodName != null) {
                    db.collection("seller").document(resEmail).collection("foodList")
                        .document(foodName).get().addOnSuccessListener {
                            if (it != null) {
                                //foodImg.setImageResource()
                                binding.cusFdFoodName.text = it.get("foodName").toString()
                                binding.cusFdFoodDesc.text = it.get("foodDesc").toString()
                                binding.cusFdFoodPrice.text = "RM " + it.get("foodPrice").toString()
                                Picasso.get().load(it.get("foodLink").toString())
                                    .into(binding.foodDetailImg)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Something went wrong,please try again2",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Something went wrong,please try again3",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(this, "Something went wrong,please try again4", Toast.LENGTH_SHORT)
                    .show()
            }
            binding.addCartBtn.setOnClickListener {
                var qtyNum = qty.text.toString().toInt()
                if (qtyNum >= 1) {
                    val loginnedUser = Firebase.auth.currentUser
                    val email = loginnedUser?.email
                    if (email != null) {
                        db.collection("cart").document(email).get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val docu = task.result
                                if (docu != null) {
                                    if (!docu.exists()) {
                                        var cartQty = qtyNum
                                        val cart: MutableMap<String, Any> =
                                            java.util.HashMap()
                                        cart["restaurantEmail"] = resEmail.toString()
                                        cart["foodName"] = foodName.toString()
                                        cart["qty"] = cartQty.toInt()
                                        cart["foodLink"] = food.foodLink.toString()
                                        cart["foodDesc"] = food.foodDesc.toString()
                                        if (foodPrice != null) {
                                            cart["foodPrice"] = foodPrice.toDouble()
                                        }
                                        if (email != null) {
                                            db.collection("cart").document(email)
                                                .collection("item")
                                                .document(foodName.toString())
                                                .set(cart, SetOptions.merge())
                                            db.collection("cart").document(email).set(
                                                hashMapOf(
                                                    "key" to 1
                                                )
                                            )
                                            Toast.makeText(
                                                this@cus_showFoodDetail,
                                                "Cart added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        if (resEmail != null) {
                                            qtyNum = qty.text.toString().toInt()
                                            checkCart(email, resEmail, food)
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Something went wrong,please try again3",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.minus.setOnClickListener {
            var qtyNum = qty.text.toString().toInt()
            if (qtyNum >= 1) {
                qty.setText((qtyNum - 1).toString())

            }
        }

        binding.add.setOnClickListener {
            var qtyNum = qty.text.toString().toInt()
            qty.setText((qtyNum + 1).toString())

        }
    }

    private fun checkCart(email: String, resEmail: String, food: cusFoodList) {
        var qty = binding.prnumber
        var qtyNum = qty.text.toString().toInt()
        var foodName = food.foodName

        db.collection("cart").document(email).collection("item").
        whereEqualTo("restaurantEmail",resEmail).get().addOnCompleteListener {
            // Toast.makeText(this, resEmail.toString(), Toast.LENGTH_SHORT).show()
            if (it.isSuccessful) {
                val doc = it.result
                if(doc.isEmpty){
                    Toast.makeText(
                        this@cus_showFoodDetail,
                        "You can only add item from one shop",
                        Toast.LENGTH_SHORT
                    ).show()
                }else {
                    if (foodName != null) {
                        db.collection("cart").document(email).collection("item")
                            .document(foodName).get().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val docu = task.result
                                    if (docu != null) {
                                        if (docu.exists()) {
                                            db.collection("cart").document(email).collection("item")
                                                .document(foodName).get().addOnSuccessListener {
                                                    sameName(
                                                        email,
                                                        resEmail,
                                                        food,
                                                        it.get("qty").toString().toInt()
                                                    )
                                                }
                                        }else{
                                            diffName(email, resEmail, food)
                                        }
                                    }
                                }
                            }

                    }
                }

            }else{
                Toast.makeText(
                    this,
                    "Something went wrong,please try again3",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun sameName(
        email: String,
        resEmail: String,
        food: cusFoodList,
        itemQty:Int
    ) {
        var qty = binding.prnumber
        var qtyNum = qty.text.toString().toInt()
        var foodName = food.foodName
        var foodPrice = food.foodPrice


        var cartQty = qtyNum + itemQty
        val cart: MutableMap<String, Any> =
            java.util.HashMap()
        cart["restaurantEmail"] = resEmail.toString()
        cart["foodName"] = foodName.toString()
        cart["qty"] = cartQty.toInt()
        cart["foodLink"] = food.foodLink.toString()
        cart["foodDesc"] = food.foodDesc.toString()
        if (foodPrice != null) {
            cart["foodPrice"] = foodPrice.toDouble()
        }
        if (email != null) {
            db.collection("cart").document(email)
                .collection("item")
                .document(foodName.toString()).set(cart, SetOptions.merge())
            db.collection("cart").document(email).set(
                hashMapOf(
                    "key" to 1
                )
            )
            Toast.makeText(
                this@cus_showFoodDetail,
                "Cart added successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun diffName(email: String, resEmail: String, food: cusFoodList) {
        var qty = binding.prnumber
        var qtyNum = qty.text.toString().toInt()
        var foodName = food.foodName
        var foodPrice = food.foodPrice

        var cartQty = qtyNum
        val cart: MutableMap<String, Any> =
            java.util.HashMap()
        cart["restaurantEmail"] = resEmail.toString()
        cart["foodName"] = foodName.toString()
        cart["qty"] = cartQty.toInt()
        cart["foodLink"] = food.foodLink.toString()
        cart["foodDesc"] = food.foodDesc.toString()
        if (foodPrice != null) {
            cart["foodPrice"] = foodPrice.toDouble()
        }
        if (email != null) {
            db.collection("cart").document(email)
                .collection("item")
                .document(foodName.toString()).set(cart, SetOptions.merge())
            db.collection("cart").document(email).set(
                hashMapOf(
                    "key" to 1
                )
            )
            Toast.makeText(
                this@cus_showFoodDetail,
                "Cart added successfully",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cart, menu)
        return true
    }
}