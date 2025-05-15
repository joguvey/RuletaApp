package com.example.ruletaapp

import kotlin.jvm.JvmSuppressWildcards
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

@JvmSuppressWildcards
interface FirestoreApi {
    @Headers("Content-Type: application/json")
    @GET("top_mundial")
    fun getPuntuacionsFirestore(): Call<FirestoreResponse>
}
