package com.nutrisnap.ui.main

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.nutrisnap.R
import com.nutrisnap.data.local.FoodEntry
import com.nutrisnap.ui.viewmodel.MainUiState
import com.nutrisnap.ui.viewmodel.MainViewModel
import com.nutrisnap.util.PdfExporter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Suppress("FunctionName")
fun MainScreen(viewModel: MainViewModel) {
    var showCamera by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            uri?.let { viewModel.analyzeFoodWithUri(context, it) }
        }

    val voiceLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val spokenText = results?.firstOrNull()
                if (!spokenText.isNullOrBlank()) {
                    viewModel.analyzeFood(text = spokenText)
                }
            }
        }

    val uiState by viewModel.uiState.collectAsState()
    val recentEntries by viewModel.recentEntries.collectAsState()
    val isLoading = uiState is MainUiState.Loading

    LaunchedEffect(uiState) {
        val currentState = uiState
        when (currentState) {
            is MainUiState.Success -> {
                val result =
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.analysis_success, currentState.data.dishName),
                        actionLabel = context.getString(R.string.undo),
                    )
                if (result == SnackbarResult.ActionPerformed) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.undoLastAction()
                }
            }
            is MainUiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = context.getString(R.string.analysis_error, currentState.message),
                )
            }
            else -> {}
        }
    }

    if (showSettingsDialog) {
        val currentGoal by viewModel.calorieGoal.collectAsState()
        var tempGoal by remember { mutableStateOf(currentGoal.toString()) }

        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(R.string.set_calorie_goal)) },
            text = {
                OutlinedTextField(
                    value = tempGoal,
                    onValueChange = { if (it.all { char -> char.isDigit() }) tempGoal = it },
                    label = { Text(stringResource(R.string.calories)) },
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onDone = {
                                val goal = tempGoal.toIntOrNull()
                                if (goal != null && goal > 0) {
                                    viewModel.updateCalorieGoal(goal)
                                    showSettingsDialog = false
                                }
                            },
                        ),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val goal = tempGoal.toIntOrNull()
                    if (goal != null && goal > 0) {
                        viewModel.updateCalorieGoal(goal)
                        showSettingsDialog = false
                    }
                }) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showCamera) {
        CameraScreen(
            onImageCaptured = { uri ->
                showCamera = false
                viewModel.analyzeFoodWithUri(context, uri)
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
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                    IconButton(onClick = {
                        val pdfExporter = PdfExporter(context)
                        val file = pdfExporter.exportToPdf(recentEntries)
                        if (file != null) {
                            val uri =
                                FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.provider",
                                    file,
                                )
                            val intent =
                                Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.export_pdf)))
                        }
                    }) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = stringResource(R.string.export_pdf))
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                GreetingHeader()
            }

            item {
                // Daily Summary Card (extracted to localize recomposition)
                DailySummaryCard(viewModel)
            }

            item {
                // Fixed-height container to prevent layout jank when loading
                Box(
                    modifier = Modifier.height(72.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    }
                }
            }

            item {
                // Action Buttons
                Text(
                    stringResource(R.string.what_did_you_eat),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                        )
                    }
                    ActionButton(
                        Icons.Default.Mic,
                        stringResource(R.string.voice),
                        enabled = !isLoading,
                    ) {
                        val intent =
                            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                                )
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, context.getString(R.string.food_description_hint))
                            }
                        try {
                            voiceLauncher.launch(intent)
                        } catch (e: Exception) {
                            scope.launch {
                                snackbarHostState.showSnackbar(context.getString(R.string.analysis_error, e.message ?: ""))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
            }

            item {
                Text(
                    stringResource(R.string.history_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 32.dp, bottom = 8.dp),
                )
            }

            if (recentEntries.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.empty_history),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(32.dp),
                    )
                }
            } else {
                items(recentEntries, key = { it.id }) { entry ->
                    HistoryItem(entry, onDelete = { viewModel.deleteEntry(entry) })
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
@Suppress("FunctionName")
fun GreetingHeader() {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting =
        when (hour) {
            in 5..11 -> stringResource(R.string.good_morning)
            in 12..17 -> stringResource(R.string.good_afternoon)
            in 18..22 -> stringResource(R.string.good_evening)
            else -> stringResource(R.string.good_night)
        }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
@Suppress("FunctionName")
fun DailySummaryCard(viewModel: MainViewModel) {
    val dailyStats by viewModel.dailyStats.collectAsState()
    val calorieGoal by viewModel.calorieGoal.collectAsState()

    val animatedCalories by animateIntAsState(targetValue = dailyStats.calories, label = "calories")
    val progress = (dailyStats.calories.toFloat() / calorieGoal).coerceIn(0f, 1.2f)
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), label = "progress")

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
                )
                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    color = if (progress > 1f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round,
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$animatedCalories",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        stringResource(R.string.calories_unit),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            Spacer(modifier = Modifier.width(24.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.calorie_goal, calorieGoal),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MacroInfoSmall(
                        stringResource(R.string.proteins).take(1),
                        "${dailyStats.proteins.toInt()}г",
                        MaterialTheme.colorScheme.primary,
                    )
                    MacroInfoSmall(
                        stringResource(R.string.fats).take(1),
                        "${dailyStats.fats.toInt()}г",
                        MaterialTheme.colorScheme.secondary,
                    )
                    MacroInfoSmall(
                        stringResource(R.string.carbs).take(1),
                        "${dailyStats.carbs.toInt()}г",
                        MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }
    }
}

@Composable
@Suppress("FunctionName")
fun MacroInfoSmall(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
@Suppress("FunctionName")
fun HistoryItem(
    entry: FoodEntry,
    onDelete: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val sdf = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeStr = sdf.format(Date(entry.timestamp))

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { expanded = !expanded },
    ) {
        ListItem(
            headlineContent = { Text(entry.dishName, fontWeight = FontWeight.SemiBold) },
            supportingContent = {
                Text("${entry.calories} ${stringResource(R.string.calories_unit)} • $timeStr")
            },
            trailingContent = {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            },
        )
        if (expanded) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    MacroInfo(stringResource(R.string.proteins), "${entry.proteins}г")
                    MacroInfo(stringResource(R.string.fats), "${entry.fats}г")
                    MacroInfo(stringResource(R.string.carbs), "${entry.carbs}г")
                }
                if (entry.aiTip.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = entry.aiTip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    )
                }
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
