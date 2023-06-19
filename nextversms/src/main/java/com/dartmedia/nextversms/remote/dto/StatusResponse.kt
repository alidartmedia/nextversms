package com.dartmedia.nextversms.remote.dto

import com.google.gson.annotations.SerializedName

data class StatusResponse(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("error")
	val error: String? = null

)
