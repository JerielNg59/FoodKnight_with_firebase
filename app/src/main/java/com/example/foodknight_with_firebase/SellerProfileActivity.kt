package com.example.foodknight_with_firebase

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.foodknight_with_firebase.databinding.ActivitySellerProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

open class SellerProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivitySellerProfileBinding
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show()
        binding.btnEditProfile.isEnabled = false
        binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        //auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        binding.btnStartEditProfile.isEnabled = true
        //UserName Validation
        val usernameStream = RxTextView.textChanges(binding.etUsername)
            .skipInitialValue()
            .map { username ->
                username.length < 4 || username.isEmpty()
            }
        usernameStream.subscribe {
            showTextMinimalAlert(it, "Username")
        }

        //Password Validation
        val passwordStream = RxTextView.textChanges(binding.etPassword)
            .skipInitialValue()
            .map { password ->
                password.length < 8
            }
        passwordStream.subscribe {
            showTextMinimalAlert(it, "Password")
        }

        //hpno validation
        val hpnoStream = RxTextView.textChanges(binding.etHpno)
            .skipInitialValue()
            .map { hpno ->
                hpno.isEmpty() || hpno.length!=10 && hpno.length!=11
            }
        hpnoStream.subscribe {
            showTextMinimalAlert(it, "Hpno")
        }

        val shopNameStream = RxTextView.textChanges(binding.etShopName)
            .skipInitialValue()
            .map { shopname ->
                shopname.isEmpty()
            }
        shopNameStream.subscribe {
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


        var firsttimeload =1
        //button validation
        var infocorrect = false
        val invalidFieldsStream = Observable.combineLatest(
            usernameStream,
            passwordStream,
            hpnoStream,
            shopNameStream,
            addressStream
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean, hpnoInvalid: Boolean, shopNameInvalid: Boolean, addressInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid && !hpnoInvalid && !shopNameInvalid && !addressInvalid
        }
        invalidFieldsStream.subscribe{ isValid ->
            if (isValid) {
                infocorrect=true
                if(firsttimeload !=1){
                    Toast.makeText(this, firsttimeload, Toast.LENGTH_SHORT).show()
                    binding.btnEditProfile.isEnabled = true
                    binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
                    firsttimeload+=1
                    Toast.makeText(this, firsttimeload, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show()
                    binding.btnEditProfile.isEnabled = false
                    binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
                }


            } else {
                infocorrect=false
                binding.btnEditProfile.isEnabled = false
                binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

        }
        //get file from firebase
        val db = FirebaseFirestore.getInstance()
        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email

        binding.etEmail.setText(email.toString())
        db.collection("user").document(loginnedUser?.email.toString()).get().addOnSuccessListener {it->
            if(it!= null){

                binding.etFullname.setText(it.get("fullname").toString())
                binding.etHpno.setText(it.get("hpno").toString())
                binding.etPassword.setText(it.get("password").toString())
                binding.etUsername.setText(it.get("username").toString())
            }
        }
        db.collection("restaurant").document(loginnedUser?.email.toString()).get().addOnSuccessListener {it->
            if(it!= null){
                binding.etShopAddress.setText(it.get("shopaddress").toString())
                binding.etShopName.setText(it.get("shopname").toString())
            }
        }


        binding.tvProfileEditPassword.setOnClickListener{
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        binding.btnEditProfile.setOnClickListener {
            updateToDatabase()

        }

        binding.btnStartEditProfile.setOnClickListener{

               if(!binding.etUsername.isEnabled){
                   binding.etUsername.isEnabled = true
                   binding.tilHpno.isEnabled =true
                   binding.tilShopName.isEnabled = true
                   binding.tilShopAddress.isEnabled =true
                   if(!infocorrect){
                       binding.btnEditProfile.isEnabled = true
                       binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
                   }else{
                       binding.btnEditProfile.isEnabled = false
                       binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
                   }

               }else{
                   binding.etUsername.isEnabled =false
                   binding.tilUsername.isFocusable = false
                   binding.tilHpno.isEnabled =false
                   binding.tilShopName.isEnabled = false
                   binding.tilShopAddress.isEnabled = false
                   binding.btnEditProfile.isEnabled = false
                   binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
               }

        }

        Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show()
        binding.btnEditProfile.isEnabled = false
        binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)


    }

//    @Override
//    protected override fun onStart(){
//        super.onStart()
//        binding = ActivitySellerProfileBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        binding.btnEditProfile.isEnabled = false
//        binding.btnEditProfile.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
//
//    }

    private fun updateToDatabase(){
        val db = FirebaseFirestore.getInstance()
        val shopname1 = binding.etShopName.text.toString()
        val shopaddress1 = binding.etShopAddress.text.toString()
        val hpno1 = binding.etHpno.text.toString()
        val username1 = binding.etUsername.text.toString()
        val fullname1 = binding.etFullname.text.toString()
        val password1 = binding.etPassword.text.toString()
        val loginnedUser = Firebase.auth.currentUser
        val mail1 = loginnedUser?.email.toString()
        val progressDialog =  ProgressDialog(this);
        progressDialog.setMessage("Updating..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val seller: MutableMap<String, Any> = java.util.HashMap()
        seller["shopaddress"] = shopaddress1
        seller["shopname"] = shopname1

        val user: MutableMap<String, Any> = java.util.HashMap()

        user["email"] = mail1
        user["fullname"] = fullname1
        user["hpno"] = hpno1
        user["password"] =password1
        user["username"] = username1

        db.collection("restaurant").document(mail1).set(seller)
            .addOnSuccessListener {
                Toast.makeText(this, "Your Profile is Successfully Updated", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }.
            addOnFailureListener {
                Toast.makeText(this, "Failed to update1", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }

        db.collection("user").document(mail1).set(user)

        startActivity(Intent(this, SellerDrawerActivity::class.java))
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "UserName")
            binding.etUsername.error = if (isNotValid) "$text must more than 4 letters!" else null
        else if (text == "Password") {
            binding.etPassword.error = if (isNotValid) "$text must more than 8 letters!" else null
        } else if (text == "Hpno") {
            binding.etHpno.error =
                if (isNotValid) "Phone number must be 10 or 11 digit only without country code" else null
        }
    }

    private fun showNameExistAlert(isNotValid: Boolean) {
        binding.etShopName.error = if (isNotValid) "Please enter your shop name!" else null
    }
    private fun showAddressExistAlert(isNotValid: Boolean) {
        binding.etShopAddress.error = if (isNotValid) "Please enter your shop address with more than 10 characters!" else null
    }
}