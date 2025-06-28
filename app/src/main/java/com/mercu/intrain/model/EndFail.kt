package com.mercu.intrain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class EndFail(

	@field:SerializedName("response")
	val response: Response? = null,

	@field:SerializedName("session_id")
	val sessionId: String? = null
)

@Parcelize
data class Response(

	@field:SerializedName("score")
	val score: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("recommendations")
	val recommendations: List<String?>? = null
) : Parcelable
