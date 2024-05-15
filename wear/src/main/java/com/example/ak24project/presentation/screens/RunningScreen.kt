package com.example.ak24project.presentation.screens

// RunningSessionScreen.kt

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ButtonDefaults
import com.example.ak24project.R
import com.example.ak24project.presentation.ExerciseClientViewModel
import com.example.ak24project.presentation.ExerciseService
import com.google.android.gms.wearable.DataMap


@Composable
fun RunningScreen(navController: NavHostController,viewModel: ExerciseClientViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    val totalSeconds = state.duration / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(space = 10.dp)
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = ImageVector.vectorResource(id = R.drawable.heart_rate), contentDescription = "Heart Rate")
                Text(text = "Heart rate:${"%.2f".format(state.heartRate)}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(ImageVector.vectorResource(id = R.drawable.sprint), contentDescription = "Distance")
                Text(text = "Distance:${"%.2f".format(state.distance)} m")
            }
            Row(
                modifier = Modifier.padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(ImageVector.vectorResource(id = R.drawable.timer), contentDescription = "Time Elapsed")
                Text(text = "Time Elapsed:${String.format("%02d:%02d:%02d", hours, minutes, seconds)}")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomWearButtonIcon("PAUSE", onClick = { viewModel.pauseExercise() }, R.drawable.pause)
            CustomWearButtonIcon("RESUME", onClick = { viewModel.resumeExercise() }, R.drawable.resume)
            CustomWearButtonText("FINISH", onClick = { finishExercise(navController, viewModel, context) })
        }
    }
}

fun finishExercise(navController: NavHostController, viewModel: ExerciseClientViewModel, context: Context){
    val serviceIntent = Intent(context, ExerciseService::class.java).apply {
        action = ExerciseService.STOP
    }

    context.startService(serviceIntent)

    viewModel.endExercise()

    navController.navigate(Screens.SummaryScreen.route)


/*    Log.d("TheService", " ---- Check ----")
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        Log.d("RunningScreen", service.service.className)
    }
    Log.d("TheService", "Finish checking for active service - after removing")*/
}

@Composable
fun CustomWearButtonText(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 60.dp, height = 44.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black)
        }
    }
}
@Composable
fun CustomWearButtonIcon(
    text: String,
    onClick: () -> Unit,
    iconID: Int
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(width = 60.dp, height = 44.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White  // Ensure the content (icon and text) inside the button is white
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = iconID),
                contentDescription = text,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}


