package com.example.foodknight_with_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.databinding.ActivityResShowFoodBinding
import com.google.firebase.firestore.*



class resShowFood : AppCompatActivity() {
    private  lateinit var binding: ActivityResShowFoodBinding

    private lateinit var db : FirebaseFirestore
    private lateinit var foodRV : RecyclerView
    private lateinit var foodArrayList :ArrayList<cusFoodList>
    private lateinit var showFoodAdapter2: showFoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResShowFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val res = intent.getParcelableExtra<restaurant>("res")

        db = FirebaseFirestore.getInstance()
        if(res!=null){

            var sellerEmail = res.uid
            foodRV = binding.cusShowFoodList
            foodRV.layoutManager = LinearLayoutManager(this)
            foodRV.setHasFixedSize(true)

            foodArrayList = arrayListOf()
            showFoodAdapter2 = showFoodAdapter(foodArrayList)
            foodRV.adapter= showFoodAdapter2

            getFoodData(sellerEmail)

            showFoodAdapter2.onItemClick={
                val intent = Intent(this,cus_showFoodDetail::class.java)
                intent.putExtra("foodInfo",it)
                startActivity(intent)
            }
            if (sellerEmail != null) {
                db.collection("restaurant").document(sellerEmail.toString()).get().addOnSuccessListener{
                    if(it!=null){
                        binding.resName.text = it.get("shopname").toString()
                        binding.resAdd.text = it.get("shopaddress").toString()

                    }else{
                        Toast.makeText(this, "Something went wrong,please try again later", Toast.LENGTH_SHORT).show()
                    }

                }.addOnFailureListener {
                    Toast.makeText(this, "Something went wrong,please try again later", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Something went wrong,please try again later", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.cart, menu)
        return true
    }

    private fun getFoodData(sellerEmail: String?) {
        db = FirebaseFirestore.getInstance()
        if (sellerEmail!= null) {
            db.collection("seller").document(sellerEmail).collection("foodList").
            addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
//                            var item:cusFoodList = dc.document.toObject(cusFoodList::class.java)
//                            foodArrayList.add(dc.document.toObject(cusFoodList::class.java))
//                            item.restaurantEmail = sellerEmail.toString()
//                            foodArrayList.add(item)
                            var item:cusFoodList = dc.document.toObject(cusFoodList::class.java)
                            item.uid = sellerEmail.toString()
                            foodArrayList.add(item)
                        }
                    }
                    showFoodAdapter2.notifyDataSetChanged()
                }

            })
        }else{
            Toast.makeText(this, "Something went wrong,please try again later", Toast.LENGTH_SHORT).show()
        }

    }
}