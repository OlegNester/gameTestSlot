package com.rikoriko.rctst.Interface

import com.rikoriko.rctst.model.DataGit
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GitServices {
    @GET("apps")
    fun getDataGit(@Query("app_id") appId: String?): Call<DataGit>
}