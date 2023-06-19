package com.dartmedia.smsmanagerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
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

    private fun actionSendMsg() {
        with(binding) {
            btnSend.setOnClickListener {
                val codeArea = etAreaCode.text.toString()
                val phoneNumber = etPhoneNumber.text.toString()

                if (codeArea.isEmpty()) {
                    etAreaCode.error = "Field is required"
                } else if (phoneNumber.isEmpty()) {
                    etPhoneNumber.error = "Field is required"
                } else {
                    val i = Intent(this@MainActivity, VerificationActivity::class.java)
                    i.putExtra(VerificationActivity.PHONE_NUMBER, "$codeArea$phoneNumber")
                    startActivity(i)
                }
            }
        }
    }
}