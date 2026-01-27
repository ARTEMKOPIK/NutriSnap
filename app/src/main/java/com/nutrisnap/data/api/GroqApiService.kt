package com.nutrisnap.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GroqApiService {
    @POST("v1/chat/completions")
    suspend fun analyzeFood(
        @Header("Authorization") authHeader: String,
        @Body request: GroqRequest,
    ): GroqResponse
}
