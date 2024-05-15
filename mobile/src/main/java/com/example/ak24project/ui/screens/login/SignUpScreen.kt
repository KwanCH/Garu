package com.example.ak24project.ui.screens.login

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.ak24project.MainActivity
import com.example.ak24project.R
import com.example.ak24project.UserDataViewModel
import com.example.ak24project.bitmapToBase64
import com.example.ak24project.drawableToBitmap
import com.example.ak24project.network.Api
import com.example.ak24project.ui.theme.md_theme_light_secondary
import com.example.ak24project.uriToBitmap
import kotlinx.coroutines.launch

import java.io.ByteArrayOutputStream

@Composable
fun SignUpScreen(navController: NavController){

    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current


    val api = Api()
    val currentLifecycleOwner = rememberUpdatedState(lifecycleOwner)
    var showDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }


    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
        }
    )

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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

                if(imageUri != null){
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(5.dp, md_theme_light_secondary, CircleShape)
                    )
                }else{
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.bear_icon),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(5.dp, md_theme_light_secondary, CircleShape)
                    )
                }

                Button(
                    onClick = {
                        pickImageLauncher.launch("image/*")
                    },

                    ) {
                    Text(
                        text = "Pick Profile Picture",
                        //color = Color.Black
                    )
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            text = "Type in your email",
                            //color = Color(0xFFFFFF9E)
                        )
                    },
                    singleLine = true,

                    )

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


                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = {
                        Text(
                            text = "Confirm password",
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,

                )


                Button(
                    onClick = {

                        currentLifecycleOwner.value.lifecycleScope.launch {
                            var bitmap = drawableToBitmap(context, R.drawable.bear_icon)

                            if(imageUri != null){
                                bitmap = uriToBitmap(context, imageUri!!)
                            }

                            val profilePicture = bitmapToBase64(bitmap!!)
                            val response = api.createAccount(email,username,password,confirmPassword,profilePicture)

                            Log.d("Singup", "$response")
                            if(response?.status.equals("success")){
                                showDialog = true
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = response?.errorMessage ?: "Unknown Error",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }
                    },

                    ) {
                    Text(
                        text = "Register",
                    )
                }

                if(showDialog){
                    AccountRegisteredDialog(navController, onDismiss = { showDialog = false })
                }

            }
        }

    }



}

@Composable
fun AccountRegisteredDialog(
    navController: NavController,
    onDismiss: () -> Unit,
){

        Dialog(onDismissRequest = {}) {
            Card(
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Congratulations!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Account registered!",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Before you can log in, you need to verify your email address." +
                                "If you haven't received the verification email, please check your spam or junk folder.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            onDismiss()
                            navController.navigate("Sign in")
                        },
                    ) {
                        Text("Awesome!")
                    }
                }
            }
        }
}




