package com.example.foodknight_with_firebase

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.foodknight_with_firebase.databinding.ActivityCartBinding
import com.example.foodknight_with_firebase.databinding.ActivityPaymentBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*

class payment : AppCompatActivity() {

    private  lateinit var binding:ActivityPaymentBinding

    private lateinit var db : FirebaseFirestore

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email
        db = FirebaseFirestore.getInstance()

        val cardnoStream = RxTextView.textChanges(binding.cardNum)
            .skipInitialValue()
            .map { cardnum ->
                cardnum.length != 16 || cardnum.isEmpty()
            }
        cardnoStream.subscribe {
            showTextMinimalAlert(it, "Cardno")
        }

        val cvvStream = RxTextView.textChanges(binding.cvv)
            .skipInitialValue()
            .map { cvv ->
                cvv.length != 3 || cvv.isEmpty()
            }
        cvvStream.subscribe {
            showTextMinimalAlert(it, "Cvv")
        }

        val cardmStream = RxTextView.textChanges(binding.cardM)
            .skipInitialValue()
            .map { cardm ->
                cardm.isEmpty() || cardm.length != 2
            }
        cardmStream.subscribe {
            showTextMinimalAlert(it, "CardM")
        }

        val cardyStream = RxTextView.textChanges(binding.cardY)
            .skipInitialValue()
            .map { cardy ->
                cardy.length != 2 || cardy.isEmpty()
            }
        cardyStream.subscribe {
            showTextMinimalAlert(it, "CardY")
        }

        //button validation

        val invalidFieldsStream = Observable.combineLatest(
            cardnoStream,
            cvvStream,
            cardmStream,
            cardyStream
        ) { cardnoInvalid: Boolean, cvvInvalid: Boolean, cardmInvalid: Boolean, cardyInvalid: Boolean ->
            !cardnoInvalid && !cvvInvalid && !cardmInvalid && !cardyInvalid
        }
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnPay.isEnabled = true
                binding.btnPay.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primary_color)
            } else {
                binding.btnPay.isEnabled = false
                binding.btnPay.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

        }

        binding.btnPay.setOnClickListener {
            if (email != null) {
                db.collection("cart").document(email).collection("item").get()
                    .addOnSuccessListener { it ->
                        val onformat = SimpleDateFormat("yyMMddhhmmss")
                        val format = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                        var date = format.format(Date())
                        var orderNum: String = onformat.format(Date()).toString()

                        for (doc in it) {
                            val history: MutableMap<String, Any> =
                                java.util.HashMap()
                            history["restaurantEmail"] = doc.get("restaurantEmail").toString()
                            history["foodName"] = doc.get("foodName").toString()
                            history["qty"] = doc.get("qty").toString()
                            history["price"] = doc.get("foodPrice").toString()
                            history["date"] = date.toString()
                            db.collection("history").document(email).collection("order")
                                .document(orderNum).collection("item")
                                .document(doc.get("foodName").toString())
                                .set(
                                    history,
                                    SetOptions.merge()
                                )
                            val history2: MutableMap<String, Any> =
                                java.util.HashMap()
                            history2["foodLink"] = doc.get("foodLink").toString()
                            history2["foodDesc"] = doc.get("foodDesc").toString()
                            history2["foodName"] = doc.get("foodName").toString()
                            history2["foodQty"] = doc.get("qty").toString()
                            history2["foodPrice"] = doc.get("foodPrice").toString()
                            history2["foodStatus"] = "New"

                            db.collection("foodOrder").document(doc.get("restaurantEmail").toString()).collection("order")
                                .document(orderNum).collection("item")
                                .document(doc.get("foodName").toString())
                                .set(
                                    history2,
                                    SetOptions.merge()
                                )

                            db.collection("history").document(email).
                            collection("order").document(orderNum).set(
                                hashMapOf(
                                    "foodStatus" to "New"
                                )
                            )

                            db.collection("history").document(email).set(
                                hashMapOf(
                                    "key" to 1
                                )
                            )

                            db.collection("foodOrder").document(doc.get("restaurantEmail").toString()).
                            collection("order").document(orderNum).set(
                                hashMapOf(
                                    "foodStatus" to "New"
                                )
                            )

                            db.collection("foodOrder").document(doc.get("restaurantEmail").toString()).set(
                                hashMapOf(
                                    "key" to 1
                                )
                            )
                        }
                        db.collection("cart").document(email).collection("item").get().addOnSuccessListener {
                            for(doc in it){
                                doc.reference .delete()
                            }
                        }
                        db.collection("cart").document(email).delete()

                        var intent = Intent(this,OrderHistoryActivity::class.java)
                        startActivity(intent)
                    }
            }
        }
        binding.cvv.filters = arrayOf<InputFilter>(MinMaxFilter(1,999))
        binding.cardM.filters = arrayOf<InputFilter>(MinMaxFilter(0,12))
        binding.cardY.filters = arrayOf<InputFilter>(MinMaxFilter(2,29))
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if (text == "Cardno")
            binding.cardNum.error = if (isNotValid) "Card No. must be exactly 16 number!" else null
        else if (text == "Cvv") {
            binding.cvv.error = if (isNotValid) "CVV must be exactly 3 digit " else null
        }
        else if (text == "CardM"){
            binding.cardM.error = if (isNotValid) "Card month must between 01 to 12 only" else null
        }
        else if (text == "CardY"){
            binding.cardY.error = if (isNotValid) "Card year must between 22 to 29 only" else null
        }
    }

    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }

}
}