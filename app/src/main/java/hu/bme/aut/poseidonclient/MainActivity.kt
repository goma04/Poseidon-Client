package hu.bme.aut.poseidonclient

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.poseidonclient.broadcastReceiver.SMSReceiver
import hu.bme.aut.poseidonclient.databinding.ActivityMainBinding
import hu.bme.aut.poseidonclient.service.LocationService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedpreferences: SharedPreferences
    private val MyPREFERENCES = "MyPrefs"
    private val Name = "name"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        binding.btnStart.setOnClickListener {
            startForegroundService(Intent(this, LocationService::class.java))
        }

        val name: String? = sharedpreferences.getString("name", "defa")

        Globals.name = name ?: "error"
        binding.tvName.text = name

        binding.btnSetName.setOnClickListener {
            val editor: SharedPreferences.Editor = sharedpreferences.edit()
            editor.putString(Name, binding.etName.text.toString());
            editor.apply()
            binding.tvName.text = binding.etName.text.toString()
            Globals.name = binding.etName.text.toString()
        }

        binding.btnStop.setOnClickListener {
            stopService(Intent(this, LocationService::class.java))
        }

        if (!checkPermission()) {
            requestPermission()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                1015
            )
        }
    }

    private fun checkPermission(): Boolean {
        val result = checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val result1 = checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val result2 = checkSelfPermission(
            applicationContext,
            Manifest.permission.RECEIVE_SMS
        )

        val result3 = checkSelfPermission(
            applicationContext,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {


        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,  Manifest.permission.ACCESS_BACKGROUND_LOCATION

            ),
            101,
        )
    }
}

