package com.example.ak24project.ui.screens

import android.provider.CalendarContract
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.runtime.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.example.ak24project.UserDataViewModel
import com.example.ak24project.network.Friend
import com.example.ak24project.R
import com.example.ak24project.getPicture
import com.example.ak24project.ui.theme.md_theme_light_secondary

@Composable
fun FriendsScreen(viewModel: UserDataViewModel) {

    val state by viewModel.uiState.collectAsState()
    viewModel.getFriends()
    val streaks = state.streaks

    var showDialog by remember { mutableStateOf(false) }

    state.friendDataLists?.let { friendDataLists ->
        Box(modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "Friends",
                    fontSize = 50.sp,
                )
                FriendList(friendData = friendDataLists, viewModel = viewModel)
            }

            FloatingActionButton(
                onClick = {
                    showDialog = true
                    Log.d("yeah", "yeah")
                          },
                //containerColor = Color(0xFFFFFF9E),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }

            if (showDialog) {
                FriendAddDialog(
                    viewModel = viewModel,  onDialogDismiss = {showDialog = false}
                )
            }

        }
    }

}


@Composable
fun FriendAddDialog(
    viewModel: UserDataViewModel,
    onDialogDismiss: () -> Unit
) {
    val (textState, setTextState) = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    AlertDialog(
        onDismissRequest = { onDialogDismiss() },
        title = { Text("Add Friend") },
        text = {
            Column {
                OutlinedTextField(
                    value = textState,
                    onValueChange = setTextState,
                    label = { Text("Enter friend's name") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.addFriend(textState) // Pass the input to viewModel's addFriend method
                    setTextState("") // Reset text field
                    onDialogDismiss() // Close the dialog
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDialogDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FriendList(
    friendData: List<Friend>,
    viewModel: UserDataViewModel,
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedFriendUsername by remember { mutableStateOf("") }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        items(friendData) { friend ->
            Card(
                modifier = Modifier
                    .pointerInput(friend) {
                        detectTapGestures(
                            onLongPress = {
                                selectedFriendUsername = friend.username
                                showDialog = true
                            }
                        )
                    }
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                ){
                    Row(){
                        Image(
                            bitmap = getPicture(friend.profilePicture)!!.asImageBitmap(),
                            contentDescription = "Picture of friend.",
                            modifier = Modifier
                                .size(90.dp)
                                .border(2.dp, md_theme_light_secondary, CircleShape)
                                .clip(CircleShape)
                                .padding(1.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Text(
                                text = friend.username,
                                fontSize = 30.sp,
                                modifier = Modifier
                                    .padding(top = 15.dp)
                            )
                            Text(
                                text = viewModel.streakVisual(friend.streak),
                                fontSize = 13.sp,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        ConfirmRemoveDialog(
            onDismiss = { showDialog = false },
            viewModel = viewModel,
            username = selectedFriendUsername // Pass the selected username to the dialog
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmRemoveDialog(
    onDismiss: () -> Unit,
    viewModel: UserDataViewModel,
    username: String
) {

    var startStreakCheck by remember { mutableStateOf(false) }
    var removeStreakCheck by remember { mutableStateOf(false) }
    var removeFriendCheck by remember { mutableStateOf(false) }

    val options = listOf("1x week", "2x week", "3x week", "4x week", "5x week", "6x week", "7x week")
    var expanded by remember { mutableStateOf(false) }
    var currOpt by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("What do you want to do?") },
        text = {
            Column {
                Row(){
                    Text(text="Start a streak:")
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ){
                        OutlinedTextField(
                            value = currOpt,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .menuAnchor()
                                .clickable {
                                    expanded = true
                                },
                            //.zIndex(20f),
                            label = {
                                Text(
                                    text = "Options"
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) // The dropdown icon
                            }
                        )
                    }
                    DropdownMenu(
                        modifier = Modifier
                            .clickable { expanded = true },
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = 150.dp, y = 0.dp)
                    ){
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    currOpt = option
                                    expanded = false
                                })
                        }
                    }

                    RadioButton(
                        selected = startStreakCheck,
                        onClick = {
                            startStreakCheck = true
                            removeStreakCheck = false
                            removeFriendCheck = false
                        }
                    )
                }
                Row(){
                    Text(text="Remove a streak:")
                    RadioButton(
                        selected = removeStreakCheck,
                        onClick = {
                            removeStreakCheck = true
                            startStreakCheck = false
                            removeFriendCheck = false
                        }
                    )
                }
                Row(){
                    Text(text="Remove friend:")
                    RadioButton(
                        selected = removeFriendCheck,
                        onClick = {
                            removeFriendCheck = true
                            startStreakCheck = false
                            removeStreakCheck = false
                        }
                    )
                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    run {
                        if (startStreakCheck) {
                            viewModel.createStreak(username, currOpt[0].digitToInt())
                        } else if (removeStreakCheck) {
                            viewModel.removeStreak(username)
                        } else if (removeFriendCheck) {
                            viewModel.removeFriend(username)
                        }
                    }
                    onDismiss() // Dismiss the dialog
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("No")
            }
        }
    )
}