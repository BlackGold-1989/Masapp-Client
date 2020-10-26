package com.laodev.masapp.activity.placespicker

import android.location.Location
import com.google.android.gms.maps.model.LatLng

fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)
fun LatLng.toLatLngString(): String = "${this.latitude},${this.longitude}"
