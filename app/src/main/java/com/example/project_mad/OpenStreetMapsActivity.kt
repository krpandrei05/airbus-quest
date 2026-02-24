package com.example.project_mad

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class OpenStreetMapsActivity : AppCompatActivity() {
    private val OpenStreetMapsTag = "OpenStreetMapsActivity"
    private lateinit var map: MapView

    val campusMap = mapOf(
        "Tennis" to GeoPoint(40.38779608214728, -3.627687914352839),
        "Futsal outdoors" to GeoPoint(40.38788595319803, -3.627048250272035),
        "Fashion and design school" to GeoPoint(40.3887315224542, -3.628643539758645),
        "Topography school" to GeoPoint(40.38926842612264, -3.630067893975619),
        "Telecommunications school" to GeoPoint(40.38956358584258, -3.629046081389352),
        "ETSISI" to GeoPoint(40.38992125672989, -3.6281366497769714),
        "Library" to GeoPoint(40.39037466191718, -3.6270256763598447),
        "CITSEM" to GeoPoint(40.389855884803005, -3.626782180787362)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(OpenStreetMapsTag, "onCreate: OpenStreetMapsActivity created!")

        enableEdgeToEdge()
        setContentView(R.layout.activity_open_street_maps)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Configuration.getInstance().userAgentValue = "com.example.project_mad"
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))

        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")

        val startPoint = if (location != null) {
            Log.d(OpenStreetMapsTag, "onCreate: Location [${location.altitude}][${location.latitude}][${location.longitude}]")
            GeoPoint(location.latitude, location.longitude)
        } else {
            Log.d(OpenStreetMapsTag, "onCreate: Location is null, using default coordinates")
            GeoPoint(40.389683644051864, -3.627825356970311)
        }

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.controller.setZoom(18.0)
        map.controller.setCenter(startPoint)

        // First location -> My location
        val startMarker = Marker(map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = ContextCompat.getDrawable(this, android.R.drawable.star_on) as BitmapDrawable
        startMarker.title = "My current location"
        map.overlays.add(startMarker)

        addRouteMarkers(map, campusMap, this)
    }

    fun addRouteMarkers(map: MapView, campusCoords: Map<String, GeoPoint>, context: Context) {
        // Draw the path connecting all points
        val polyline = Polyline()
        polyline.setPoints(campusCoords.values.toList())
        polyline.outlinePaint.color = Color.BLUE
        polyline.outlinePaint.strokeWidth = 10f
        map.overlays.add(polyline)

        // Add markers for each location
        for ((name, point) in campusCoords) {
            val marker = Marker(map)
            marker.position = point
            marker.title = name

            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.icon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_compass) as BitmapDrawable

            map.overlays.add(marker)
        }

        // Refresh map display
        map.invalidate()
    }
}