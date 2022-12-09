package com.example.foodknight_with_firebase

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.foodknight_with_firebase.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth:FirebaseAuth

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Email Validation
        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }
        val loginnedUser = Firebase.auth.currentUser
        val email1 = loginnedUser?.email
        if(!email1.isNullOrBlank()){
            binding.etEmail.setText(email1.toString())

        }
        var isMerchant = false

        db.collection("restaurant").document(loginnedUser?.email.toString()).get().addOnSuccessListener {it->
            if(it!= null){
                isMerchant = true
            }
        }


        //Reset Password
        binding.btnResetPw.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this) { reset ->
                    if (reset.isSuccessful) {
                        if(!email1.isNullOrBlank()){
                            if(isMerchant){
                                Toast.makeText(
                                    this,"Please check your email to reset the password",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, SellerDrawerActivity::class.java))
                            }else{
                                Toast.makeText(
                                    this,"Please check your email to reset the password",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, CustomerDrawerActivity::class.java))
                            }

                        }else{
                            Intent(this, LoginActivity::class.java).also {
                                it.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(it)
                                Toast.makeText(
                                    this,"Please check your email to reset the password",Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                        else{
                            Toast.makeText(this, reset.exception?.message, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
        //Click
        binding.tvBackLogin.setOnClickListener{
            if(!email1.isNullOrBlank()){
                if(isMerchant){
                    startActivity(Intent(this, SellerDrawerActivity::class.java))
                }else{
                    startActivity(Intent(this, CustomerDrawerActivity::class.java))
                }
            }else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }


    }

   private fun showEmailValidAlert(isNotValid: Boolean){
        if (isNotValid){
            binding.etEmail.error = "Email is not valid!"
            binding.btnResetPw.isEnabled = false
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
        } else{
            binding.etEmail.error = null
            binding.btnResetPw.isEnabled = true
            binding.btnResetPw.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
        }
    }

}