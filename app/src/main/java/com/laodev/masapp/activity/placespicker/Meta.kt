package com.laodev.masapp.activity.placespicker

import com.google.gson.annotations.SerializedName

data class Meta(
        @SerializedName("code")
        val code: Int,
        @SerializedName("requestId")
        val requestId: String
)