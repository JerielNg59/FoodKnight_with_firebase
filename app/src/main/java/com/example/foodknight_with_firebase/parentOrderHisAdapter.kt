package com.example.foodknight_with_firebase

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase


class parentOrderHisAdapter(private val parentList: ArrayList<orderNum>,ctx:Context) :
    RecyclerView.Adapter<parentOrderHisAdapter.MyViewHolder>() {

    var ctx:Context = ctx

    //var onItemClick:((restaurant)->Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.fullhistory, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem: orderNum = parentList[position]


        lateinit var db : FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email


        holder.orderNumer.text = "Order ID : " +currentItem.orderID.toString()


        if (email != null) {
            db.collection("history").document(email).collection("order")
                .document(currentItem.orderID.toString()).get().addOnSuccessListener {
                    db.collection("foodOrder").document(it.get("restaurantEmail").toString()).
                    collection("order").document(currentItem.orderID.toString()).collection("item").get()
                        .addOnSuccessListener {
                            var size = it.size()
                            var preparedNum:Int = 0
                            var completedNum:Int = 0
                            holder.orderStatus.text = "Status : " + "New"
                            for(doc in it){

                                if(doc.get("foodStatus").toString()=="Preparing"){
                                    db.collection("history").document(email).
                                    collection("order").document(currentItem.orderID.toString()).set(
                                        hashMapOf(
                                            "foodStatus" to "Preparing"
                                        )
                                    )
                                    holder.orderStatus.text = "Status : " + "Preparing"
                                }else if(doc.get("foodStatus").toString()=="Prepared"){
                                    db.collection("history").document(email).
                                    collection("order").document(currentItem.orderID.toString()).set(
                                        hashMapOf(
                                            "foodStatus" to "Preparing"
                                        )
                                    )
                                    holder.orderStatus.text = "Status : " + "Preparing"
                                    preparedNum++
                                }else if(doc.get("foodStatus").toString()=="Completed"){
                                    db.collection("history").document(email).
                                    collection("order").document(currentItem.orderID.toString()).set(
                                        hashMapOf(
                                            "foodStatus" to "Preparing"
                                        )
                                    )
                                    holder.orderStatus.text = "Status : " + "Preparing"
                                    completedNum++
                                }
                            }
                            if(preparedNum==size&&preparedNum!=0){
                                db.collection("history").document(email).
                                collection("order").document(currentItem.orderID.toString()).set(
                                    hashMapOf(
                                        "foodStatus" to "Prepared"
                                    )
                                )
                                holder.orderStatus.text = "Status : " + "Prepared"
                            }else if(completedNum==size&&completedNum!=0){
                                db.collection("history").document(email).
                                collection("order").document(currentItem.orderID.toString()).set(
                                    hashMapOf(
                                        "foodStatus" to "Completed"
                                    )
                                )
                                holder.orderStatus.text = "Status : " + "Completed"
                            }
                        }

                }
        }

        test(holder,currentItem)

//        holder.itemView.setOnClickListener{
//            onItemClick?.invoke(currentItem)
//        }
    }

    private fun test(holder: MyViewHolder,currentItem:orderNum){

        lateinit var db : FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        var hisArrayList :ArrayList<history>

        hisArrayList = arrayListOf()
        var hisAdapter2 :orderHisAdapter= orderHisAdapter(hisArrayList)

        holder.hisRV.layoutManager = LinearLayoutManager(ctx)
        holder.hisRV.setHasFixedSize(true)


        if (email != null) {
            db.collection("history").document(email).collection("order").
            document(currentItem.orderID.toString()).collection("item").addSnapshotListener(object :
                EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        Log.e("firestore Error", error.message.toString())
                        return
                    }

                    for (dc: DocumentChange in value?.documentChanges!!) {
                        if (dc.type == DocumentChange.Type.ADDED) {
                            var item: history = dc.document.toObject(history::class.java)
                            hisArrayList.add(item)
                        }
                    }
                    holder.hisRV.adapter = hisAdapter2
                    hisAdapter2.notifyDataSetChanged()
                }

            })
        }
    }

    override fun getItemCount(): Int {
        return parentList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var orderNumer: TextView = itemView.findViewById(R.id.orderId)
        var orderStatus: TextView = itemView.findViewById(R.id.orderStatus)

        var hisRV:RecyclerView = itemView.findViewById(R.id.history)
    }


}