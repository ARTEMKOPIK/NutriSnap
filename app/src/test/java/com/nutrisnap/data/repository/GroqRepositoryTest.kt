package com.nutrisnap.data.repository

import com.nutrisnap.data.api.Choice
import com.nutrisnap.data.api.GroqApiService
import com.nutrisnap.data.api.GroqResponse
import com.nutrisnap.data.api.ResponseMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GroqRepositoryTest {
    private lateinit var apiService: GroqApiService
    private lateinit var repository: GroqRepository

    @Before
    fun setup() {
        apiService = mock()
        repository = GroqRepository(apiService, "fake_key")
    }

    @Test
    fun `analyze returns success when api call is successful`() =
        runBlocking {
            val jsonResponse =
                """
                {
                  "dishName": "Apple",
                  "calories": 52,
                  "proteins": 0.3,
                  "fats": 0.2,
                  "carbs": 14.0,
                  "description": "A fresh apple",
                  "aiTip": "An apple a day keeps the doctor away"
                }
                """.trimIndent()

            val mockResponse =
                GroqResponse(
                    choices = listOf(Choice(message = ResponseMessage(content = jsonResponse))),
                )

            whenever(apiService.analyzeFood(any(), any())).doReturn(mockResponse)

            val result = repository.analyze("Apple", null)

            assertTrue(result.isSuccess)
            val analysis = result.getOrNull()
            assertEquals("Apple", analysis?.dishName)
            assertEquals(52, analysis?.calories)
        }
}
