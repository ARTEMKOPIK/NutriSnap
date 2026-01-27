package com.nutrisnap.ui.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrisnap.R
import com.nutrisnap.ui.viewmodel.MainUiState
import com.nutrisnap.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionName")
fun MainScreen(viewModel: MainViewModel) {
    var showCamera by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is MainUiState.Loading

    LaunchedEffect(uiState) {
        val currentState = uiState
        when (currentState) {
            is MainUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.analysis_success, currentState.data.dishName),
                )
            }
            is MainUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.analysis_error, currentState.message),
                )
            }
            else -> {}
        }
    }

    if (showCamera) {
        CameraScreen(
            onImageCaptured = { _ ->
                showCamera = false
                // Handle image analysis
            },
            onClose = { showCamera = false },
            onError = { showCamera = false },
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.app_name),
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                    )
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Daily Summary Card (extracted to localize recomposition)
            DailySummaryCard(viewModel)

            // Fixed-height container to prevent layout jank when loading
            Box(
                modifier = Modifier.height(72.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (uiState is MainUiState.Loading) {
                    CircularProgressIndicator()
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Text(
                stringResource(R.string.what_did_you_eat),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(24.dp))

            val comingSoonMessage = stringResource(R.string.coming_soon)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ActionButton(
                    Icons.Default.PhotoCamera,
                    stringResource(R.string.camera),
                    enabled = !isLoading,
                ) { showCamera = true }
                ActionButton(
                    Icons.Default.PhotoLibrary,
                    stringResource(R.string.gallery),
                    enabled = !isLoading,
                ) {
                    scope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                }
                ActionButton(
                    Icons.Default.Mic,
                    stringResource(R.string.voice),
                    enabled = !isLoading,
                ) {
                    scope.launch { snackbarHostState.showSnackbar(comingSoonMessage) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                placeholder = { Text(stringResource(R.string.food_description_hint)) },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions =
                    KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.analyzeFood(text = inputText)
                                inputText = ""
                            }
                        },
                    ),
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (inputText.isNotBlank() && !isLoading) {
                            IconButton(onClick = { inputText = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear),
                                )
                            }
                        }
                        IconButton(
                            enabled = !isLoading && inputText.isNotBlank(),
                            onClick = {
                                viewModel.analyzeFood(text = inputText)
                                inputText = ""
                            },
                        ) {
                            Icon(Icons.Default.Send, contentDescription = stringResource(R.string.send))
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
@Suppress("FunctionName")
fun DailySummaryCard(viewModel: MainViewModel) {
    val dailyStats by viewModel.dailyStats.collectAsState()

    val animatedCalories by animateIntAsState(targetValue = dailyStats.calories, label = "calories")
    val animatedProteins by animateFloatAsState(targetValue = dailyStats.proteins.toFloat(), label = "proteins")
    val animatedFats by animateFloatAsState(targetValue = dailyStats.fats.toFloat(), label = "fats")
    val animatedCarbs by animateFloatAsState(targetValue = dailyStats.carbs.toFloat(), label = "carbs")

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.today), style = MaterialTheme.typography.titleMedium)
            Text(
                "$animatedCalories",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(stringResource(R.string.calories), style = MaterialTheme.typography.labelLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                MacroInfo(stringResource(R.string.proteins), "${animatedProteins.toInt()}г")
                MacroInfo(stringResource(R.string.fats), "${animatedFats.toInt()}г")
                MacroInfo(stringResource(R.string.carbs), "${animatedCarbs.toInt()}г")
            }
        }
    }
}

@Composable
@Suppress("FunctionName")
fun MacroInfo(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
@Suppress("FunctionName")
fun ActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}
