package com.example.e_catapp.api

import android.content.Context
import com.example.e_catapp.helper.AuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private const val BASE_URL = "https://addin.putraeu.my.id/api/"
    fun Create(context: Context):ApiService{
        val retrofit= Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }


    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}