package com.rikoriko.rctst.Interface

import com.rikoriko.rctst.model.DataGeo
import retrofit2.Call
import retrofit2.http.GET

interface GeoServices {
    @GET("json.gp")
    fun getDataGeo(): Call<DataGeo>
}