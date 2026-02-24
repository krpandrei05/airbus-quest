package com.example.project_mad

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.Manifest
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), LocationListener {
    private val MainTAG = "MainActivity"

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var tvLocation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        Log.d(MainTAG, "onCreate: MainActivity created!")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSecondActivity: Button = findViewById(R.id.btnSecondActivity)
        btnSecondActivity.setOnClickListener {
            Log.d(MainTAG, "Click: Going to SecondActivity.")
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        tvLocation = findViewById(R.id.tvLocation)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkPermissionsAndStartLocation()

        val btnOpenStreetMap: Button = findViewById(R.id.btnOpenStreetMap)
        btnOpenStreetMap.setOnClickListener {
            val hasFine = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

            // Check if we have AT LEAST ONE permission (Fine or Coarse)
            if (hasFine || hasCoarse) {
                // Permission granted! Try to find the location.
                var latestLocation: Location? = null

                if (hasFine) {
                    latestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }

                // If GPS failed or we only have Coarse permission, try Network provider
                if (latestLocation == null) {
                    latestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                if (latestLocation != null) {
                    // Sent real location to the Map Activity
                    val intent = Intent(this, OpenStreetMapsActivity::class.java)

                    val bundle = Bundle()
                    bundle.putParcelable("location", latestLocation)
                    intent.putExtra("locationBundle", bundle)

                    startActivity(intent)
                } else {
                    Log.e(MainTAG, "Click: No location found!")
                    Toast.makeText(this, "Location not found yet. Try outside.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermissionsAndStartLocation() {
        val hasFine = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        tvLocation.text = "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
        Log.i(MainTAG, "Location updated: ${location.latitude}, ${location.longitude}")
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}