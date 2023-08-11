package com.tejas.forecastzen.Model

import com.google.gson.annotations.SerializedName

data class ModelClass(
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("main") val main: Main,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("clouds") val clouds: Clouds,
    @SerializedName("sys") val sys: Sys,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)