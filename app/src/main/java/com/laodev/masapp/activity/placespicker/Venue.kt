package com.laodev.masapp.activity.placespicker

import com.google.gson.annotations.SerializedName

data class Venue(
        @SerializedName("categories")
        val categories: List<Category>,
        @SerializedName("hasPerk")
        val hasPerk: Boolean,
        @SerializedName("id")
        val id: String,
        @SerializedName("location")
        val location: Location,
        @SerializedName("name")
        val name: String,
        @SerializedName("referralId")
        val referralId: String,
        @SerializedName("venuePage")
        val venuePage: VenuePage
)