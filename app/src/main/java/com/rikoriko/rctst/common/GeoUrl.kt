package com.rikoriko.rctst.common

import com.rikoriko.rctst.Interface.GeoServices
import com.rikoriko.rctst.retrofit.GeoClient

object GeoUrl {
    private val GEO_URL = GlobalVariable.GEO_URL
    val geoService: GeoServices
        get() = GeoClient.getGeoClient(GEO_URL).create(GeoServices::class.java)
}