package com.example.ak24project.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.ak24project.AchievementIcon
import com.example.ak24project.R
import com.example.ak24project.SignInActivity
import com.example.ak24project.UserDataViewModel
import com.example.ak24project.UserDataViewState
import com.example.ak24project.bitmapToBase64
import com.example.ak24project.getPicture
import com.example.ak24project.network.Achievement
import com.example.ak24project.network.Achievements
import com.example.ak24project.network.Api
import com.example.ak24project.ui.theme.md_theme_light_primary
import com.example.ak24project.ui.theme.md_theme_light_secondary
import com.example.ak24project.uriToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import kotlin.math.floor


@Composable
fun HomeScreen(viewModel: UserDataViewModel) {
     val context = LocalContext.current
     val state by viewModel.uiState.collectAsState()
     val userName = state.username
     
     if(state.newlyAchieved.isNotEmpty()){
          NewAchievementAlertDialog(state.newlyAchieved)
     }

     if(state.newLevel.first){
          LevelUpAlertDialog(state.newLevel.second, viewModel)
     }
     
     Box(
          modifier = Modifier
               .padding(top = 50.dp)
               .fillMaxSize()
     ){
          Button(
               modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-30).dp, x = (-10).dp)
                    .size(width = 81.dp, height = 35.dp),
            
               onClick = {

                    try {
                         context.openFileOutput("token.txt", Context.MODE_PRIVATE).use{}
                    }catch(e: FileNotFoundException){
                         Log.d("HomeScreen", "Error $e")
                    }

                    ContextCompat.startActivity(context, Intent(context, SignInActivity::class.java, ), null)
                    viewModel.logout()
                    (context as? Activity)?.finishAffinity()
               }
          ){
               Text("Logout", fontSize = 10.sp)
          }

          Column(
               modifier = Modifier
                    .fillMaxSize(),
               horizontalAlignment = Alignment.CenterHorizontally,
               //verticalArrangement = Arrangement.Center
          ) {

               Text(
                    text = "Overview",
                    fontSize = 50.sp,
                    modifier = Modifier
                         .padding(bottom = 20.dp)
               )

               Text(
                    text = "LVL ${state.levelDetail?.lvl}",
               )
               state.levelDetail?.xp?.let { state.levelDetail?.nextLvl?.let { it1 -> XPBar(it, it1) } }

               if (userName != null) {
                    Text(
                         text = "User: $userName",
                         modifier = Modifier.padding(top = 25.dp)
                    )
               }

               ProfilePicture(viewModel)

               DisplayAchievements(viewModel)

               Text(
                    text = "Stats",
                    fontSize = 20.sp,
                    modifier = Modifier
                         .width(400.dp)
               )


               Card(
                    //colors = CardDefaults.cardColors(
                    //containerColor = Color(0xFFFFFF9E)
                    //),
                    modifier = Modifier
                         .padding(bottom = 10.dp, start = 5.dp, end = 5.dp)
                         .fillMaxWidth()
               ){
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)){
                         Column(
                              verticalArrangement = Arrangement.spacedBy(20.dp),
                              modifier = Modifier.padding(start = 25.dp)
                         ) {
                              Text(text = "Total distance: ${state.totalDistance}m", modifier = Modifier.padding(top = 15.dp))
                              Text(text = "Longest distance: ${state.longestDistance}m")
                              Text(text = "Last Run: ${state.lastRun?.substring(0, 10)}", modifier = Modifier.padding(bottom = 15.dp))
                         }
                         Column(
                              verticalArrangement = Arrangement.spacedBy(20.dp),
                         ) {
                              Text(text = "Achievements: ${state.achievementTotal}", modifier = Modifier.padding(top = 15.dp))
                              Text(text = "Highest streak: ${state.highestStreak}")
                              Text(text = "Number of runs: ${state.numberOfRuns}", modifier = Modifier.padding(bottom = 15.dp))
                         }
                    }
               }

          }
     }

}


