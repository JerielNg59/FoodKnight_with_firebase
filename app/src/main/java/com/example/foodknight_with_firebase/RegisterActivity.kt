package com.example.foodknight_with_firebase

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.example.foodknight_with_firebase.databinding.ActivityLoginBinding
import com.example.foodknight_with_firebase.databinding.ActivityRegisterBinding
import com.google.firebase.firestore.SetOptions.merge
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@SuppressLint("CheckResult")
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db:FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Name Validation
        val nameStream = RxTextView.textChanges(binding.etFullname)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe {
            showNameExistAlert(it)
        }
        //Email Validation
        val emailStream = RxTextView.textChanges(binding.etEmail)
            .skipInitialValue()
            .map { email ->
                !Patterns.EMAIL_ADDRESS.matcher(email).matches()
            }
        emailStream.subscribe {
            showEmailValidAlert(it)
        }
        //UserName Validation
        val usernameStream = RxTextView.textChanges(binding.etUsername)
            .skipInitialValue()
            .map { username ->
                username.length < 4
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

        //Confirm Password Validation
        val passwordConfirmStream = Observable.merge(
            RxTextView.textChanges(binding.etPassword)
                .skipInitialValue()
                .map { password ->
                    password.toString() != binding.etPassword.text.toString()

                },
            RxTextView.textChanges(binding.etConfirmPassword)
                .skipInitialValue()
                .map { confirmPassword ->
                    confirmPassword.toString() != binding.etPassword.text.toString()
                })
        passwordConfirmStream.subscribe() {
            showPasswordConfirmAlert(it)
        }

//            //role validation
//         val roleStream = RxTextView.textChanges(binding.etRole)
//            .skipInitialValue()
//            .map { role ->
//                role.toString() != "Merchant" && role.toString() != "Customer"
//            }
//        roleStream.subscribe {
//            showTextMinimalAlert(it, "Role")
//        }

        // phone number Validation
        val hpnoStream = RxTextView.textChanges(binding.etHpno)
            .skipInitialValue()
            .map { hpno ->
                hpno.isEmpty() || hpno.length!=10 && hpno.length!=11
            }
        hpnoStream.subscribe {
            showTextMinimalAlert(it, "Hpno")
        }


        //button validation

        val invalidFieldsStream = Observable.combineLatest(
            nameStream,
            emailStream,
            usernameStream,
            passwordStream,
            passwordConfirmStream,
            hpnoStream,
            { nameInvalid: Boolean, emailInvalid: Boolean, usernameInvalid: Boolean, passwordInvalid: Boolean, passwordConfirmInvalid: Boolean ,hpnoInvalid: Boolean->
                !nameInvalid && !emailInvalid && !usernameInvalid && !passwordInvalid && !passwordConfirmInvalid && !hpnoInvalid
            })
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnRegister.isEnabled = true
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btnRegister.isEnabled = false
                binding.btnRegister.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

        }




//LISTENER
        binding.btnRegister.setOnClickListener {
           val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            registerUser(email,password)
        }
        binding.tvHaveAccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }

    }

    private fun showNameExistAlert(isNotValid: Boolean) {
        binding.etFullname.error = if (isNotValid) "Please enter your name!" else null
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "UserName")
            binding.etUsername.error = if (isNotValid) "$text must more than 4 letters!" else null
        else if (text == "Password") {
            binding.etPassword.error = if (isNotValid) "$text must more than 8 letters!" else null
        }
        else if (text == "Hpno"){
            binding.etHpno.error = if (isNotValid) "Phone number must be 10 or 11 digit only without country code" else null
        }
    }

    private fun showEmailValidAlert(isNotValid: Boolean) {
        binding.etEmail.error = if (isNotValid) "Please enter a valid email!" else null
    }

    private fun showPasswordConfirmAlert(isNotValid: Boolean) {
        binding.etConfirmPassword.error =
            if (isNotValid) "The confirmation password should be same as your password" else null
    }

    private fun uploadToFireStore(){
        val fullname1 = binding.etFullname.text.toString()
        val mail1 = binding.etEmail.text.toString()
        val username1 = binding.etUsername.text.toString()
        val password1 = binding.etPassword.text.toString()
        val hpno1 = binding.etHpno.text.toString()
        val db = FirebaseFirestore.getInstance()
        val progressDialog =  ProgressDialog(this);

        progressDialog.setMessage("Registrating..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val user: MutableMap<String, Any> = java.util.HashMap()
        user["fullname"] =fullname1
        user["email"] = mail1
        user["username"] = username1
        user["password"] = password1
        user["hpno"] = hpno1

        val seller: MutableMap<String, Any> = java.util.HashMap()
        db.collection("seller").document(mail1).set(seller)

        db.collection("user").document(mail1).set(user).
            addOnSuccessListener {
                Toast.makeText(this, "Successfully Registered", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }.
            addOnFailureListener {
                Toast.makeText(this, "Failed To Register", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }

    }

    private fun registerUser(email:String,password:String){
        auth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(this){
            if (it.isSuccessful){
                if(it.isSuccessful){
                    uploadToFireStore()
                    startActivity(Intent(this,LoginActivity::class.java))
                    Toast.makeText(this,"Registered Successfully",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




}