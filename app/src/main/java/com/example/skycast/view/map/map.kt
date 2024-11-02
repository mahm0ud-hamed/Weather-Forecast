package com.example.skycast.view.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.skycast.databinding.ActivityMapBinding
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.MainActivity
import com.example.skycast.R
import com.example.skycast.data.repository.Repository
import com.example.skycast.data.source.local.LocalDataSource
import com.example.skycast.data.source.remote.RemoteDataSource
import com.example.skycast.databinding.BootmSheetBinding
import com.example.skycast.model.database.DataBase
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.locationKey
import com.example.skycast.view.viewmodel.SharedViewModel
import com.example.skycast.view.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.osmdroid.config.Configuration
import java.io.IOException
import java.util.Locale
import com.example.skycast.model.sharedprefrence.SharedPrefrenceHelper.Companion.maps

class map : AppCompatActivity() {
    lateinit var binding: ActivityMapBinding
    private lateinit var marker: Marker
    private lateinit var gestureDetector: GestureDetector
    lateinit var vmFactory: ViewModelFactory
    lateinit var viewModel: SharedViewModel
    private val sharedPrefFile = "SettingPref"
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*creating an object from view model*/
        vmFactory = ViewModelFactory(
            Repository(
                RemoteDataSource(),
                LocalDataSource(
                    SharedPrefrenceHelper(
                        getSharedPreferences(
                            sharedPrefFile,
                            MODE_PRIVATE
                        )
                    ), DataBase.gteInstance(this).getWeatherDao(),
                    DataBase.gteInstance(this).getAlarmDao()
                )
            )
        )
        viewModel = ViewModelProvider(this, vmFactory).get(SharedViewModel::class.java)

        binding.map.setTileSource(TileSourceFactory.MAPNIK)
        binding.map.setMultiTouchControls(true)

        val startPoint = GeoPoint(51.505, -0.09)
        binding.map.controller.setZoom(5.0)
        binding.map.controller.setCenter(startPoint)


        marker = Marker(binding.map).apply {
            position = startPoint
            title = "London"
        }
        binding.map.overlays.add(marker)
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()

        // Initialize GestureDetector for long press
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                super.onLongPress(e)
                e.let {
                    // Get the coordinates of the long press event
                    val geoPoint: GeoPoint = binding.map.projection.fromPixels(it.x.toInt(), it.y.toInt()) as GeoPoint
                    // Update the marker's position
                    marker.position = geoPoint
                    marker.title = "Lat: ${geoPoint.latitude}, Lon: ${geoPoint.longitude}" // Update title with coordina // tes

                    val geoCoder = Geocoder(this@map , Locale.ENGLISH)
                    var addressText = getString(R.string.no_address_found)
                    try{
                        val address = geoCoder.getFromLocation(geoPoint.latitude, geoPoint.longitude , 5 )
                        if (address != null && address!!.isEmpty()){
                            addressText = address[0].getAddressLine(0) ?:getString(R.string.no_address_found)
                        }
                    }catch (e : IOException){
                        e.printStackTrace()
                    }
                    binding.map.invalidate() // Refresh the map
                    showLocationInfo(geoPoint, addressText)
                }
            }
        })

        // Set touch listener to detect gestures
        binding.map.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event) // Pass the touch event to the GestureDetector
            false // Allow other touch events to be processed by the map
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


    private fun showLocationInfo(geoPoint: GeoPoint , address :String){
        val bindSheetInfo = BootmSheetBinding.inflate(layoutInflater)
        val sheetInfoDialog = BottomSheetDialog(this)
        sheetInfoDialog.setContentView(bindSheetInfo.root)
        bindSheetInfo.latitudeValue.text = geoPoint.latitude.toString()
        bindSheetInfo.longitudeValue.text = geoPoint.longitude.toString()
        bindSheetInfo.addressValue.text = address

        bindSheetInfo.cancelButton.setOnClickListener {
            sheetInfoDialog.cancel()
        }

        bindSheetInfo.viewButton.setOnClickListener {
            val intent = Intent(this , MainActivity::class.java)
            intent.putExtra("LATITUDE" , geoPoint.latitude)
            Log.i("intent" , "in map activity"+geoPoint.latitude.toString() )
            intent.putExtra("LONGTIUDE" , geoPoint.longitude)
            intent.putExtra("ToView" , true )
            startActivity(intent)
            sheetInfoDialog.cancel()
            finish()

        }

        bindSheetInfo.setAsHome.setOnClickListener{
            viewModel.saveSelection("LAT_POINT" , geoPoint.latitude.toString())
            viewModel.saveSelection("LONG_POINT" , geoPoint.longitude.toString())
            val intent = Intent(this , MainActivity::class.java)
            intent.putExtra("LATITUDE" , geoPoint.latitude)
            intent.putExtra("LONGTIUDE" , geoPoint.longitude)
            intent.putExtra("ToHome" , true )
            /*solve issue of dublication of Home Screen , it will start activity of Home again , not use what in back stack */
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            sheetInfoDialog.cancel()
            viewModel.saveSelection(locationKey , maps)
            finish()

        }

        sheetInfoDialog.show()

    }
}