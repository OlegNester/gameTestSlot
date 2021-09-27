package com.rikoriko.rctst.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GitClient {
    private var retrofit: Retrofit? = null

    private var interceptor = HttpLoggingInterceptor()

    fun getInterceptor(): HttpLoggingInterceptor? {
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }

    var client = OkHttpClient.Builder()
        .addInterceptor(getInterceptor()).cache(null)

    fun getGitClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(SayYesClient.client.build())
                .build()
        }
        return retrofit!!
    }
}