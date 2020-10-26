package com.laodev.masapp.activity.placespicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.laodev.masapp.R
import com.laodev.masapp.util.PermissionsUtil
import kotlinx.android.synthetic.main.activity_places_picker.*
import kotlinx.android.synthetic.main.places_bottomsheet.*
import java.util.*

class PlacesPickerActivity : ScopedActivity(), OnMapReadyCallback, NearbyPlacesAdapter.OnClickListener {

    private lateinit var viewModel: PlacesPickerViewModel
    private lateinit var mMap: GoogleMap
    private var markerOptions: MarkerOptions? = null
    private var mMarker: Marker? = null
    private val places = mutableListOf<Place>()
    private lateinit var mAdapter: NearbyPlacesAdapter

    private var rvPlaces: RecyclerView? = null

    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)


    override fun onMapReady(map: GoogleMap?) {
        mMap = map!!
        mMap.setOnCameraMoveListener {
            mMarker?.position = mMap.cameraPosition.target
            if (switch_nearby_places.isChecked) {
                viewModel.markerMoved(map.cameraPosition.target)
            }
        }
        checkPermissions()
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_picker)

        rvPlaces = findViewById(R.id.rv_places)

        viewModel = ViewModelProviders
                .of(this, PlacesPickerViewModelFactory(this, this))
                .get(PlacesPickerViewModel::class.java)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        setupAdapter()

        viewModel.currentLocationLiveData.observe(this, androidx.lifecycle.Observer {
            if (mMarker == null) {
                markerOptions = MarkerOptions().position(it!!)
                if (mMap != null)
                    mMarker = mMap.addMarker(markerOptions)
            }
            mMarker?.position = it
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
        })

        viewModel.nearbyPlacesLiveData.observe(this, androidx.lifecycle.Observer {
            if (switch_nearby_places.isChecked) {
                places.clear()
                places.addAll(it)
                rvPlaces!!.adapter!!.notifyDataSetChanged()
            }
        })

        viewModel.showLocationDialogLiveData.observe(this, androidx.lifecycle.Observer {
            enableGps()
        })

        get_location.setOnClickListener {
            checkPermissions()
        }

        tv_select_this_location.setOnClickListener {
            showDialog()
        }

        switch_nearby_places.setOnCheckedChangeListener { switch, isChecked ->
            if (isChecked) {
                if (!PermissionsUtil.hasLocationPermissions(this@PlacesPickerActivity)) {
                    switch.toggle()
                    Toast.makeText(this@PlacesPickerActivity, R.string.missing_permissions, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.markerMoved(mMap.cameraPosition.target)
                }
            } else {
                places.clear()
                rvPlaces!!.adapter!!.notifyDataSetChanged()
            }
        }
        mAdapter.onClickListener = this
    }

    private fun enableGps() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val result =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        result.addOnCompleteListener {
            try {
                val response = it.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                if (exception.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(
                                this,
                                LocationRequest.PRIORITY_HIGH_ACCURACY)
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    } catch (e: ClassCastException) {
                        // Ignore, should be an impossible error.
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // All required changes were successfully made
                Log.i("3llomi", "onActivityResult: GPS Enabled by user")
                viewModel.getCurrentLocation()
                if (switch_nearby_places.isChecked) {
                    viewModel.markerMoved(mMap.cameraPosition.target)
                }

            } else {
                Log.i("3llomi", "onActivityResult: GPS not Enabled by user")
            }
        }
    }

    override fun onClick(view: View, place: Place) {
        showDialog(place)
    }

    private fun setupAdapter() {
        rvPlaces!!.layoutManager = LinearLayoutManager(this)
        mAdapter = NearbyPlacesAdapter(this, places)
        rvPlaces!!.adapter = mAdapter
    }

    private fun checkPermissions() {
        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                    .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                for (index in permissions.indices.reversed()) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, R.string.missing_permissions, Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                }
                // all permissions were granted
                Log.d("3llomi", "onRequestPermissionsResult")
                viewModel.getCurrentLocation()
            }
        }
    }

    private fun showDialog(place: Place? = null) {
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle(getString(R.string.user_this_location))

            if (place != null) {
                val message = "${place.name} \n ${place.address}"
                setMessage(message)
            }

            setNegativeButton(R.string.change_location, null)
            setPositiveButton(R.string.normal_select) { _, _ ->
                val data = Intent()
                if (place != null) {
                    data.putExtra(Place.EXTRA_PLACE, place)
                } else {
                    data.putExtra(Place.EXTRA_PLACE, Place("", "", "", mMap.cameraPosition.target))
                }
                setResult(Activity.RESULT_OK, data)
                finish()
            }

            dialog.show()
        }
    }
}

