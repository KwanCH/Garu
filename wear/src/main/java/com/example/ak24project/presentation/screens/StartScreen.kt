package com.example.ak24project.presentation.screens

// StartScreen.kt
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.health.services.client.data.ExerciseType
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.example.ak24project.presentation.ExerciseClientViewModel
import com.example.ak24project.presentation.ExerciseService


@Composable
fun StartScreen(navController: NavHostController, viewModel: ExerciseClientViewModel) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Button(onClick = {
            startSession(navController,viewModel,context)
        },
            modifier = Modifier
                .align(Alignment.Center)
                .width(180.dp)) {
            Text(text = "START")
        }
    }
}

fun startSession(navController: NavController, viewModel: ExerciseClientViewModel, context: Context){
    val serviceIntent = Intent(context, ExerciseService::class.java).apply {
        action = ExerciseService.START
    }

    context.startService(serviceIntent)

    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    Log.d("TheService", " ---- Check ----")
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        Log.d("TheService", service.service.className)
    }
    Log.d("TheService", "Finish checking for Active Service - after")



    navController.navigate(Screens.RunningScreen.route)
    viewModel.maybeStartExercise(ExerciseType.RUNNING)

}