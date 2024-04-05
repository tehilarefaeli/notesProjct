package com.example.notesapp.activities.ui.main

import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.SearchView.OnQueryTextListener
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.type.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationActivity : AppCompatActivity() {

    private var locationProvider: FusedLocationProviderClient ?= null
    private lateinit var binding: ActivityLocationBinding
    private var googleMap : GoogleMap ?= null
    private var myLocation: Location?=null
    private var address: Address ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findMyLocationIfHasAccess()
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            markMyLocation()
        }
        binding.search.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {location->
                    val geoCoder = Geocoder(this@LocationActivity, Locale.getDefault())
                    try {
                        val addressList = geoCoder.getFromLocationName(query, 1)

                            addressList?.takeIf { it.size > 0 }?.let {
                                val lan = com.google.android.gms.maps.model.LatLng(
                                    it.get(0).latitude,
                                    it.get(0).longitude
                                )
                                googleMap?.clear()
                                googleMap?.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        lan,
                                        10f
                                    )
                                )
                                googleMap?.addMarker(MarkerOptions().position(lan).title(query))
                                this@LocationActivity.address = it.get(0)

                            }
                    }catch (e: Exception){
                        val x = 0

                    }

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun findMyLocationIfHasAccess() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            findMyLocation()
        }
    }

    private fun findMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            locationProvider?.getLastLocation()?.addOnSuccessListener {
                myLocation = it
                markMyLocation()
            }
        }
    }

    private fun markMyLocation() {
        myLocation?.let {myLocation->
            googleMap?.let {googleMap->
                var latitude = myLocation.latitude
                var longitude = myLocation.longitude
                val latlng = com.google.android.gms.maps.model.LatLng(latitude, longitude)
                googleMap.addMarker(MarkerOptions().position(latlng))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16f))
                val geoCoder = Geocoder(this@LocationActivity)
                try {
                    this@LocationActivity.address = geoCoder.getFromLocation(latitude, longitude, 1)?.get(0)

                }catch (e: Exception){}
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        findMyLocation()
    }
}