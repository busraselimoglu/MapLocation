package com.example.maplocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.maplocation.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(listener)

        //Latitude (Enlem) Longitude(Boylam)
        //val sydney = LatLng(39.9208372, 32.8454561)
        // Add a marker in Sydney and move the camera
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

        //It seems so far from the world
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        //Position approached -> newLatLngZoom
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15f))



        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // LocationListener -> current location
        locationListener = LocationListener { location ->
            // actions to be taken as long as the location changes
            //println(location.latitude)
            //println(location.longitude)

            // Setting Current Location
            mMap.clear() // marker is clear
            val currentLocation = LatLng(location.latitude,location.longitude)
            mMap.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,16f))


            // Geocoder -> Convert latitude and longitude information to address information
            // Regeocoder -> Convert address information to latitude and longitude information
            val geocoder = Geocoder(this,Locale.getDefault())
            try {

                val addressList = geocoder.getFromLocation(location.latitude,location.longitude,1)
                if (addressList.isNotEmpty()){
                    println(addressList[0].toString())
                }

            }catch (e: Exception){

            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // if not allowed
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            // if allowed
            //requestLocationUpdates --> request Location Updates
            // provider: sağlayıcı   , minTime: Tell how long to request Location Updates, minDistanceM: tell location update minimum distance
            // instant control -> minTime=1  minDistanceM=1f
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

            // last known location
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null){
                val lastLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastLatLng).title("Last Known Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng,15f))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            // allowed
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
        }
    }

    val listener = object: GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(latLng: LatLng) {
            mMap.clear()

            val geocoder = Geocoder(this@MainActivity,Locale.getDefault())

            if (latLng != null){
                var addressLoc = ""
                try {
                    val addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1)
                    if (addressList.size > 0){
                        if (addressList[0].thoroughfare != null){
                            addressLoc += addressList[0].thoroughfare
                            if (addressList[0].subThoroughfare != null){
                                addressLoc += addressList[0].subThoroughfare
                            }
                        }
                    }
                }catch (e: Exception){
                    println(e.message)
                }
                mMap.addMarker(MarkerOptions().position(latLng).title(addressLoc))
            }

        }

    }

}