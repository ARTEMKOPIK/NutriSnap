package com.nutrisnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrisnap.data.api.FoodAnalysis
import com.nutrisnap.data.local.DailyStats
import com.nutrisnap.data.local.FoodDao
import com.nutrisnap.data.local.FoodEntry
import com.nutrisnap.data.repository.GroqRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repository: GroqRepository,
    private val foodDao: FoodDao,
) : ViewModel() {
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private fun getStartOfDay(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    // Refresh startOfDay every hour to handle midnight transition
    private val startOfDayFlow =
        flow {
            while (true) {
                emit(getStartOfDay())
                delay(3600_000)
            }
        }

    val dailyStats =
        startOfDayFlow.flatMapLatest { start ->
            foodDao.getDailyStats(start)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyStats())

    // todayEntries kept for potential future use or other UI components not seen
    val todayEntries =
        startOfDayFlow.flatMapLatest { start ->
            foodDao.getEntriesAfter(start)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun analyzeFood(
        text: String? = null,
        imageBytes: ByteArray? = null,
    ) {
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
                    aiTip = analysis.aiTip,
                ),
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
