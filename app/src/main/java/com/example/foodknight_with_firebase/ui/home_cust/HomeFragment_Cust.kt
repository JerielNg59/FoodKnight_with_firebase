package com.example.foodknight_with_firebase.ui.home_cust

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodknight_with_firebase.MyAdapter2
import com.example.foodknight_with_firebase.databinding.FragmentHomeCustBinding
import com.example.foodknight_with_firebase.resShowFood
import com.example.foodknight_with_firebase.restaurant
import com.google.firebase.firestore.*

class HomeFragment_Cust : Fragment() {

    private lateinit var homeViewModelCust: HomeViewModel_Cust
    private lateinit var binding: FragmentHomeCustBinding

    private lateinit var db : FirebaseFirestore
    private lateinit var restaurantRV : RecyclerView
    private lateinit var restaurantArrayList :ArrayList<restaurant>
    private lateinit var myAdapter2: MyAdapter2

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModelCust =
            ViewModelProvider(this).get(HomeViewModel_Cust::class.java)

        binding = FragmentHomeCustBinding.inflate(inflater, container, false)
        val root: View = binding.root

        restaurantRV = binding.restaurantList
        restaurantRV.layoutManager = LinearLayoutManager(activity)
        restaurantRV.setHasFixedSize(true)

        restaurantArrayList = arrayListOf()
        myAdapter2 = MyAdapter2(restaurantArrayList)
        restaurantRV.adapter=myAdapter2

        getResData()

        myAdapter2.onItemClick={
            val intent = Intent(requireContext(),resShowFood::class.java)
            intent.putExtra("res",it)
            startActivity(intent)
        }

        return root
    }

    private fun getResData() {
        db = FirebaseFirestore.getInstance()
        db.collection("restaurant").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("firestore Error", error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        var item:restaurant = dc.document.toObject(restaurant::class.java)
                        item.uid = dc.document.id
                        restaurantArrayList.add(item)

                    }
                }
                myAdapter2.notifyDataSetChanged()

            }
        })
    }

}