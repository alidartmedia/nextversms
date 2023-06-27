package com.dartmedia.nextversms.remote

import com.dartmedia.nextversms.remote.dto.SendRequestBody
import com.dartmedia.nextversms.remote.dto.SendRequestResponse
import com.dartmedia.nextversms.remote.dto.StatusResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("request")
    fun sendRequest(@Body sendRequestBody: SendRequestBody): Call<SendRequestResponse>

    @GET("status")
    fun getStatusRequest(@Query("msisdn") msisdn: String, @Query("to") to: String, @Query("msg") msg: String): Call<StatusResponse>

}