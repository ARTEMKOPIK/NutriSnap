package com.nutrisnap.data.api

import com.google.gson.annotations.SerializedName

data class GroqRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.1,
    @SerializedName("response_format") val responseFormat: ResponseFormat? = null,
)

data class ResponseFormat(
    val type: String = "json_object",
)

data class Message(
    val role: String,
    val content: List<Content>,
)

data class Content(
    val type: String,
    val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrl? = null,
)

data class ImageUrl(
    val url: String,
)

data class GroqResponse(
    val choices: List<Choice>,
)

data class Choice(
    val message: ResponseMessage,
)

data class ResponseMessage(
    val content: String,
)

// Model for the parsed food data
data class FoodAnalysis(
    val dishName: String,
    val calories: Int,
    val proteins: Float,
    val fats: Float,
    val carbs: Float,
    val description: String,
    val aiTip: String,
)
