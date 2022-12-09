package com.example.foodknight_with_firebase.ui.allFood

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.databinding.AllFoodFragmentBinding
import com.example.foodknight_with_firebase.ui.Foods
import com.example.foodknight_with_firebase.ui.MyAdapter
import com.example.foodknight_with_firebase.ui.addFood.AddFoodViewModel
import com.example.foodknight_with_firebase.ui.addFood.addFood
import com.example.foodknight_with_firebase.ui.editDel.editDel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class allFood : Fragment() {

    private lateinit var binding: AllFoodFragmentBinding
    private  lateinit var db: FirebaseFirestore
    private  lateinit var recyclerView :RecyclerView
    private  lateinit var foodArrayList : ArrayList<Foods>
    private  lateinit var tempFoodArrayList : ArrayList<Foods>
    private lateinit var myAdapter: MyAdapter
    private val AllFoodViewModel : AllFoodViewModel by activityViewModels()
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {





        val galleryViewModel =
            ViewModelProvider(this).get(AddFoodViewModel::class.java)
        //passing fragments data

        val view = inflater.inflate(R.layout.all_food_fragment,container,false)




        binding = AllFoodFragmentBinding.inflate(inflater, container, false)

        val root: View = binding.root
        recyclerView = binding.recycleFood
        recyclerView.layoutManager =LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        foodArrayList = arrayListOf()
        tempFoodArrayList = arrayListOf()


        var adapter = MyAdapter(tempFoodArrayList)
        myAdapter = adapter
        recyclerView.adapter = myAdapter


        adapter.setOnItemClickListener(object : MyAdapter.onItemClickListener{


            //override fun onItemClick(position: Int) {
            // Toast.makeText(activity, "You Clicked item no . $position", Toast.LENGTH_SHORT).show()

            //AllFoodViewModel.foodDesc(foodArrayList[position].foodDesc.toString())
            // AllFoodViewModel.foodLink(foodArrayList[position].foodLink.toString())
            // AllFoodViewModel.foodName(foodArrayList[position].foodName.toString())
            //  AllFoodViewModel.foodPrice(foodArrayList[position].foodPrice.toString())
            //AllFoodViewModel.foodQty(foodArrayList[position].foodQty.toString())
            //findNavController().navigate(R.id.action_allFood_to_editDel)

            //var bundle = Bundle()
            //bundle.putString("foodDesc",foodArrayList[position].foodDesc)
            // bundle.putString("foodLink",foodArrayList[position].foodLink)
            //bundle.putString("foodName",foodArrayList[position].foodName)
            // bundle.putString("foodPrice",foodArrayList[position].foodPrice)

            //val fragment =editDel()
            //fragment.arguments = bundle
            // fragmentManager?.beginTransaction()?.replace(R.id.lay,fragment)?.commit()

            //}

            override fun onButtonClick(position: Int) {
                AllFoodViewModel.foodDesc(foodArrayList[position].foodDesc.toString())
                AllFoodViewModel.foodLink(foodArrayList[position].foodLink.toString())
                AllFoodViewModel.foodName(foodArrayList[position].foodName.toString())
                AllFoodViewModel.foodPrice(foodArrayList[position].foodPrice.toString())
                AllFoodViewModel.foodQty(foodArrayList[position].foodQty.toString())
                findNavController().navigate(R.id.action_allFood_to_editDel)
            }


        })
        EventChangeListener()

        //Search view
        val searchView = binding.searchView as SearchView


        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                tempFoodArrayList.clear()
                val searchText = newText!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    foodArrayList.forEach{
                        if(it.foodName?.toLowerCase(Locale.getDefault())?.contains(searchText) == true){
                            tempFoodArrayList.add(it)
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                    myAdapter.notifyDataSetChanged()

                }else {

                    tempFoodArrayList.clear()
                    tempFoodArrayList.addAll(foodArrayList)
                    recyclerView.adapter?.notifyDataSetChanged()
                    myAdapter.notifyDataSetChanged()
                }
                return false
            }
        } )
        myAdapter.notifyDataSetChanged()
        return root
    }



    private fun EventChangeListener() {

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("seller").document(email).collection("foodList").
            addSnapshotListener(object : EventListener<QuerySnapshot>{
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
                    myAdapter.notifyDataSetChanged()
                }


            })
        }



    }




}