@Composable
fun DisplayAchievements(viewModel: UserDataViewModel) {
     val state by viewModel.uiState.collectAsState()
     var showDialog by remember { mutableStateOf(false) }

     Text(
          text = "Achievements",
          fontSize = 20.sp,
          modifier = Modifier
               .width(400.dp)
     )
     Card(
          colors = CardDefaults.cardColors(
               //containerColor = Color(0xFFFFFF9E)
          ),
          modifier = Modifier
               .padding(4.dp)
               .fillMaxWidth()
               .height(100.dp)
               .pointerInput(Unit) {
                    detectTapGestures(
                         onTap = {
                              showDialog = true
                         }
                    )
               },
     ){

          if(state.achievementsList.first.isNotEmpty()){
                    Row (
                         horizontalArrangement = Arrangement.Center,
                         verticalAlignment = Alignment.CenterVertically,
                         modifier = Modifier
                              .fillMaxSize()
                    ){

                         if(state.achievementsList.first.size < 4){
                              for(achievement in state.achievementsList.first){
                                   AchievementIcon(achievement.name,90.dp)
                              }
                         } else {
                              for(i in 0..2){
                                   AchievementIcon(state.achievementsList.first[i].name,90.dp)
                              }

                         }

                    }

          } else{

               Column(
                    modifier = Modifier
                         .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center

               ) {
                    Text(text="Nothing here yet, but every champion starts somewhere!",
                         style = TextStyle(fontSize = 13.sp),)
               }

          }


     }

     if (showDialog) {
          AchievementsDialog(
               onDismiss = { showDialog = false },
               state = state
          )
     }

}
@Composable
fun XPBar(currentXP: Double, maxXP: Int) {
     val progress = (currentXP.toFloat() / maxXP.toFloat()).coerceIn(0f, 1f)

     Box(
          modifier = Modifier
               .height(24.dp) // Height of the XP bar
               .fillMaxWidth(),
          contentAlignment = Alignment.CenterStart
     ) {
          // Background of the XP bar
          Box(
               modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(color = md_theme_light_secondary, shape = RoundedCornerShape(12.dp)) // Rounded corners
          )

          // Filled part of the XP bar
          val colors = listOf(Color(0xFF71F7EB), Color(0xFF003062))
          Box(
               modifier = Modifier
                    .fillMaxWidth(fraction = progress)
                    .height(24.dp)
                    .background(
                         brush = Brush.horizontalGradient(colors = colors),
                         shape = RoundedCornerShape(12.dp)
                    ) // Rounded corners
          )

          Text(
               text = "${floor(currentXP).toInt()}/$maxXP",
               textAlign = TextAlign.Center,
               //color = Color(0xFF120C6E),
               modifier = Modifier
                       .align(Alignment.Center)

          )


     }
}

@Composable
fun AchievementsDialog(
     onDismiss: () -> Unit,
     state: UserDataViewState,
) {


     Dialog(
          onDismissRequest = { onDismiss()},
          properties = DialogProperties(
               usePlatformDefaultWidth = false
          )

     ){
          LazyVerticalGrid(
               columns = GridCells.Fixed(2),
               modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.95f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black.copy(alpha = 0.7f))

          ){
               val colors = listOf(Color(0xFF71F7EB).copy(alpha = 0.4f), Color(0xFF003062).copy(alpha = 0.4f))
               item {
                    Column(
                         modifier = Modifier
                              .padding(8.dp)
                              .height(22.dp)
                              .fillMaxWidth(0.9f)
                              .background(Color.Transparent),
                    ){
                         Text( text = "Achieved",
                              //color = Color.White,
                         )
                    }
               }
               item{}

               //Displaying achieved achievements
               items(state.achievementsList.first){achievement ->
                    Card(
                         modifier = Modifier
                              .padding(8.dp)
                              .height(250.dp)
                              .fillMaxWidth(0.9f),
                         elevation = CardDefaults.cardElevation(defaultElevation = 40.dp)
                    ){
                         Column(
                              modifier = Modifier
                                   .fillMaxSize()
                                   .background(Brush.horizontalGradient(colors)),
                              horizontalAlignment = Alignment.CenterHorizontally,
                              verticalArrangement = Arrangement.Center
                         ){
                              Text(text = achievement.name)

                              AchievementIcon(achievement.name)

                              Text(
                                   text = achievement.dateAchieved,
                                   //color = Color.Black.copy(alpha = 0.3f),
                              )

                              Text(text = achievement.description, Modifier.padding(4.dp), fontSize = 13.sp)
                         }
                    }
               }


               if(state.achievementsList.first.size % 2 != 0){
                    item {}
               }

               item {
                    Column(
                         modifier = Modifier
                              .padding(8.dp)
                              .height(22.dp)
                              .fillMaxWidth(0.9f)
                              .background(Color.Transparent),
                    ){
                         Text( text = "Not achieved",
                              //color = Color.White,
                         )
                    }
               }
               item{}

               //Displaying not achieved achievements
               items(state.achievementsList.second){achievement ->

                    Card(
                         modifier = Modifier
                              .padding(8.dp)
                              .height(250.dp)
                              .fillMaxWidth(0.9f),
                         elevation = CardDefaults.cardElevation(defaultElevation = 40.dp)
                    ){
                         Column(
                              modifier = Modifier
                                   .fillMaxSize()
                                   .background(Brush.horizontalGradient(colors)),
                              horizontalAlignment = Alignment.CenterHorizontally,
                              verticalArrangement = Arrangement.Center
                         ){
                              Text(text = achievement.name)
                              AchievementIcon(achievement.name)
                              Text(text = achievement.description, Modifier.padding(4.dp), fontSize = 13.sp)
                         }
                    }
               }
          }
     }
}

