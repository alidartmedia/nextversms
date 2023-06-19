package com.dartmedia.smsmanagerapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.dartmedia.nextversms.NextVerSMS
import com.dartmedia.nextversms.utils.VerifyListener
import com.dartmedia.smsmanagerapp.databinding.ActivityMainBinding
import pub.devrel.easypermissions.AppSettingsDialog


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "Main Activity"
    }

    private lateinit var smsManager: SmsManager

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val dialogLoading: DialogLoading by lazy { DialogLoading(this@MainActivity) }
    private val nextVerSMS: NextVerSMS by lazy {
        NextVerSMS.Builder()
            .apiKey("")
            .apiSecret("")
            .build()
    }
    private val permissions = arrayOf(Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        checkPermission()
        actionSendMsg()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AppSettingsDialog.Builder(this).setTitle("SMS permission is required").build().show()
            } else if (grantResults[1] != PackageManager.PERMISSION_GRANTED){
                AppSettingsDialog.Builder(this).setTitle("SMS Permission is required").build().show()
            } else if (grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                AppSettingsDialog.Builder(this).setTitle("Phone Permission is required").build().show()
            }
        }
    }

    private fun hasPermissions(): Boolean {
        permissions.forEach { perm ->
            if (ActivityCompat.checkSelfPermission(this@MainActivity, perm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }

        return true
    }

    private fun checkPermission() {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this@MainActivity, permissions, 101 )
        }
    }

    @SuppressLint("Range")
    private fun actionSendMsg() {

        with(binding) {
            btnSend.setOnClickListener {
                val phoneNumber = etPhoneNumber.text.toString()

                if (phoneNumber.isEmpty()) {
                    etPhoneNumber.error = "Field is required"
                } else {
//                    deleteOutboxSMS("+6281905598514")
//                    sendSMS(phoneNumber, msg)

//                    val cursor = contentResolver.query(Uri.parse("content://sms"), null, null, null, null)
//                    cursor?.moveToFirst()
//
//                    Log.d(TAG, cursor?.getString(cursor.getColumnIndex("body")) ?: "null")

                    dialogLoading.showLoading()

                     nextVerSMS.verify(phoneNumber, object : VerifyListener {
                        override fun onSuccess() {
                            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                            dialogLoading.hideLoading()
                        }

                        override fun onFailed(errorMessage: String) {
                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            dialogLoading.hideLoading()
                        }
                    })
                }
            }
        }
    }
}