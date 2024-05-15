package com.example.ak24project.ui.screens.login

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavHostController
import com.example.ak24project.MainActivity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.ak24project.network.Api
import kotlinx.coroutines.launch

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.io.FileInputStream
import java.io.FileNotFoundException


@Composable
fun SignInScreen(navController: NavHostController, message: String) {
    val context = LocalContext.current
    //Check if user has already logged in previously
    val api = Api()
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val currentLifecycleOwner = rememberUpdatedState(lifecycleOwner)


    val focusManager = LocalFocusManager.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    action = {
                        IconButton(onClick = { data.dismiss() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                //tint = Color.White
                            )
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp)
                    )
                }
            }
        },
        //containerColor = Color(0xFF4464AD)
    ) { paddingValues ->

        if(message.isNotEmpty()){
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }else{
            alreadyLoggedIn(context)
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { /* Detect press but do nothing to allow for normal component interaction */ },
                        onTap = {
                            // Clear focus when the Box is tapped
                            focusManager.clearFocus()
                        }
                    )
                }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = {
                        Text(
                            text = "Type in username",
                            //color = Color(0xFFFFFF9E)
                        )
                    },
                    singleLine = true,
                )


                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = {
                        Text(
                            text = "Type in password",
                            //color = Color(0xFFFFFF9E)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                )


                Button(
                    onClick = {
                        currentLifecycleOwner.value.lifecycleScope.launch {

                            Log.d("SingInScreen","Requesting SessionKey from API")
                            val response = api.signin(username, password)
                            val sessionKey = response?.token.toString()


                            if (response?.status.equals("success")) {

                                val fos = context.openFileOutput("token.txt", Context.MODE_PRIVATE)

                                // Storing the token
                                fos.write(sessionKey.toByteArray())
                                fos.flush()
                                fos.close()


                                val intent = Intent(context, MainActivity::class.java).apply {
                                    putExtra("sessionKey", sessionKey)
                                }

                                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                context.startActivity(intent)
                            }

                            snackbarHostState.showSnackbar(
                                message = response?.errorMessage ?: "Unknown Error",
                                duration = SnackbarDuration.Short,
                            )
                        }

                    },
                    //colors = ButtonDefaults.buttonColors(Color(0xFFFFFF9E)),

                    ) {
                    Text(
                        text = "Login",
                        //color = Color.Black
                    )
                }


                Button(
                    onClick = { navController.navigate("Sign up") },
                    //colors = ButtonDefaults.buttonColors(Color(0xFFFFFF9E))

                ) {
                    Text(
                        text = "Sign up",
                        //color = Color.Black
                    )
                }

                ClickableText(
                    text = AnnotatedString("Forgot password?"),
                    //style = TextStyle(color = Color(0xFFFFFF9E)),
                    onClick = { navController.navigate("Forgot password") }
                )
            }
        }
    }

}

fun alreadyLoggedIn(context:Context) {

    val fin: FileInputStream?
    try {
        fin = context.openFileInput("token.txt")

        var a: Int
        val temp = StringBuilder()

        while (fin!!.read().also { a = it } != -1) {
            temp.append(a.toChar())
        }

        val sessionKey = temp.toString()
        fin.close()

        if(sessionKey != ""){

            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("sessionKey", sessionKey)
            }
            context.startActivity(intent)
        }

    } catch (e: FileNotFoundException){

        Log.d("SingInScreen", "Error:$e")

    }

}