package com.example.personalfinancemanager.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitInstance {

    // IMPORTANT: Use 10.0.2.2 for the Android Emulator to connect to your computer's localhost.
    // If you are testing on a REAL device, you must use your computer's network IP address
    // (e.g., "http://192.168.1.100:5000/").
    private const val BASE_URL = "http://10.36.36.223:5000/"

    // Create a logger to see network request details in Logcat (very useful for debugging)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}