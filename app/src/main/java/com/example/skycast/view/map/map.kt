package com.example.skycast.view.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.skycast.databinding.ActivityMapBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import android.Manifest
import android.content.pm.PackageManager
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import org.osmdroid.config.Configuration



class map : AppCompatActivity() {
    lateinit var binding : ActivityMapBinding
     private lateinit var marker: Marker


    companion object{
       const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)

        val startPoint = GeoPoint(51.505, -0.09)
        binding.map.controller.setZoom(15.0)
        binding.map.controller.setCenter(startPoint)


         marker = Marker(binding.map).apply {
            position = startPoint
            title = "London"
        }
        binding.map.overlays.add(marker)
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()

        binding.map.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Get the coordinates of the touch event
                val geoPoint :GeoPoint = binding.map.projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                // Update the marker's position
                marker.position = geoPoint
                marker.title = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}" // Update title with coordinates
                marker.setOnMarkerClickListener { _, _ ->
                    // Optionally, show a toast or log the coordinates
                    Toast.makeText(this, marker.title, Toast.LENGTH_SHORT).show()
                    false
                }
                binding.map.invalidate() // Refresh the map
            }
            false
        }
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDetach()

    }
}