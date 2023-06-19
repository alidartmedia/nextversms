package com.dartmedia.smsmanagerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.dartmedia.nextversms.NextVerSMS
import com.dartmedia.nextversms.utils.VerifyListener
import com.dartmedia.smsmanagerapp.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {
    companion object {
        const val TAG = "Verification Activity"
        const val PHONE_NUMBER = "phone number"
    }

    private lateinit var countdownTimer: CountDownTimer

    private var timeLeftInMillis: Long = 90000

    private val binding: ActivityVerificationBinding by lazy { ActivityVerificationBinding.inflate(layoutInflater) }
    private val nextVerSMS: NextVerSMS by lazy {
        NextVerSMS.Builder(this)
            .url("https://a46a-182-253-154-61.ngrok-free.app/")
            .apiKey("")
            .apiSecret("")
            .build()
    }
    private val dialogSuccess: DialogSuccess by lazy { DialogSuccess(this@VerificationActivity) }
    private val dialogFailed: DialogFailed by lazy { DialogFailed(this@VerificationActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        verify()
        countDown()
        retryVerify()
        actionBackPressed()
    }

    private fun verify() {
        val phoneNumber = intent.getStringExtra(PHONE_NUMBER)
        if (phoneNumber != null) {
            nextVerSMS.verify(phoneNumber, object : VerifyListener {
                override fun onFailed(errorMessage: String) {
                    countdownTimer.onFinish()
                    countdownTimer.cancel()

                    dialogFailed.showDialog()
                }

                override fun onSuccess() {
                    countdownTimer.onFinish()
                    countdownTimer.cancel()

                    dialogSuccess.showDialog()
                }
            })
        }
    }

    private fun countDown() {
        countdownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateCountdownText()
            }

            override fun onFinish() {
                with(binding) {
                    btnRetry.isEnabled = true
                    tvCountDown.text = "00:00"
                }
            }
        }

        countdownTimer.start()
    }

    private fun updateCountdownText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60

        val timeLeftFormatted = String.format("%02d:%02d", minutes, seconds)
        binding.tvCountDown.text = timeLeftFormatted
    }

    private fun retryVerify() {
        binding.btnRetry.setOnClickListener {
            timeLeftInMillis = 90000
            binding.btnRetry.isEnabled = false

            countDown()
            verify()
        }
    }

    private fun actionBackPressed() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}