package com.laodev.masapp.activity.placespicker

import com.google.gson.annotations.SerializedName

data class Response(
        @SerializedName("confident")
        val confident: Boolean,
        @SerializedName("venues")
        val venues: List<Venue>
)