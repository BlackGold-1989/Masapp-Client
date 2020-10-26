package com.laodev.masapp.activity.placespicker

import com.google.gson.annotations.SerializedName

data class Category(
        @SerializedName("icon")
        val icon: Icon,
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("pluralName")
        val pluralName: String,
        @SerializedName("primary")
        val primary: Boolean,
        @SerializedName("shortName")
        val shortName: String
)