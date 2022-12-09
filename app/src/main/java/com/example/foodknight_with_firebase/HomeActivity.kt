package com.example.foodknight_with_firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foodknight_with_firebase.databinding.ActivityHomeBinding
import com.example.foodknight_with_firebase.databinding.FragmentHomeCustBinding
import com.example.foodknight_with_firebase.ui.home.HomeFragment
import com.example.foodknight_with_firebase.ui.home_cust.HomeFragment_Cust
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//Auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        binding.btnLogout.setOnClickListener {
            auth.signOut()
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                Toast.makeText(this, "Logout Successful!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnCustomer.setOnClickListener {
            startActivity(Intent(this, CustomerDrawerActivity::class.java))
        }







        binding.btnMerchant.setOnClickListener {
            //startActivity(Intent(this, SellerRegistrationActivity::class.java))

            tosellerpage()

        }

    }

    private fun tosellerpage(){
        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email.toString()

        db.collection("restaurant").document(email).get()
            .addOnSuccessListener { it ->
                if (it != null){
                    binding.sellername.text= it.get("shopname").toString()
                    test()
                }
            }

    }

    private fun test(){

        val text = binding.sellername.text
        if (text!="null") {
            startActivity(Intent(this, SellerDrawerActivity::class.java))

        } else {
            startActivity(Intent(this, SellerRegistrationActivity::class.java))
        }
    }
}