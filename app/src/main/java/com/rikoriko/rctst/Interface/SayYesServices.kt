package com.rikoriko.rctst.Interface

import com.rikoriko.rctst.model.DataSayYes
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SayYesServices {
    @GET("event")
    fun getSayYesModel(@Query("app_id") appId: String?,
                       @Query("hash") hash: String?,
                       @Query("sender") sender: String?): Call<DataSayYes>
}