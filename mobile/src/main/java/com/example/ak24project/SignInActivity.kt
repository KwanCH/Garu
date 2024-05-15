package com.example.ak24project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ak24project.ui.screens.login.ForgotPwdScreen
import com.example.ak24project.ui.screens.login.SignInScreen
import com.example.ak24project.ui.screens.login.SignUpScreen
import com.example.compose.AppTheme


class SignInActivity : ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(true)
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        setContent {

            AppTheme{
                Surface{
                    MyApp()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        val message = intent.getStringExtra("message") ?: ""

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
                )
            },
            //containerColor = Color(0xFF4464AD)
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from Scaffold to ensure content doesn't overlap with the TopAppBar
            ) {
                NavHost(navController = navController, startDestination = "Sign in") {
                    composable("Sign in") { SignInScreen(navController, message) }
                    composable("Sign up") { SignUpScreen(navController) }
                    composable("Forgot password") { ForgotPwdScreen() }
                }
            }
        }
    }

}