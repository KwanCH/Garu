package com.example.ak24project.presentation.screens

// SummaryScreen.kt
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope

import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text
import com.example.ak24project.R
import com.example.ak24project.presentation.ExerciseClientViewModel
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SummaryScreen(navController: NavHostController, context: Context, scope: CoroutineScope, viewModel: ExerciseClientViewModel) {
    Box(){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Image(
                painter = painterResource(id = R.drawable.finishline),
                contentDescription = "Goal",
                modifier = Modifier
                    .size(width = 130.dp, height = 150.dp)
            )
            Button(
                onClick = { sendData(navController, scope, context, viewModel)},
            ){
                Text(text = "OK")
            }
        }

    }
}

fun sendData(navController:NavHostController, scope: CoroutineScope, context: Context, viewModel: ExerciseClientViewModel) {

    Log.d("SummaryScreen", "Sending data!")
    navController.navigate(Screens.StartScreen.route)

    val dataClient = Wearable.getDataClient(context)

    //Preparing the data
    val dataMap = convertToDataMap(viewModel)

    val putDataMapReq: PutDataMapRequest = PutDataMapRequest.create("/fitness_data")


    putDataMapReq.dataMap.putLong("updateTime", System.currentTimeMillis())
    putDataMapReq.dataMap.putDataMap("clientData",dataMap)
    putDataMapReq.setUrgent()
    val putDataReq = putDataMapReq.asPutDataRequest()

    scope.launch {
        withContext(Dispatchers.IO) {
            Tasks.await(dataClient.putDataItem(putDataReq))
            Log.d("SummaryScreen", "Data sent!")

        }
    }
}

fun convertToDataMap(viewModel: ExerciseClientViewModel):DataMap{
    val dataMap = DataMap()

    dataMap.putDouble("heartRate", viewModel.uiState.value.heartRate)
    dataMap.putDouble("distance", viewModel.uiState.value.distance)
    dataMap.putLong("duration", viewModel.uiState.value.duration)
    dataMap.putString("date", viewModel.uiState.value.time)

    return dataMap
}

