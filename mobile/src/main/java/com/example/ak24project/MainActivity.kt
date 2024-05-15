package com.example.ak24project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ak24project.network.Api
import com.example.ak24project.ui.navigation.BottomNavigationBar
import com.example.ak24project.ui.screens.FriendsScreen
import com.example.ak24project.ui.screens.HistoryScreen
import com.example.ak24project.ui.screens.HomeScreen
import com.example.ak24project.ui.screens.login.alreadyLoggedIn
import com.example.compose.AppTheme
import com.google.android.gms.common.SignInButton
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.sql.Date

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {

    private lateinit var dataClient: DataClient

    private val viewModel = UserDataViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        dataClient = Wearable.getDataClient(this).apply {
            addListener(this@MainActivity)
        }

        //ChatGPT
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        val sessionKey = intent.getStringExtra("sessionKey") ?: ""

        viewModel.setSessionKey(sessionKey)
        viewModel.isSessionExpired(this)
        viewModel.getUserData(atLaunch = true)

        setContent {
            AppTheme(){
                Surface{
                    MyApp(viewModel)
                }
            }
        }
    }

    @Composable
    fun MyApp(viewModel: UserDataViewModel) {

        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
            //containerColor = Color(0xFF4464AD)
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize())
                //.background(Color(0xFF4464AD)))
            {
                NavHost(navController, startDestination = "home", Modifier.padding(paddingValues)) {
                    composable("friends") { FriendsScreen(viewModel) }
                    composable("home") { HomeScreen(viewModel) }
                    composable("history") { HistoryScreen(viewModel) }
                }
            }
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        showToast("onDataChanged at Phone")
        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path
            if (event.type == DataEvent.TYPE_CHANGED && path == "/fitness_data") {
                val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                val clientDataMap = dataMapItem.dataMap.getDataMap("clientData")

                if (clientDataMap != null){
                    val heartRate = clientDataMap.getDouble("heartRate")
                    val distance = clientDataMap.getDouble("distance")
                    val duration = clientDataMap.getLong("duration")
                    val date = clientDataMap.getString("date")

                    viewModel.processData(heartRate,distance,duration,date)
                    //Send data to Database instead of adding to the list
                    //Convert the numbers to right value

                    runOnUiThread {
                        showToast("Heart Rate: $heartRate, Distance: $distance, Duration: $duration, Date: $date")
                    }
                }

                clientDataMap?.let {



                } ?: run {
                    Log.w("MainActivity", "Received dataMap is null")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        dataClient.removeListener(this)
    }
}


