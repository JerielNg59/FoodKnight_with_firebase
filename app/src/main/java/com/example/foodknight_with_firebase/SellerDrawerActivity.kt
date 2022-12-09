package com.example.foodknight_with_firebase
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.foodknight_with_firebase.databinding.ActivitySellerDrawerBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SellerDrawerActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySellerDrawerBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySellerDrawerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarSellerDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_content_seller_drawer)
        // Passing each menu ID as a set of Ids because each
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        val headerView: View = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.customer_username)
        val navUserEmail: TextView = headerView.findViewById(R.id.customer_email)
        val db = FirebaseFirestore.getInstance()
        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        db.collection("user").document(loginnedUser?.email.toString()).get().addOnSuccessListener {it->
            if(it!= null){
                navUsername.text = it.get("username").toString()
            }
        }
                navUserEmail.text = email.toString()

                // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.sellerHome, R.id.addFood, R.id.allFood, R.id.sellerProfileV2
            ), drawerLayout
        )
                setupActionBarWithNavController(navController, appBarConfiguration)
                navView.setupWithNavController(navController)


            }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.customer_drawer, menu)
            return true
        }

        override fun onSupportNavigateUp(): Boolean {
            val navController = findNavController(R.id.nav_host_fragment_content_seller_drawer)
            return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
        }
    }
