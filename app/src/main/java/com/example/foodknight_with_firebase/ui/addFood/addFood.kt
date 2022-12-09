package com.example.foodknight_with_firebase.ui.addFood

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import io.reactivex.Observable

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.databinding.FragmentAddFoodBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jakewharton.rxbinding2.widget.RxTextView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var  db:FirebaseFirestore
var minteger = 0
/**
 * A simple [Fragment] subclass.
 * Use the [addFood.newInstance] factory method to
 * create an instance of this fragment.
 */
class addFood : Fragment() {
    // TODO: Rename and change types of parameters
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    lateinit var binding: FragmentAddFoodBinding
    lateinit var imageURI: Uri
    var isCameraSelected = false
    var imageUri: Uri? = null
    var bitmap: Bitmap? = null

    private   lateinit var remoteUrl: String
    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val galleryViewModel =
            ViewModelProvider(this).get(AddFoodViewModel::class.java)
        imageURI = "null".toUri()
        binding = FragmentAddFoodBinding.inflate(inflater, container, false)
        val root: View = binding.root


        binding.btnAdd.setOnClickListener {
            cameraCheckPermission()
            galleryCheckPermission()
            val pictureDialog = this.context?.let { it1 -> AlertDialog.Builder(it1) }
            pictureDialog?.setTitle("Select Action")
            val pictureDialogItem = arrayOf("Select photo from Gallery",
                "Capture photo from Camera")
            pictureDialog?.setItems(pictureDialogItem) { dialog, which ->

                when (which) {
                    0 ->  selectImage()
                    1 -> camera()
                }
            }

            if (pictureDialog != null) {
                pictureDialog.show()
            }
        }

        binding.btnUpload.setOnClickListener {

            uploadImage()

        }
        binding.btnPlus.setOnClickListener{
            minteger =  binding.txtQty.text.toString().toInt()
            minteger += 1;
            display(minteger);
        }
        binding.btnMinus.setOnClickListener {
            minteger =  binding.txtQty.text.toString().toInt()
            if (minteger > 0) {

                minteger -= 1;
                display(minteger);
            }
        }
        val nameStream = RxTextView.textChanges(binding.txtFoodName)
            .skipInitialValue()
            .map { name ->
                name.isEmpty()
            }
        nameStream.subscribe {
            showNameExistAlert(it)
        }

        val qtyStream = RxTextView.textChanges(binding.txtQty)
            .skipInitialValue()
            .map { qty ->
                qty.isEmpty()
            }
        qtyStream.subscribe {
            showQtyExistAlert(it)
        }

        val descStream = RxTextView.textChanges(binding.txtDesc)
            .skipInitialValue()
            .map { desc ->
                desc.isEmpty()
            }
        descStream.subscribe {
            showDescExistAlert(it)
        }
        val priceStream = RxTextView.textChanges(binding.txtPrice)
            .skipInitialValue()
            .map { price ->
                price.isEmpty()
            }
        priceStream.subscribe {
            showPriceExistAlert(it)
        }





