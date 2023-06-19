package com.dartmedia.nextversms.remote.dto

import com.google.gson.annotations.SerializedName

data class SendRequestResponse(

	@field:SerializedName("msg")
	val msg: String? = null,

	@field:SerializedName("to")
	val to: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("error")
	val error: String? = null


)