@Composable
fun NewAchievementAlertDialog(
     newAchieved: MutableSet<Achievement>
) {
     var currentAchievement by remember { mutableStateOf(newAchieved.firstOrNull()) }

     if(currentAchievement != null){
          AlertDialog(
               onDismissRequest = {},

               title = { Text(text = "Achievement Unlocked!") },
               text = {
                    Column {
                         Text(text = currentAchievement!!.name, style = MaterialTheme.typography.headlineSmall)
                         Spacer(modifier = Modifier.height(8.dp))
                         Text(text = currentAchievement!!.description)
                    }
               },
               confirmButton = {
                    Button(onClick = {
                         newAchieved.remove(currentAchievement)
                         currentAchievement = newAchieved.firstOrNull()
                    }) {
                         Text("Awesome!")
                    }
               },
               // Optionally add an image or an icon here
               icon = {
                    AchievementIcon(currentAchievement!!.name)
               },
               // Customize the dialog shape, background, etc.
               shape = MaterialTheme.shapes.medium,
               containerColor = MaterialTheme.colorScheme.surfaceVariant,
               // Set the dialog properties like size, etc.
               properties = DialogProperties(usePlatformDefaultWidth = false)
          )

     }

}


@Composable
fun LevelUpAlertDialog(
     lvlCount: Int,
     viewModel: UserDataViewModel
) {
     var showDialog by remember { mutableStateOf(true) }

     if(showDialog){
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
                         Image(
                              painter = painterResource(id = R.drawable.level_up),
                              contentDescription = "Level Up",
                              modifier = Modifier
                                   .size(100.dp)
                                   .padding(16.dp),
                         )
                         Text(
                              text = "Congratulations!",
                              style = MaterialTheme.typography.headlineMedium,
                              color = MaterialTheme.colorScheme.onSurface
                         )
                         Text(
                              text = "You've reached Level $lvlCount!",
                              style = MaterialTheme.typography.bodyLarge,
                              color = MaterialTheme.colorScheme.onSurfaceVariant
                         )
                         Spacer(modifier = Modifier.height(20.dp))
                         Button(
                              onClick = {
                                   showDialog = false
                                   viewModel.updateNewLevel()
                              },
                         ) {
                              Text("Continue")
                         }
                    }
               }
          }

/*          AlertDialog(
               onDismissRequest = {},

               title = { Text(text = "Level Up!") },
               text = {
                    Column {
                         Text(text = currentAchievement!!.name, style = MaterialTheme.typography.headlineSmall)
                         Spacer(modifier = Modifier.height(8.dp))
                         Text(text = currentAchievement!!.description)
                    }
               },
               confirmButton = {
                    Button(onClick = {showdialog = false}) {
                         Text("Awesome!")
                    }
               },
               // Optionally add an image or an icon here
               icon = {
*//*                    Image(
                         painter = rememberAsyncImagePainter(model = R.drawable.social_butterfly_cropped_small),
                         contentDescription = "PNG Icon",
                         modifier = Modifier.size(80.dp),
                         contentScale = ContentScale.Fit
                    )*//*
               },
               // Customize the dialog shape, background, etc.
               shape = MaterialTheme.shapes.medium,
               containerColor = MaterialTheme.colorScheme.surfaceVariant,
               // Set the dialog properties like size, etc.
               properties = DialogProperties(usePlatformDefaultWidth = false)
          )*/

     }

}
@Composable
fun ProfilePicture(viewModel: UserDataViewModel) {
     val context = LocalContext.current
     val coroutineScope = rememberCoroutineScope()


     val profilePicture = produceState<Bitmap?>(initialValue = null, viewModel.uiState.collectAsState().value.profilePicture) {
          value = getPicture(viewModel.uiState.value.profilePicture)
     }


     val pickImageLauncher = rememberLauncherForActivityResult(
          contract = ActivityResultContracts.GetContent(),
          onResult = { uri: Uri? ->
               coroutineScope.launch {
                    uri?.let {
                         val bitmap = uriToBitmap(context, it) // Assume uriToBitmap is a suspend function
                         bitmap?.let { bmp ->
                              val base64String = bitmapToBase64(bmp) // Assume bitmapToBase64 is a suspend function
                              viewModel.updateProfilePicture(base64String)
                              // Update profilePicture state if necessary
                         }
                    }
               }
          }
     )

     Box(
          contentAlignment = Alignment.TopCenter,
          modifier = Modifier
               .padding(top = 5.dp, bottom = 15.dp)
     ) {
          val imageBitmap = profilePicture.value?.asImageBitmap()
          if (imageBitmap != null) {
               Image(
                    bitmap = imageBitmap,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                         .size(200.dp)
                         .clip(CircleShape)
                         .border(5.dp, md_theme_light_secondary, CircleShape)
                         .clickable { pickImageLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop
               )
          } else {
               // Instead of showing a default image, display an empty Box with a border
               Box(
                    modifier = Modifier
                         .size(200.dp)
                         .clip(CircleShape)
                         //.border(2.dp, Color(0xFFFFFF9E), CircleShape)
                         .clickable { pickImageLauncher.launch("image/*") }
               )
          }
     }
}
