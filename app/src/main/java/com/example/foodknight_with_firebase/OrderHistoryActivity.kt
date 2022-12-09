package com.example.foodknight_with_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.databinding.ActivityOrderHistoryBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


class OrderHistoryActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var db : FirebaseFirestore
    private lateinit var hisRV : RecyclerView
    private lateinit var hisArrayList :ArrayList<orderNum>
    private lateinit var hisAdapter2: parentOrderHisAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hisRV = binding.cusOrderHis
        hisRV.layoutManager = LinearLayoutManager(this)
        hisRV.setHasFixedSize(true)

        hisArrayList = arrayListOf()
        hisAdapter2 = parentOrderHisAdapter(hisArrayList,this)
        hisRV.adapter= hisAdapter2

        getHisData()

    }

    private fun getHisData() {
        db = FirebaseFirestore.getInstance()

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        if (email != null) {
            db.collection("history").document(email).collection("order").orderBy(FieldPath.documentId(),Query.Direction.DESCENDING)
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                        if (error != null) {
                            Log.e("firestore Error", error.message.toString())
                            return
                        }

                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {

                                var item = orderNum(dc.document.id.toString())
                                hisArrayList.add(item)
                            }
                        }
                        hisAdapter2.notifyDataSetChanged()
                    }

                })
        }

    }


}