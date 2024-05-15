/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.ak24project

import android.Manifest

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable


import androidx.health.services.client.HealthServices
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ak24project.presentation.ExerciseClientViewModel
import com.example.ak24project.presentation.screens.RunningScreen
import com.example.ak24project.presentation.screens.Screens
import com.example.ak24project.presentation.screens.StartScreen
import com.example.ak24project.presentation.screens.SummaryScreen
import com.example.ak24project.presentation.theme.theme.WearAppTheme


class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val healthClient = HealthServices.getClient(this.applicationContext)
        val exerciseClient = healthClient.exerciseClient

        val dateClient = Wearable.getDataClient(this).apply {
            addListener(this@MainActivity)
        }


        val viewModel = ExerciseClientViewModel(exerciseClient)

        setContent{
            WearAppTheme {
                Surface{
                    requestPermission()
                    WearApp(viewModel)
                }
            }
        }

    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Handle the permissions request response
        val allPermissionsGranted = permissions.entries.all { it.value }
        if (allPermissionsGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_LONG).show()
        }
    }



    private fun requestPermission() {
        val requiredPermissions = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val allPermissionsGranted = requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allPermissionsGranted) {
            requestPermissionLauncher.launch(requiredPermissions)
        } else {
            Log.d("MainActivity","\"All permissions are already granted\"")
            //Toast.makeText(this, "All permissions are already granted", Toast.LENGTH_LONG).show()
        }
    }



    override fun onDataChanged(dataEvents: DataEventBuffer) {
        // Handle received data items
        // Example: Update UI based on received data
    }

}


@Composable
fun WearApp(viewModel: ExerciseClientViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        NavHost(navController = navController, startDestination = Screens.StartScreen.route) {
            composable(Screens.StartScreen.route) { StartScreen(navController, viewModel) }
            composable(Screens.RunningScreen.route) { RunningScreen(navController, viewModel) }
            composable(Screens.SummaryScreen.route) { SummaryScreen(navController, context, coroutineScope, viewModel) }
        }
    }
}

