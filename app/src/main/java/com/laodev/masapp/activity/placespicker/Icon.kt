package com.laodev.masapp.activity.placespicker

import com.sinch.gson.annotations.SerializedName

data class Icon(
        @SerializedName("prefix")
        val prefix: String,
        @SerializedName("suffix")
        val suffix: String
) {
    fun getIcon(size: Int) = "$prefix$size$suffix"
}