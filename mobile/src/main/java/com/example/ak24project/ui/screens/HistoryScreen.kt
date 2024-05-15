package com.example.ak24project.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import com.example.ak24project.R
import com.example.ak24project.UserDataViewModel
import com.example.ak24project.network.SessionData
import com.example.ak24project.ui.theme.md_theme_light_secondary


@Composable
fun HistoryScreen(viewModel: UserDataViewModel) {
    val state by viewModel.uiState.collectAsState()

    viewModel.getHistory()
    state.sessionDataLists?.let { HistoryList(it, viewModel) }

}
@Composable
fun HistoryList(
    historyData: List<SessionData>,
    viewModel: UserDataViewModel
){

    var showDialog by remember { mutableStateOf(false) }
    var selectedSessionID by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "History",
            fontSize = 50.sp,
            modifier = Modifier
        )
    }
    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        items(historyData) { session ->
            Card(
                colors = CardDefaults.cardColors(
                    //containerColor = Color(0xFFFFFF9E)
                ),
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .pointerInput(session) {
                        detectTapGestures(
                            onLongPress = {
                                selectedSessionID =
                                    session.id // Capture the friend's username
                                showDialog = true
                            }
                        )
                    },
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row(){
                        Row(){
                            Image(
                                painter = painterResource(R.drawable.icons8_date_50),
                                contentDescription = "Picture of friend.",
                                modifier = Modifier
                                    .size(25.dp)
                            )
                            Text(
                                text = "Date: ${session.date}",
                                //color = Color.Black
                            )
                        }
                        Row(){
                            Image(
                                painter = painterResource(R.drawable.icons8_timer_32),
                                contentDescription = "Picture of friend.",
                                modifier = Modifier
                                    .size(25.dp)
                            )
                            Text(
                                text = "Duration: ${session.duration}",
                                //color = Color.Black
                            )
                        }
                    }
                    Row(){
                        Row(){
                            Image(
                                painter = painterResource(R.drawable.icons8_heart_rate_48),
                                contentDescription = "Picture of friend.",
                                modifier = Modifier
                                    .size(25.dp)
                            )
                            Text(
                                text = "Heart rate: ${session.heartRate}",
                                //color = Color.Black
                            )
                        }
                        Row(){
                            Image(
                                painter = painterResource(R.drawable.icons8_distance_64),
                                contentDescription = "Picture of friend.",
                                modifier = Modifier
                                    .size(25.dp)
                            )
                            Text(
                                text = "Distance: ${session.distance} m",
                                //color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        RemoveDialog(
            onDismiss = { showDialog = false },
            viewModel = viewModel,
            sessionID = selectedSessionID // Pass the selected username to the dialog
        )
    }
}


@Composable
fun RemoveDialog(
    onDismiss: () -> Unit,
    viewModel: UserDataViewModel,
    sessionID: String,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Remove this session?") }, // Optionally show the friend's username in the dialog
        confirmButton = {
            Button(
                onClick = {
                    viewModel.removeSession(sessionID) // Call removeFriend with the passed username
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

