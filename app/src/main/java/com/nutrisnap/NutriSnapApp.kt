package com.nutrisnap

import android.app.Application
import androidx.room.Room
import com.nutrisnap.data.api.GroqApiService
import com.nutrisnap.data.local.AppDatabase
import com.nutrisnap.data.repository.GroqRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NutriSnapApp : Application() {

    lateinit var database: AppDatabase
    lateinit var repository: GroqRepository

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Database
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "nutrisnap_db"
        ).build()

        // Initialize API and Repository
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.groq.com/openai/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(GroqApiService::class.java)
        val apiKey = BuildConfig.GROQ_API_KEY
        
        repository = GroqRepository(apiService, apiKey)
    }
}
