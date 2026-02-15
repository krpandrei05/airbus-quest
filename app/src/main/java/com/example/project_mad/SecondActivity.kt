package com.example.project_mad

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity(), LocationListener {
    private val SecondTag = "SecondActivity"

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_second)

        Log.d(SecondTag, "onCreate: SecondActivity created!")

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnMainActivity: Button = findViewById(R.id.btnMainActivity)
        btnMainActivity.setOnClickListener {
            Log.d(SecondTag, "Click: Going to MainActivity.")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnThirdActivity: Button = findViewById(R.id.btnThirdActivity)
        btnThirdActivity.setOnClickListener {
            Log.d(SecondTag, "Click: Going to ThirdActivity.")
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        val btnGetLocation: Button = findViewById(R.id.btnGetLocation)
        btnGetLocation.setOnClickListener {
            Log.d(SecondTag, "Click: Getting location.")
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                startLocationUpdates()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d(SecondTag, "Location received, sending to ThirdActivity")

        locationManager.removeUpdates(this)

        val intent = Intent(this, ThirdActivity::class.java)
        val bundle = Bundle()

        bundle.putParcelable("location", location)
        intent.putExtra("locationBundle", bundle)

        startActivity(intent)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
}