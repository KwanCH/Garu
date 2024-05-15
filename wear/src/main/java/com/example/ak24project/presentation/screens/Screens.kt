package com.example.ak24project.presentation.screens

// Screen.kt
sealed class Screens(val route: String) {
    object StartScreen : Screens("StartScreen")
    object RunningScreen : Screens("RunningScreen")
    object SummaryScreen : Screens("SummaryScreen")
}