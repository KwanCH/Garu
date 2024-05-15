package com.example.ak24project.ui.navigation

import androidx.annotation.DrawableRes
import com.example.ak24project.R

sealed class Screen(val route: String, @DrawableRes val iconRes:Int, val label: String) {
    object Friends : Screen("friends", R.drawable.friends_logo, "Friends")

    object Home : Screen("home", R.drawable.home_app_logo, "Home")

    object History : Screen("history", R.drawable.history_logo, "History")


}