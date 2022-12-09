package com.example.foodknight_with_firebase

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.foodknight_with_firebase.databinding.ActivitySellerRegistrationBinding
import com.example.foodknight_with_firebase.databinding.ActivitySellerDrawerBinding.inflate
import com.example.foodknight_with_firebase.databinding.ActivityRegisterBinding.inflate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class SellerRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellerRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        // Name Validation
        val nameStream = RxTextView.textChanges(binding.etShopName)
            .skipInitialValue()
            .map { shopname ->
                shopname.isEmpty()
            }
        nameStream.subscribe {
            showNameExistAlert(it)
        }

        // Address Validation
        val addressStream = RxTextView.textChanges(binding.etShopAddress)
            .skipInitialValue()
            .map { shopaddress ->
                shopaddress.isEmpty() || shopaddress.length <10
            }
        addressStream.subscribe {
            showAddressExistAlert(it)
        }

        val invalidFieldsStream = Observable.combineLatest(
            nameStream,
            addressStream,
            { nameInvalid: Boolean, addressInvalid: Boolean->
                !nameInvalid && !addressInvalid
            })
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnMerchantRegister.isEnabled = true
                binding.btnMerchantRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btnMerchantRegister.isEnabled = false
                binding.btnMerchantRegister.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

        }
        binding.btnMerchantRegister.setOnClickListener {
          updateFirestore()
            startActivity(Intent(this, SellerDrawerActivity::class.java))

        }
//        Toast.makeText(this,"Registered Successfully", Toast.LENGTH_SHORT).show()

    }
    private fun updateFirestore(){
        val db = FirebaseFirestore.getInstance()
        val shopname1 = binding.etShopName.text.toString()
        val shopaddress1 = binding.etShopAddress.text.toString()
        val loginnedUser = Firebase.auth.currentUser
        val mail1 = loginnedUser?.email.toString()
        val progressDialog =  ProgressDialog(this);
        progressDialog.setMessage("Registrating..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val user: MutableMap<String, Any> = java.util.HashMap()
        user["shopname"] = shopname1
        user["shopaddress"] = shopaddress1

        db.collection("restaurant").document(mail1).set(user, SetOptions.merge())
        .addOnSuccessListener {
            Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }.
        addOnFailureListener {
            Toast.makeText(this, "Failed to Register", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()

        }

    }


    private fun showNameExistAlert(isNotValid: Boolean) {
        binding.etShopName.error = if (isNotValid) "Please enter your shop name!" else null
    }
    private fun showAddressExistAlert(isNotValid: Boolean) {
        binding.etShopAddress.error = if (isNotValid) "Please enter your shop address with more than 10 characters!" else null
    }
}