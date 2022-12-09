package com.example.foodknight_with_firebase.ui.sellerHome

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.annotation.UiThread
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.databinding.SellerHomeFragmentBinding
import com.example.foodknight_with_firebase.ui.Foods
import com.example.foodknight_with_firebase.ui.HomeAdapter
import com.example.foodknight_with_firebase.ui.allFood.AllFoodViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class sellerHome : Fragment() {
    lateinit var binding: SellerHomeFragmentBinding
    private  lateinit var db: FirebaseFirestore
    private  lateinit var recyclerView : RecyclerView
    private  lateinit var foodArrayList : ArrayList<Foods>
    private lateinit var HomeAdapter: HomeAdapter
    private  lateinit var tempFoodArrayList : ArrayList<Foods>
    private val AllFoodViewModel : AllFoodViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(SellerHomeViewModel::class.java)

        binding = SellerHomeFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var status = ""
        binding.txtAll.setText(Html.fromHtml("<u>All</u>"))
        recyclerView = binding.recycleFood
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)


        foodArrayList = arrayListOf()
        tempFoodArrayList = arrayListOf()
        var adapter = HomeAdapter(tempFoodArrayList)
        HomeAdapter = adapter
        recyclerView.adapter = HomeAdapter

        binding.txtAll.setOnClickListener {
            binding.txtAll.setText(Html.fromHtml("<u>All</u>"))
            binding.txtNew.setText(Html.fromHtml("New"))
            binding.txtPreparing.setText(Html.fromHtml("Preparing"))
            binding.txtPrepared.setText(Html.fromHtml("Prepared"))
            binding.txtCompleted.setText(Html.fromHtml("Completed"))


            tempFoodArrayList.clear()
            val searchText = ""!!.toLowerCase(Locale.getDefault())
            if(searchText.isNotEmpty()){
                foodArrayList.forEach{
                    if(it.foodStatus?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                        tempFoodArrayList.add(it)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }else {

                tempFoodArrayList.clear()
                tempFoodArrayList.addAll(foodArrayList)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        binding.txtNew.setOnClickListener{
            binding.txtAll.setText(Html.fromHtml("All"))
            binding.txtNew.setText(Html.fromHtml("<u>New<u>"))
            binding.txtPreparing.setText(Html.fromHtml("Preparing"))
            binding.txtPrepared.setText(Html.fromHtml("Prepared"))
            binding.txtCompleted.setText(Html.fromHtml("Completed"))

            tempFoodArrayList.clear()
            val searchText = "New"!!.toLowerCase(Locale.getDefault())
            if(searchText.isNotEmpty()){
                foodArrayList.forEach{
                    if(it.foodStatus?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                        tempFoodArrayList.add(it)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }else {

                tempFoodArrayList.clear()
                tempFoodArrayList.addAll(foodArrayList)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        binding.txtPreparing.setOnClickListener{
            binding.txtAll.setText(Html.fromHtml("All"))
            binding.txtNew.setText(Html.fromHtml("New"))
            binding.txtPreparing.setText(Html.fromHtml("<u>Preparing<u>"))
            binding.txtPrepared.setText(Html.fromHtml("Prepared"))
            binding.txtCompleted.setText(Html.fromHtml("Completed"))

            tempFoodArrayList.clear()
            val searchText = "Preparing"!!.toLowerCase(Locale.getDefault())
            if(searchText.isNotEmpty()){
                foodArrayList.forEach{
                    if(it.foodStatus?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                        tempFoodArrayList.add(it)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }else {

                tempFoodArrayList.clear()
                tempFoodArrayList.addAll(foodArrayList)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        binding.txtPrepared.setOnClickListener{
            binding.txtAll.setText(Html.fromHtml("All"))
            binding.txtNew.setText(Html.fromHtml("New"))
            binding.txtPreparing.setText(Html.fromHtml("Preparing"))
            binding.txtPrepared.setText(Html.fromHtml("<u>Prepared<u>"))
            binding.txtCompleted.setText(Html.fromHtml("Completed"))

            tempFoodArrayList.clear()
            val searchText = "Prepared"!!.toLowerCase(Locale.getDefault())
            if(searchText.isNotEmpty()){
                foodArrayList.forEach{
                    if(it.foodStatus?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                        tempFoodArrayList.add(it)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }else {

                tempFoodArrayList.clear()
                tempFoodArrayList.addAll(foodArrayList)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }

        binding.txtCompleted.setOnClickListener{
            binding.txtAll.setText(Html.fromHtml("All"))
            binding.txtNew.setText(Html.fromHtml("New"))
            binding.txtPreparing.setText(Html.fromHtml("Preparing"))
            binding.txtPrepared.setText(Html.fromHtml("Prepared"))
            binding.txtCompleted.setText(Html.fromHtml("<u>Completed<u>"))

            tempFoodArrayList.clear()
            val searchText = "Completed"!!.toLowerCase(Locale.getDefault())
            if(searchText.isNotEmpty()){
                foodArrayList.forEach{
                    if(it.foodStatus?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                        tempFoodArrayList.add(it)
                    }
                }
                recyclerView.adapter?.notifyDataSetChanged()
            }else {

                tempFoodArrayList.clear()
                tempFoodArrayList.addAll(foodArrayList)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }





        adapter.setOnItemClickListener(object :HomeAdapter.onItemClickListener{
            @UiThread
            @SuppressLint("NotifyDataSetChanged")
            override fun onButtonClick(position: Int) {
                if(foodArrayList[position].foodStatus != "Completed" ) {

                    if (foodArrayList[position].foodStatus != "Cancelled") {

                        val name = foodArrayList[position].foodName
                        val status = foodArrayList[position].foodStatus


                        updateStatus(name, status)

                        HomeAdapter.notifyItemChanged(position,status)
                        activity?.let { recreate(it) };
                    }
                }

            }

        })

        EventChangeListener()



        return root
    }

    @UiThread
    private fun updateStatus(name: String?, status: String?) {
        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email
        var state = ""
        val db = FirebaseFirestore.getInstance()

        if(status == "New") {
            state = "Preparing"
        }
        else if(status == "Preparing") {
            state = "Prepared"
        }
        else if(status ==  "Prepared") {
            state = "Completed"
        }
        else if(status ==  "Completed") {
            state = "Completed"
        }
        else if(status ==  "Cancelled") {
            state = "Cancelled"
        }

        val destination = email?.let {


            if (email != null) {
                db.collection("foodOrder").document(email).collection("order").get()

                    .addOnSuccessListener { it ->
                        for(doc in it){
                            if (email != null) {
                                if (name != null) {
                                    db.collection("foodOrder").document(email).collection("order").document(doc.id).collection("item").document(name).update(mapOf("foodStatus" to state))
                                }
                                recyclerView.adapter?.notifyDataSetChanged()
                                HomeAdapter.notifyDataSetChanged()

                            }

                        }
                    }
                    .addOnFailureListener { exception ->

                    }


            }

        }

    }




    private fun EventChangeListener() {

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("foodOrder").document(email).collection("order").get()

                .addOnSuccessListener { it ->
                    for(doc in it){
                        if (email != null) {
                            db.collection("foodOrder").document(email).collection("order").document(doc.id).
                            collection("item").
                            addSnapshotListener(object : EventListener<QuerySnapshot>
                            {
                                @SuppressLint("NotifyDataSetChanged")
                                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                                    if(error != null){
                                        Log.e("firestore Error", error.message.toString())
                                        return
                                    }

                                    for(dc : DocumentChange in value?.documentChanges!!){
                                        if(dc.type == DocumentChange.Type.ADDED){
                                            foodArrayList.add(dc.document.toObject(Foods::class.java))
                                            tempFoodArrayList.add(dc.document.toObject(Foods::class.java))
                                        }
                                    }
                                    HomeAdapter.notifyDataSetChanged()
                                }


                            })
                        }

                    }
                }
                .addOnFailureListener { exception ->

                }


        }



    }

}