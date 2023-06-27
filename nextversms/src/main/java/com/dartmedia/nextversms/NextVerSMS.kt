package com.dartmedia.nextversms

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.widget.Toast
import com.dartmedia.nextversms.remote.ApiConfig
import com.dartmedia.nextversms.remote.ApiManager
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
    private var apiKey: String
    private var apiSecret: String
    private var mContext: Context

    private lateinit var smsManager: SmsManager

    class Builder(context: Context) {
        private val mContext = context
        private val apiManager = ApiManager(mContext)

        private var apiKey: String? = null
        private var apiSecret: String? = null

        fun url(url: String) = apply { apiManager.setBaseUrl(url) }
        fun apiKey(apiKey: String) = apply { this.apiKey = apiKey }
        fun apiSecret(apiSecret: String) = apply { this.apiSecret = apiSecret }
        fun build() = NextVerSMS(this)

        fun getApiKey() = apiKey!!
        fun getApiSecret() = apiSecret!!
        fun getContext() = mContext
    }

    init {
        apiKey = builder.getApiKey()
        apiSecret = builder.getApiSecret()
        mContext = builder.getContext()
    }

    private fun sendRequest(sendRequestBody: SendRequestBody, callback: VerifyListener) {
        ApiConfig.getApiService(mContext).sendRequest(sendRequestBody).enqueue(object : Callback<SendRequestResponse> {
            override fun onResponse(
                call: Call<SendRequestResponse>,
                response: Response<SendRequestResponse>
            ) {
                if (!response.isSuccessful) {
                    callback.onFailed(response.message())
                } else {
                    sendSMS(sendRequestBody.msisdn, response.body()?.to.toString(), response.body()?.msg.toString(), callback)
                }
            }

            override fun onFailure(call: Call<SendRequestResponse>, t: Throwable) {
                callback.onFailed(t.message.toString())
            }
        })
    }

    private fun getStatusRequest(msisdn: String, to: String, msg: String, callback: VerifyListener) {
        ApiConfig.getApiService(mContext).getStatusRequest(msisdn, to, msg).enqueue(object : Callback<StatusResponse> {
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
                        }
                        "failed" -> {
                            callback.onFailed("Send SMS failed")
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
        Handler(Looper.getMainLooper()).postDelayed({
            getStatusRequest(msisdn, to, msg, callback)
        }, 5000)
    }

    private fun sendSMS(msisdn: String, phoneNumber: String, message: String, callback: VerifyListener) {
        try {
            smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage("+$phoneNumber", null, message, null, null)
            getStatusRequest(msisdn, phoneNumber, message, callback)
        } catch (e: Exception) {
            callback.onFailed(e.localizedMessage)
        }
    }

    fun verify(msisdn: String, callback: VerifyListener) {
        val sendRequestBody = SendRequestBody(msisdn)
        sendRequest(sendRequestBody, callback)
    }
}