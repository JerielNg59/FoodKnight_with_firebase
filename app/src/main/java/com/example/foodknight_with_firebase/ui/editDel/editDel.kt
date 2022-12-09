package com.example.foodknight_with_firebase.ui.editDel
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import com.example.foodknight_with_firebase.R
import com.example.foodknight_with_firebase.databinding.EditDelFragmentBinding
import com.example.foodknight_with_firebase.ui.allFood.AllFoodViewModel
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
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import java.io.ByteArrayOutputStream
import java.util.*

class editDel : Fragment() {
    lateinit var binding: EditDelFragmentBinding
    lateinit var remoteUrl: String
    lateinit var imageURI: Uri
    var bitmap: Bitmap? = null
    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2
    private val AllFoodViewModel : AllFoodViewModel by activityViewModels()
    var minteger = 0
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(EditDelViewModel::class.java)

        remoteUrl = "null"
        imageURI = "null".toUri()
        binding = EditDelFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        var id = ""

        // val foodName : TextView = binding.txtFoodName
        // val foodDesc : TextView = binding.txtDesc
        // val foodPrice : TextView = binding.txtPrice
        //val args = this.arguments
        // val foodLink = args?.get("foodLink").toString()
        // Picasso.get().load(foodLink).into(binding.foodPic)
        //val oo : TextView=  binding.txtFoodName
        AllFoodViewModel.foodLink.observe(viewLifecycleOwner) { foodLink ->Picasso.get().load(foodLink).into(binding.foodPic) }
        AllFoodViewModel.foodName.observe(viewLifecycleOwner) { foodName -> binding.txtFoodName.setText(foodName) }
        AllFoodViewModel.foodDesc.observe(viewLifecycleOwner) { foodDesc -> binding.txtDesc.setText(foodDesc) }
        AllFoodViewModel.foodPrice.observe(viewLifecycleOwner) { foodPrice -> binding.txtPrice.setText(foodPrice) }
        AllFoodViewModel.foodQty.observe(viewLifecycleOwner) { foodQty -> binding.txtQty.setText(foodQty) }
        AllFoodViewModel.foodQty.observe(viewLifecycleOwner) { foodQty -> minteger = foodQty.toInt() }
        AllFoodViewModel.foodName.observe(viewLifecycleOwner) { foodName -> id = foodName.toString() }


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

        binding.btnUpload.setOnClickListener{
            val foodName = binding.txtFoodName.text.toString()
            uploadImage(id)
            if(id != foodName){
                delete(id)
            }
        }

        binding.btnDelete.setOnClickListener{

            delete(id)
        }
        binding.btnPlus.setOnClickListener{

            minteger += 1;
            display(minteger);
        }
        binding.btnMinus.setOnClickListener {
            if (minteger > 0) {

                minteger -= 1;
                display(minteger);
            }
        }
        // foodDesc.text =  args?.get("foodDesc").toString()
        //foodPrice.text =  args?.get("foodPrice").toString()

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

    private fun uploadImage(id: String) {
        val progressDialog =  ProgressDialog(activity);
        progressDialog.setMessage("Uploading file..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val imageIdentifier = UUID.randomUUID().toString() + ".png";
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images\$fileName").child(imageIdentifier)


        if(imageURI != "null".toUri()) {
            val uploadTask = storageReference.putFile(imageURI).addOnSuccessListener {

                binding.foodPic.setImageURI(imageURI)
                val downloadUrl = storageReference.downloadUrl

                downloadUrl.addOnSuccessListener {
                    var remoteUrl = it.toString()
                    // update our Cloud Firestore with the public image URI.
                    saveData(remoteUrl, id)
                }
                Toast.makeText(activity, "Succesfuly Uploaded", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }.addOnFailureListener {
                progressDialog.dismiss()

                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()

            }
        }
        else if(bitmap != null){
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            val path: String = MediaStore.Images.Media.insertImage( requireActivity().contentResolver
                , bitmap, "Title", null)
            imageURI= Uri.parse(path);
            val uploadTask = storageReference.putFile(imageURI).addOnSuccessListener {

                binding.foodPic.setImageURI(imageURI)
                val downloadUrl = storageReference.downloadUrl

                downloadUrl.addOnSuccessListener {
                    var remoteUrl = it.toString()
                    // update our Cloud Firestore with the public image URI.
                    saveData(remoteUrl,id)
                }
                Toast.makeText(activity, "Succesfuly Uploaded", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }.addOnFailureListener {
                progressDialog.dismiss()

                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()

            }

        }
        else{



            AllFoodViewModel.foodLink.observe(viewLifecycleOwner) { foodLink ->

                saveData(foodLink, id)
                progressDialog.dismiss()
            }
        }



    }

    private fun delete(id: String) {


        val db = FirebaseFirestore.getInstance()

        val progressDialog =  ProgressDialog(activity);
        progressDialog.setMessage("Uploading file..")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val loginnedUser = Firebase.auth.currentUser
        val email = loginnedUser?.email


        if (email != null) {
            db.collection("seller").document(email).collection("foodList").document(id).delete()

                .
                addOnSuccessListener {
                    Toast.makeText(activity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }.
                addOnFailureListener {
                    Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()

                }
        }
    }


    private fun saveData(remoteUrl: String, id: String) {
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
        val food: MutableMap<String, Any> = java.util.HashMap()
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