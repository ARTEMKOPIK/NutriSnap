package com.nutrisnap.data.repository

import android.util.Base64
import com.google.gson.Gson
import com.nutrisnap.data.api.Content
import com.nutrisnap.data.api.FoodAnalysis
import com.nutrisnap.data.api.GroqApiService
import com.nutrisnap.data.api.GroqRequest
import com.nutrisnap.data.api.ImageUrl
import com.nutrisnap.data.api.Message
import com.nutrisnap.data.api.ResponseFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroqRepository(private val apiService: GroqApiService, private val apiKey: String) {
    private val gson = Gson()

    private val systemPrompt =
        """
        Ты — эксперт по диетологии и анализу еды NutriSnap.
        Твоя задача — анализировать изображения еды или текстовые описания и возвращать точные данные о калориях и макронутриентах.

        ОБЯЗАТЕЛЬНО возвращай ответ ТОЛЬКО в формате JSON следующей структуры:
        {
          "dishName": "Название блюда на русском",
          "calories": 123,
          "proteins": 10.5,
          "fats": 5.0,
          "carbs": 20.0,
          "description": "Краткое описание ингредиентов",
          "aiTip": "Полезный совет по этому приему пищи"
        }

        Всегда отвечай на русском языке. Если на фото не еда, вежливо сообщи об этом в поле description, а в полях цифр поставь 0.
        """.trimIndent()

    suspend fun analyze(
        text: String?,
        imageBytes: ByteArray?,
    ): Result<FoodAnalysis> =
        withContext(Dispatchers.IO) {
            try {
                val contentList = mutableListOf<Content>()

                // Choose model based on input type
                val modelId =
                    if (imageBytes != null) {
                        "meta-llama/llama-4-maverick-17b-128e-instruct"
                    } else {
                        "llama-3.3-70b-versatile"
                    }

                val fullTextPrompt = if (text.isNullOrBlank()) systemPrompt else "$systemPrompt\n\nПользователь добавил описание: $text"
                contentList.add(Content(type = "text", text = fullTextPrompt))

                if (imageBytes != null) {
                    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                    contentList.add(
                        Content(
                            type = "image_url",
                            imageUrl = ImageUrl(url = "data:image/jpeg;base64,$base64Image"),
                        ),
                    )
                }

                val request =
                    GroqRequest(
                        model = modelId,
                        messages = listOf(Message(role = "user", content = contentList)),
                        responseFormat = ResponseFormat(),
                    )

                val response = apiService.analyzeFood("Bearer $apiKey", request)
                val jsonContent =
                    response.choices.firstOrNull()?.message?.content ?: return@withContext Result.failure(
                        Exception("Empty response"),
                    )

                val analysis = gson.fromJson(jsonContent, FoodAnalysis::class.java)
                Result.success(analysis)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}
