package hu.bme.aut.poseidonclient.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.poseidonclient.R
import hu.bme.aut.poseidonclient.location.LocationHelper
import java.util.*


class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"
    private var locationHelper: LocationHelper? = null
    val db = Firebase.firestore

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }

    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
       if(locationHelper == null){
           val helper = LocationHelper(applicationContext, LocationServiceCallback())
           helper.startMonitorLocation()
           locationHelper = helper
       }
        return START_STICKY
    }

    private inner class MyThread: Thread(){
        override fun run(){
            var i = 0
            while(true){
                Thread.sleep(100)
                Log.d("thread", "$i")
                i++
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

    }

var k =0
    inner class LocationServiceCallback : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            k++
            val location = result.lastLocation ?: return
            Log.d("location2","k: $k Lat: ${location.latitude} Lng: ${location.longitude}")
            db.collection("locations").document("clientLocation").set(hashMapOf("latitude" to location.latitude, "longitude" to location.longitude))

            /*

            val intent = Intent()
            intent.action = BR_NEW_LOCATION
            intent.putExtra(KEY_LOCATION, location)
            LocalBroadcastManager.getInstance(this@LocationService).sendBroadcast(intent)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val withFloating = sharedPreferences.getBoolean(SettingsActivity.KEY_WITH_FLOATING, false)
            if (withFloating) {
                floatingWindowHelper.updateLocation(location)
            }*/


        }

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            Log.d("location","Location available: ${locationAvailability.isLocationAvailable}")
        }
    }

}