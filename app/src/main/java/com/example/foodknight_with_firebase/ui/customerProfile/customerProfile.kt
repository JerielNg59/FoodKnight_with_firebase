package com.example.foodknight_with_firebase.ui.customerProfile

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.databinding.CustomerProfileFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable

class customerProfile : Fragment() {
    lateinit var binding: CustomerProfileFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CustomerProfileFragmentBinding.inflate(inflater, container, false)

        val root: View = binding.root

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


        //button validation
        var firsttime= 1
        var infocorrect = false
        val invalidFieldsStream = Observable.combineLatest(
            usernameStream,
            passwordStream,
            hpnoStream
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean, hpnoInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid && !hpnoInvalid
        }
        invalidFieldsStream.subscribe{ isValid ->
            if (isValid) {
                infocorrect=true
                if(firsttime!=1){
                    binding.btnEditProfile.isEnabled = true
                    binding.btnEditProfile.backgroundTintList =
                        this.context?.let { ContextCompat.getColorStateList(it, R.color.primary_color) }
                }else{
                    binding.btnEditProfile.isEnabled = false
                    binding.btnEditProfile.backgroundTintList =
                        this.context?.let { ContextCompat.getColorStateList(it, android.R.color.darker_gray) }
                }
                firsttime=2
            } else {
                infocorrect=false
                binding.btnEditProfile.isEnabled = false
                binding.btnEditProfile.backgroundTintList =
                    this.context?.let { ContextCompat.getColorStateList(it, android.R.color.darker_gray) }
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


        binding.tvProfileEditPassword.setOnClickListener{
            //startActivity(Intent(this, ResetPasswordActivity::class.java))
            firsttime = 1
            findNavController().navigate(R.id.action_customerProfile2_to_resetPasswordActivity2)
        }

        binding.btnEditProfile.setOnClickListener {
            firsttime = 1
            updateToDatabase()
            findNavController().navigate(R.id.action_customerProfile2_to_homeFragment_Cust)

        }
        binding.btnEditProfile.isEnabled = true
        binding.btnEditProfile.backgroundTintList =
            this.context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.primary_color) }

        binding.btnStartEditProfile.setOnClickListener{

            if(!binding.etUsername.isEnabled){
                binding.etUsername.isEnabled = true
                binding.tilHpno.isEnabled =true
                if(!infocorrect){
                    binding.btnEditProfile.isEnabled = true
                    binding.btnEditProfile.backgroundTintList =
                        this.context?.let { it1 -> ContextCompat.getColorStateList(it1, R.color.primary_color) }
                }else{
                    binding.btnEditProfile.isEnabled = false
                    binding.btnEditProfile.backgroundTintList =
                        this.context?.let { it1 -> ContextCompat.getColorStateList(it1, android.R.color.darker_gray) }
                }

            }else{
                binding.etUsername.isEnabled =false
                binding.tilUsername.isFocusable = false
                binding.tilHpno.isEnabled =false
                binding.btnEditProfile.isEnabled = false
                binding.btnEditProfile.backgroundTintList =
                    this.context?.let { it1 -> ContextCompat.getColorStateList(it1, android.R.color.darker_gray) }
            }

        }

        return root
    }
    private fun updateToDatabase(){
        val db = FirebaseFirestore.getInstance()
        val hpno1 = binding.etHpno.text.toString()
        val username1 = binding.etUsername.text.toString()
        val fullname1 = binding.etFullname.text.toString()
        val password1 = binding.etPassword.text.toString()
        val loginnedUser = Firebase.auth.currentUser
        val mail1 = loginnedUser?.email.toString()
        val progressDialog =  ProgressDialog(activity);
        progressDialog.setMessage("Updating..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val user: MutableMap<String, Any> = java.util.HashMap()

        user["email"] = mail1
        user["fullname"] = fullname1
        user["hpno"] = hpno1
        user["password"] =password1
        user["username"] = username1

        db.collection("user").document(mail1).set(user) .addOnSuccessListener {
            Toast.makeText(activity, "Your Profile is Successfully Updated", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
        }.
        addOnFailureListener {
            Toast.makeText(activity, "Failed to update", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()

        }


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

}