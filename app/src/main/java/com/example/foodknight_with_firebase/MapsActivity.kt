package com.example.foodknight_with_firebase

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodknight_with_firebase.databinding.ActivityMapsBinding
import com.example.foodknight_with_firebase.ui.sellerProfileV2.sellerProfileV2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    var currentMarker: Marker? = null
    var currentAddress = ""
    private lateinit var mMap: GoogleMap
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var db: FirebaseFirestore
    private val REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        db = FirebaseFirestore.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        fetchLocation()


        findViewById<Button>(R.id.btnUpdateAddress).setOnClickListener{
            updateFirestore()
            startActivity(Intent(this, sellerProfileV2::class.java))

        }
    }

    private fun updateFirestore(){
        val db = FirebaseFirestore.getInstance()
        val loginnedUser = Firebase.auth.currentUser
        val mail1 = loginnedUser?.email.toString()
        val progressDialog =  ProgressDialog(this);
        progressDialog.setMessage("Updating..")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val user: MutableMap<String, Any> = java.util.HashMap()
        user["shopaddress"] = currentAddress

        db.collection("restaurant").document(mail1).set(user, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }.
            addOnFailureListener {
                Toast.makeText(this, "Failed to Update", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()

            }

    }

    private fun fetchLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }

        val task = fusedLocationProviderClient!!.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                //Toast.makeText(applicationContext, currentLocation!!.latitude.toString() + "" + currentLocation!!.longitude,
                // Toast.LENGTH_SHORT).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@MapsActivity)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
        moveMarker(latLng)

        googleMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                Log.d("====", "latitude : " + marker.position.latitude)

                if (currentMarker != null) {
                    currentMarker?.remove()
                }
                val newlatLng = LatLng(marker.position.latitude, marker.position.longitude)
                moveMarker(newlatLng)
            }

            override fun onMarkerDrag(marker: Marker) {}
        })
    }

    private fun moveMarker(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng).title("I am here")
            .snippet(getTheAddress(latLng!!.latitude, latLng!!.longitude)).draggable(true)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        currentMarker = mMap.addMarker(markerOptions)
        currentMarker?.showInfoWindow()
    }
    private fun getTheAddress(latitude: Double, longitude: Double): String? {
        var retVal = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            currentAddress = addresses[0].getAddressLine(0)
            retVal = addresses[0].getAddressLine(0)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return retVal
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }
}