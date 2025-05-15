package com.example.ruletaapp

import retrofit2.http.GET

interface FirebaseApi {
    @GET("top_mundial.json")
    suspend fun getPuntuacions(): Map<String, Puntuacio>
}
