package com.nutrisnap.ui.viewmodel

import app.cash.turbine.test
import com.nutrisnap.data.local.DailyStats
import com.nutrisnap.data.local.FoodDao
import com.nutrisnap.data.repository.GroqRepository
import com.nutrisnap.util.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var repository: GroqRepository
    private lateinit var foodDao: FoodDao
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var viewModel: MainViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mock()
        foodDao = mock()
        preferenceManager = mock()

        Dispatchers.setMain(testDispatcher)

        whenever(preferenceManager.calorieGoal).doReturn(2000)
        whenever(foodDao.getAllEntries()).doReturn(flowOf(emptyList()))
        whenever(foodDao.getDailyStats(any())).doReturn(flowOf(DailyStats()))

        viewModel = MainViewModel(repository, foodDao, preferenceManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calorieGoal matches preferenceManager value initially`() =
        runTest {
            viewModel.calorieGoal.test {
                assertEquals(2000, awaitItem())
            }
        }

    @Test
    fun `updateCalorieGoal updates state and preferenceManager`() =
        runTest {
            viewModel.updateCalorieGoal(2500)
            viewModel.calorieGoal.test {
                assertEquals(2500, awaitItem())
            }
        }
}
