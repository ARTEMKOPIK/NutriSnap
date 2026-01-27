package com.nutrisnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrisnap.data.api.FoodAnalysis
import com.nutrisnap.data.local.FoodDao
import com.nutrisnap.data.local.FoodEntry
import com.nutrisnap.data.repository.GroqRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class MainViewModel(
    private val repository: GroqRepository,
    private val foodDao: FoodDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    val todayEntries = foodDao.getAllEntries()
        .map { entries ->
            val startOfDay = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            entries.filter { it.timestamp >= startOfDay }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyCalories = todayEntries.map { entries ->
        entries.sumOf { it.calories }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun analyzeFood(text: String? = null, imageBytes: ByteArray? = null) {
        _uiState.value = MainUiState.Loading
        viewModelScope.launch {
            repository.analyze(text, imageBytes)
                .onSuccess { analysis ->
                    saveEntry(analysis)
                    _uiState.value = MainUiState.Success(analysis)
                }
                .onFailure { error ->
                    _uiState.value = MainUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    private fun saveEntry(analysis: FoodAnalysis) {
        viewModelScope.launch {
            foodDao.insertEntry(
                FoodEntry(
                    dishName = analysis.dishName,
                    calories = analysis.calories,
                    proteins = analysis.proteins,
                    fats = analysis.fats,
                    carbs = analysis.carbs,
                    description = analysis.description,
                    aiTip = analysis.aiTip
                )
            )
        }
    }

    fun deleteEntry(entry: FoodEntry) {
        viewModelScope.launch {
            foodDao.deleteEntry(entry)
        }
    }
}

sealed class MainUiState {
    object Idle : MainUiState()
    object Loading : MainUiState()
    data class Success(val data: FoodAnalysis) : MainUiState()
    data class Error(val message: String) : MainUiState()
}
