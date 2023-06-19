package com.dartmedia.nextversms

import android.telephony.SmsManager
import android.widget.Toast
import com.dartmedia.nextversms.remote.ApiConfig
import com.dartmedia.nextversms.remote.dto.SendRequestBody
import com.dartmedia.nextversms.remote.dto.SendRequestResponse
import com.dartmedia.nextversms.remote.dto.StatusResponse
import com.dartmedia.nextversms.utils.VerifyListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NextVerSMS private constructor(builder: Builder){
    private val timer = Timer()

    private var apiKey: String
    private var apiSecret: String

    private lateinit var smsManager: SmsManager

    class Builder {
        private var apiKey: String? = null
        private var apiSecret: String? = null

        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun apiSecret(apiSecret: String) = apply { this.apiSecret = apiSecret }
        fun build() = NextVerSMS(this)

        fun getApiKey() = apiKey!!
        fun getApiSecret() = apiSecret!!
    }

    init {
        apiKey = builder.getApiKey()
        apiSecret = builder.getApiSecret()
    }

    private fun sendRequest(sendRequestBody: SendRequestBody, callback: VerifyListener) {
        ApiConfig.getApiService().sendRequest(sendRequestBody).enqueue(object : Callback<SendRequestResponse> {
            override fun onResponse(
                call: Call<SendRequestResponse>,
                response: Response<SendRequestResponse>
            ) {
                if (!response.isSuccessful) {
                    callback.onFailed(response.message())
                } else {
                    getStatusRequest(sendRequestBody.msisdn, response.body()?.to.toString(), response.body()?.msg.toString(), callback)
                }
            }

            override fun onFailure(call: Call<SendRequestResponse>, t: Throwable) {
                callback.onFailed(t.message.toString())
            }
        })
    }

    private fun getStatusRequest(msisdn: String, to: String, msg: String, callback: VerifyListener) {
        ApiConfig.getApiService().getStatusRequest(msisdn).enqueue(object : Callback<StatusResponse> {
            override fun onResponse(
                call: Call<StatusResponse>,
                response: Response<StatusResponse>
            ) {
                if (!response.isSuccessful) {
                    val type = object : TypeToken<StatusResponse>(){}.type
                    val err = Gson().fromJson<StatusResponse>(response.errorBody()!!.charStream(), type)!!
                    callback.onFailed(err.error.toString())
                } else {
                    when (response.body()?.result) {
                        "success" -> {
                            callback.onSuccess()
                            timer.cancel()
                            sendSMS(to, msg)
                        }
                        "failed" -> {
                            callback.onFailed("Send SMS failed")
                            timer.cancel()
                        }
                        else -> {
                            scheduleApiCallWhenPending(msisdn, to, msg, callback)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<StatusResponse>, t: Throwable) {
                callback.onFailed(t.message.toString())
            }
        })
    }

    private fun scheduleApiCallWhenPending(msisdn: String, to: String, msg: String, callback: VerifyListener) {
        timer.schedule(object : TimerTask() {
            override fun run() {
                getStatusRequest(msisdn, to, msg, callback)
            }
        }, 5000)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("+$phoneNumber", null, message, null, null)
    }

    fun verify(msisdn: String, callback: VerifyListener) {
        val sendRequestBody = SendRequestBody(msisdn)
        sendRequest(sendRequestBody, callback)
    }
}