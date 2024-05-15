package com.example.ak24project.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.health.services.client.*
import androidx.health.services.client.data.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

import androidx.health.services.client.ExerciseClient
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.ak24project.presentation.screens.Screens
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ExerciseViewState(
    val capabilities: ExerciseCapabilities? = null,
    val exerciseStarted: Boolean = false,
    val exercisePaused: Boolean = false,
    val duration: Long = 0L,
    val heartRate: Double = 0.0,
    val distance: Double = 0.0,
    val time: String = ""

)

class ExerciseClientViewModel (
    private val exerciseClient: ExerciseClient
): ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseViewState())
    val uiState: StateFlow<ExerciseViewState> = _uiState

    private val exerciseCallback = object : ExerciseUpdateCallback {

        override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
            if (update.exerciseStateInfo.state.isEnded) {
                // Workout has either been ended by the user, or otherwise terminated
                Log.d("TAG","Session terminated")
            }

            val exerciseStateInfo = update.exerciseStateInfo
            val activeDuration = update.activeDurationCheckpoint
            val latestMetrics = update.latestMetrics

            val (inProgress, paused) = if (exerciseStateInfo.state.isResuming){
                Pair(true,false)
            } else if (exerciseStateInfo.state.isResuming){
                Pair(true, false)
            } else if (exerciseStateInfo.state.isEnded){
                Pair(true, false)
            } else if (exerciseStateInfo.state == ExerciseState.ACTIVE) {
                Pair(true, false)
            } else {
                Pair(false, false)
            }

            if(inProgress){
                val duration = if (activeDuration != null){
                    (
                            (Instant.now().toEpochMilli() - activeDuration.time.toEpochMilli()) +
                                    activeDuration.activeDuration.toMillis()
                    )
                } else 0

                _uiState.value = _uiState.value.copy(
                    exerciseStarted = true,
                    exercisePaused = paused,
                    duration = duration,
                    heartRate = latestMetrics.getData(DataType.HEART_RATE_BPM_STATS)?.average ?: _uiState.value.heartRate,
                    distance = latestMetrics.getData(DataType.DISTANCE_TOTAL)?.total ?: _uiState.value.distance
                )

                //Writing data from sensor to console
                Log.d("ExerciseViewClient", _uiState.value.heartRate.toString())
                Log.d("ExerciseViewClient", _uiState.value.duration.toString())
                Log.d("ExerciseViewClient", _uiState.value.distance.toString())

            } else {

                _uiState.value = _uiState.value.copy(
                    exerciseStarted = true,
                    exercisePaused = paused,
                    duration = _uiState.value.duration,
                    heartRate = _uiState.value.heartRate,
                    distance = _uiState.value.distance
                )

            }

        }


        override fun onLapSummaryReceived(lapSummary: ExerciseLapSummary) {

        }

        override fun onRegistered() {
            // Stub implementation, you can log or leave it empty if there's nothing to do
            Log.d("ExerciseViewClient", "ExerciseUpdateCallback registered")
        }

        override fun onRegistrationFailed(throwable: Throwable) {
            Log.d("ExerciseViewClient", "Register ExerciseUpdateCallback failed")

        }

        override fun onAvailabilityChanged(
            dataType: DataType<*, *>,
            availability: Availability
        ) {
            // Called when the availability of a particular DataType changes.

/*            when (availability // Relates to Location/GPS.
            ) {
                is LocationAvailability -> trySendBlocking(ExerciseMessage.LocationAvailabilityMessage(availability))
            }*/

        }

    }


    init {
        getCapabilities()
    }



    private fun getCapabilities(){
        viewModelScope.launch {
            val capabilities = exerciseClient.getCapabilities()
            _uiState.value = _uiState.value.copy(capabilities = capabilities)
        }
    }


    @SuppressLint("RestrictedApi")
    fun maybeStartExercise(exerciseType: ExerciseType) {
        viewModelScope.launch {
            val exerciseInfo = exerciseClient.getCurrentExerciseInfo()
            val canStart = when (exerciseInfo.exerciseTrackedStatus) {
                ExerciseTrackedStatus.OTHER_APP_IN_PROGRESS -> false
                ExerciseTrackedStatus.OWNED_EXERCISE_IN_PROGRESS -> false
                ExerciseTrackedStatus.NO_EXERCISE_IN_PROGRESS -> true
                else -> false
            }
            if (canStart){
                Log.d("ExerciseViewClient", "Starting session!")
                startExercise(exerciseType)
                Log.d("ExerciseViewClient", "Session started!")

            }
        }
    }

    private suspend fun startExercise(exerciseType: ExerciseType){
        val dataTypes = setOf(
            DataType.HEART_RATE_BPM,
            DataType.HEART_RATE_BPM_STATS,
            DataType.DISTANCE_TOTAL,
        )

        val exerciseConfig = ExerciseConfig(
            exerciseType = ExerciseType.RUNNING,
            dataTypes = dataTypes,
            isAutoPauseAndResumeEnabled = false,
            isGpsEnabled = true,
            exerciseGoals = mutableListOf()
        )
        exerciseClient.setUpdateCallback(callback = exerciseCallback)
        exerciseClient.startExercise(exerciseConfig)
    }

    fun endExercise() {
        Log.d("ExerciseViewClient","Ending exercise")
        viewModelScope.launch {
            exerciseClient.endExercise()
            val currentDateTime = LocalDateTime.now()

            val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val time = currentDateTime.format(timeFormatter)
            _uiState.value = _uiState.value.copy(
                duration = _uiState.value.duration,
                heartRate = _uiState.value.heartRate,
                distance = _uiState.value.distance,
                time = time)

        }

    }

    fun pauseExercise() {
        Log.d("ExerciseViewClient","Pausing exercise")
        viewModelScope.launch {
            exerciseClient.pauseExercise()

        }
        Log.d("ExerciseViewClient","Paused exercise")
    }

    fun resumeExercise() {
        Log.d("ExerciseViewClient","Resuming exercise")
        viewModelScope.launch {
            exerciseClient.resumeExercise()
        }

    }

    fun convertToDataMap(time: String):DataMap{
        val dataMap = DataMap()

        dataMap.putDouble("heartRate", _uiState.value.heartRate)
        dataMap.putDouble("distance", _uiState.value.distance)
        dataMap.putLong("duration", _uiState.value.duration)
        dataMap.putString("date", time)

        return dataMap
    }


}