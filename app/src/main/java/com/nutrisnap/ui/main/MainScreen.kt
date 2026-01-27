package com.nutrisnap.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrisnap.R
import com.nutrisnap.ui.viewmodel.MainViewModel
import com.nutrisnap.ui.viewmodel.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var showCamera by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    
    val dailyCalories by viewModel.dailyCalories.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val todayEntries by viewModel.todayEntries.collectAsState()

    if (showCamera) {
        CameraScreen(
            onImageCaptured = { uri ->
                showCamera = false
                // Handle image analysis
            },
            onError = { showCamera = false }
        )
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "NutriSnap", 
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Daily Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Сегодня", style = MaterialTheme.typography.titleMedium)
                    Text("$dailyCalories", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("калорий", style = MaterialTheme.typography.labelLarge)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MacroInfo("Белки", "${todayEntries.sumOf { it.proteins.toInt() }}г")
                        MacroInfo("Жиры", "${todayEntries.sumOf { it.fats.toInt() }}г")
                        MacroInfo("Углеводы", "${todayEntries.sumOf { it.carbs.toInt() }}г")
                    }
                }
            }

            if (uiState is MainUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Text(
                "Что вы съели?", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(Icons.Default.PhotoCamera, "Камера") { showCamera = true }
                ActionButton(Icons.Default.PhotoLibrary, "Галерея") { /* Implement gallery picker */ }
                ActionButton(Icons.Default.Mic, "Голос") { /* Implement STT */ }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Опишите ваш прием пищи...") },
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { 
                        if (inputText.isNotBlank()) {
                            viewModel.analyzeFood(text = inputText)
                            inputText = ""
                        }
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MacroInfo(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}