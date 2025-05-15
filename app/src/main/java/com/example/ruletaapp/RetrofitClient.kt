package com.example.ruletaapp

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://firestore.googleapis.com/v1/projects/ruletillaapp/databases/(default)/documents/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @JvmStatic
    fun getApi(): FirestoreApi {
        return retrofit.create(FirestoreApi::class.java)
    }

}
