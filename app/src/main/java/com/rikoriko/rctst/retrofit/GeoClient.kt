package com.rikoriko.rctst.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeoClient {
    private var retrofit: Retrofit? = null

    fun getGeoClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }
}