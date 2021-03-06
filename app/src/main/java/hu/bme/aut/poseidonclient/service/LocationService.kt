package hu.bme.aut.poseidonclient.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.poseidonclient.Globals
import hu.bme.aut.poseidonclient.R
import hu.bme.aut.poseidonclient.broadcastReceiver.SMSReceiver
import hu.bme.aut.poseidonclient.location.LocationHelper
import java.util.*
import android.content.IntentFilter
import android.widget.Toast


class LocationService : Service() {
    private val CHANNEL_ID = "ForegroundServiceChannel"
    private var locationHelper: LocationHelper? = null
    val db = Firebase.firestore


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (locationHelper == null) {
            val helper = LocationHelper(applicationContext, LocationServiceCallback())
            helper.startMonitorLocation()
            locationHelper = helper
        }

        createNotificationChannel();



        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("notificationTitle")
            .setContentText("lwl")
            .build()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }



    inner class LocationServiceCallback : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {

            val location = result.lastLocation ?: return

            db.collection("phones").document(
                Globals.name
            ).set(
                hashMapOf(
                    "_nothing" to "nothing"
                )
            )

            db.collection("phones").document(Globals.name).collection("location")
                .document("location")
                .set(hashMapOf("latitude" to location.latitude, "longitude" to location.longitude))
        }
    }
}