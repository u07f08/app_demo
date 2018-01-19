package com.example.flowmahuang.kotlinpractice.module.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.example.flowmahuang.kotlinpractice.module.permission.PermissionsChecker
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.util.*


class GetAddressCounty(val mContext: Context, val callback: CountyNameCallback) {
    interface CountyNameCallback {
        fun isPermissionDeny()

        fun getLocationFail()

        fun countyName(cityName: String, countyName: String)
    }

    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    private val checkPermissionList = Array(2) {
        Manifest.permission.ACCESS_FINE_LOCATION
        Manifest.permission.ACCESS_COARSE_LOCATION
    }

    private lateinit var mLocationRequest: LocationRequest
    private lateinit var mLocationProviderClient: FusedLocationProviderClient
    private lateinit var mSettingsClient: SettingsClient
    private lateinit var mLocationSettingsRequest: LocationSettingsRequest

    init {
        checkPermission()
    }

    private fun checkPermission() {
        val permissionsChecker = PermissionsChecker(mContext)
        if (permissionsChecker.missingPermissions(* checkPermissionList)) {
            callback.isPermissionDeny()
        } else {
            mLocationRequest = LocationRequest()
            mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
            mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            mLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext)
            mSettingsClient = LocationServices.getSettingsClient(mContext)
            mLocationSettingsRequest = LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest)
                    .build()
        }
    }

    private fun getCountyNameByLngLat(lng: Double, lat: Double) {
        val gc = Geocoder(mContext, Locale.TRADITIONAL_CHINESE)
        val address = gc.getFromLocation(lat, lng, 1)[0]

        callback.countyName(address.adminArea, address.locality)
    }

    fun requestUserLocation() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(checkSettingSuccessListener)
                .addOnFailureListener(checkSettingFailureListener)
    }

    /**
     *  Callback Func
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            if (p0 != null) {
                getCountyNameByLngLat(p0.lastLocation.longitude, p0.lastLocation.latitude)
            } else {

                callback.getLocationFail()
            }
            mLocationProviderClient.removeLocationUpdates(this)
        }
    }

    /**
     * permission already check when class initialed
     */
    @SuppressLint("MissingPermission")
    private val checkSettingSuccessListener = OnSuccessListener<LocationSettingsResponse> {
        mLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, mContext.mainLooper)
    }

    private val checkSettingFailureListener = OnFailureListener {
        callback.getLocationFail()
    }
}