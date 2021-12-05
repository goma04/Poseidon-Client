package hu.bme.aut.poseidonclient.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import android.os.Looper

class LocationHelper(private val context: Context, private val callback: LocationCallback) {

    @SuppressLint("MissingPermission")
    fun startMonitorLocation() {
        val request = LocationRequest().apply {
            interval = 5000L
            fastestInterval = 5000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(request, callback, Looper.getMainLooper())
    }
}