        val invalidFieldsStream = Observable.combineLatest(
            nameStream,
            qtyStream,
            descStream,
            priceStream,

            { nameInvalid: Boolean,qtyStream:Boolean,descStream: Boolean,priceStream: Boolean->
                !nameInvalid && !qtyStream && !descStream && !priceStream
            })
        invalidFieldsStream.subscribe { isValid ->
            if (isValid) {
                binding.btnUpload.isEnabled = true
                binding.btnUpload.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_color)
            } else {
                binding.btnUpload.isEnabled = false
                binding.btnUpload.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            }

        }




        return root
    }

    private fun showPriceExistAlert(isNotValid: Boolean) {
        binding.txtPrice.error = if (isNotValid) "Please enter your Price!" else null
    }

    private fun showDescExistAlert(isNotValid: Boolean) {
        binding.txtDesc.error = if (isNotValid) "Please enter your Description!" else null
    }

    private fun showQtyExistAlert(isNotValid: Boolean) {
        binding.txtQty.error = if (isNotValid) "Please enter your Quantity!" else null
    }

    private fun showNameExistAlert(isNotValid: Boolean) {
        binding.txtFoodName.error = if (isNotValid) "Please enter your name!" else null
    }


    private fun display(minteger: Int) {
        var displayInteger = binding.txtQty
        displayInteger.setText("" + minteger)
    }

    lateinit var aa :ProgressDialog
    @RequiresApi(Build.VERSION_CODES.N)

    private fun uploadImage() {

        val progressDialog = ProgressDialog(activity);
        progressDialog.setMessage("Uploading file..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val imageIdentifier = UUID.randomUUID().toString() + ".png";
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images\$fileName")
            .child(imageIdentifier)


        if (imageURI != "null".toUri()) {


            val uploadTask = storageReference.putFile(imageURI).addOnSuccessListener {

                binding.foodPic.setImageURI(imageURI)
                val downloadUrl = storageReference.downloadUrl

                downloadUrl.addOnSuccessListener {
                    var remoteUrl = it.toString()
                    // update our Cloud Firestore with the public image URI.
                    saveData(remoteUrl)
                }
                Toast.makeText(activity, "Succesfuly Uploaded", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }.addOnFailureListener {
                progressDialog.dismiss()

                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()

            }
        } else if(bitmap != null) {
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            val path: String = MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver, bitmap, "Title", null
            )
            imageURI = Uri.parse(path);
            val uploadTask = storageReference.putFile(imageURI).addOnSuccessListener {

                binding.foodPic.setImageURI(imageURI)
                val downloadUrl = storageReference.downloadUrl

                downloadUrl.addOnSuccessListener {
                    var remoteUrl = it.toString()
                    // update our Cloud Firestore with the public image URI.
                    saveData(remoteUrl)
                }
                Toast.makeText(activity, "Succesfuly Uploaded", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }.addOnFailureListener {
                progressDialog.dismiss()

                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()

            }
        }
        else{
            var remoteUrl = "https://media.istockphoto.com/vectors/geometric-banner-megaphone-with-coming-soon-bubble-loudspeaker-modern-vector-id1181378326?k=20&m=1181378326&s=612x612&w=0&h=FUstjwTm6ZOYSHkusiHSsPHUV7kSGDnmRF18QDy-AO8="
            saveData(remoteUrl)
            progressDialog.dismiss()}


    }



    private fun saveData(remoteUrl: String) {
        val foodName = binding.txtFoodName.text.toString()
        val foodPrice = binding.txtPrice.text.toString()
        val foodDesc = binding.txtDesc.text.toString()
        val foodQty = binding.txtQty.text.toString()
        val db = FirebaseFirestore.getInstance()

        val progressDialog =  ProgressDialog(activity);
        progressDialog.setMessage("Uploading file..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email
        val food: MutableMap<String, Any> = HashMap()
        food["foodDesc"] = foodDesc
        food["foodLink"] = remoteUrl
        food["foodName"] = foodName
        food["foodPrice"] = foodPrice
        food["foodQty"] = foodQty



        if (email != null) {
            db.collection("seller").document(email).collection("foodList").document(foodName).set(food)
                .
                addOnSuccessListener {
                    Toast.makeText(activity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }.
                addOnFailureListener {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()

                }
        }

    }


    private fun cameraCheckPermission() {

        Dexter.withContext(this.context)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {

                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        p1: PermissionToken?
                    ) {

                    }

                }
            ).onSameThread().check()
    }
    private fun galleryCheckPermission() {

        Dexter.withContext(this.context).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {

            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    activity,
                    "You have denied the storage permission to select image",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {

            }
        }).onSameThread().check()
    }
    private fun camera() {
        imageURI = "null".toUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }
    private fun selectImage() {
        bitmap = null
        val intent = Intent();
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {
                    bitmap = data?.extras?.get("data") as Bitmap
                    binding.foodPic.setImageBitmap(bitmap)
                }

                GALLERY_REQUEST_CODE -> {

                    imageURI = data?.data!!
                    binding.foodPic.setImageURI(imageURI)}

            }

        }

    }

}