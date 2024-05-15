package com.example.ak24project

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ak24project.network.Achievement
import com.example.ak24project.network.Achievements
import com.example.ak24project.network.Api
import com.example.ak24project.network.Friend
import com.example.ak24project.network.LevelDetail
import com.example.ak24project.network.SessionData
import com.example.ak24project.ui.screens.login.alreadyLoggedIn
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.floor

data class UserDataViewState(
    val username: String? = "",
    val sessionKey: String = "",
    val sessionDataLists: List<SessionData>? = mutableListOf<SessionData>(),
    val profilePicture: String? = "",
    val friendDataLists: List<Friend>? = mutableListOf<Friend>(),
    val totalDistance: Number? = 0,
    val longestDistance: Number? = 0,
    val numberOfRuns: Number? = 0,
    val highestStreak: Number = 0,
    val lastRun: String? = "No sessions yet.",
    val levelDetail: LevelDetail? = null,
    val achievementsList: Pair<List<Achievement>, List<Achievement>> = Pair(mutableListOf<Achievement>(),mutableListOf<Achievement>()),
    val achievementTotal: String = "Undefined",
    val streaks: MutableMap<String, String> = mutableMapOf(),
    val newlyAchieved: MutableSet<Achievement> = mutableSetOf<Achievement>(),
    val newLevel: Pair<Boolean, Int> = Pair(false, 0),
)

class UserDataViewModel () : ViewModel() {

    private val _uiState = MutableStateFlow(UserDataViewState())
    val uiState: StateFlow<UserDataViewState> = _uiState


    fun setSessionKey(sessionKey: String){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY1 $sessionKey")
            _uiState.value = _uiState.value.copy(
                username = "",
                sessionKey = sessionKey
            )
            Log.d("UserDataViewModel", "DA KEY2 ${_uiState.value.sessionKey}")

            Log.d("UserDataViewModel", "DA KEY in GetHistory ${_uiState.value.sessionKey}")
            //val response = Api().fetchDataFromAPI(_uiState.value.sessionKey)

            //Log.d("UserDataViewModel", "GetHistory result: $response")
        }

    }

    fun getHistory(){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in GetHistory ${_uiState.value.sessionKey}")
            val response = Api().fetchDataFromAPI(_uiState.value.sessionKey)

            Log.d("UserDataViewModel", "GetHistory response: ${response?.data}")
            val tempList = response?.data?.reversed()

            _uiState.value = _uiState.value.copy(sessionDataLists = tempList)
        }
    }

    fun getFriends(){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in GetAllFriends ${_uiState.value.sessionKey}")
            val response = Api().getAllFriends(_uiState.value.sessionKey)

            Log.d("UserDataViewModel", "GetFriends response: ${response?.friendList}")
            _uiState.value = _uiState.value.copy(friendDataLists = response?.friendList)
        }
    }

    fun removeStreak(friendname: String){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in GetAllFriends ${_uiState.value.sessionKey}")
            val response = Api().removeStreak(_uiState.value.sessionKey,friendname)
            Log.d("UserDataViewModel", "GetFriends response: $response")
            getFriends()
        }
    }

    fun createStreak(friendname: String, targetRuns: Int){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in GetAllFriends ${_uiState.value.sessionKey}")
            val response = Api().createStreak(_uiState.value.sessionKey, friendname, targetRuns)
            getFriends()
            Log.d("UserDataViewModel", "createStreak response: $response?")
        }
    }

    private fun getStreaks(friends: List<Friend>?) {

        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in GetStreak ${_uiState.value.sessionKey}")
            val streaks: MutableMap<String, String> = mutableMapOf()

            friends?.forEach {
                val response = Api().getStreak(_uiState.value.sessionKey, it.username)
                if (response != null) {
                    streaks[it.username] = response.currentStreak
                }
            }
            _uiState.value = _uiState.value.copy(streaks = streaks)
            Log.d("InTheJuice", _uiState.value.streaks.toString())
        }
    }

    fun removeFriend(friendname: String){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in removeFriend ${_uiState.value.sessionKey}")
            val response = Api().removeFriend(_uiState.value.sessionKey, friendname)

            Log.d("UserDataViewModel", "removeFriend response: ")
            getFriends()
            getUserData()

            //_uiState.value = _uiState.value.copy(friendDataLists = response?.friends)
        }
    }

    fun removeSession(sessionID: String){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in removeFriend ${_uiState.value.sessionKey}")
            val response = Api().removeSession(_uiState.value.sessionKey, sessionID)

            Log.d("UserDataViewModel", "removeFriend response: ")
            getHistory()
            getUserData()
            //_uiState.value = _uiState.value.copy(friendDataLists = response?.friends)
        }
    }

    fun addFriend(friendname: String){
        viewModelScope.launch {
            Log.d("UserDataViewModel", "DA KEY in addFriend ${_uiState.value.sessionKey}")
            val response = Api().addFriend(_uiState.value.sessionKey, friendname)

            Log.d("UserDataViewModel", "addFriend response: ")
            getFriends()
            getUserData()
            //_uiState.value = _uiState.value.copy(friendDataLists = response?.friends)
        }
    }

    fun getUserData(atLaunch: Boolean = false){

        viewModelScope.launch {
            val response = Api().fetchUserData(_uiState.value.sessionKey)

            Log.d("UserDataViewModel", "getUserData response nor: ${response?.data?.numberOfRuns}")
            Log.d("UserDataViewModel", "getUserData response ld: ${response?.data?.longestDistance}")
            Log.d("UserDataViewModel", "getUserData response td: ${response?.data?.totalDistance}")
            Log.d("UserDataViewModel", "getUserData response levelDetail: ${response?.data?.levelDetail}")
            Log.d("UserDataViewModel", "getUserData response achievements: ${response?.data?.achievements}")
            Log.d("UserDataViewModel", "getUserData response achievements: ${response?.data?.lastRun}")

            val currentAchievements = _uiState.value.achievementsList.first
            val currentLvl = _uiState.value.levelDetail?.lvl


            if (response != null) {
                _uiState.value = response.data?.let { getAchievedAchievements(it.achievements) }?.let {
                    _uiState.value.copy(
                        username = response.data.username,
                        profilePicture = response.data.profilePicture,
                        totalDistance = response.data.totalDistance,
                        longestDistance = response.data.longestDistance,
                        numberOfRuns = response.data.numberOfRuns,
                        lastRun = response.data.lastRun,
                        levelDetail = response.data.levelDetail,
                        highestStreak = response.data.highestStreak,
                        achievementsList = it
                    )
                }!!
            }

            if(!atLaunch){
                val updatedAchievements = _uiState.value.achievementsList.first
                val updatedLvl = _uiState.value.levelDetail?.lvl
                if(currentAchievements.size < updatedAchievements.size){
                    val newAchievements = updatedAchievements.subtract(currentAchievements.toSet()).toMutableSet()
                    Log.d("UserDataViewModel","This is neeeeew $newAchievements")
                    _uiState.value = _uiState.value.copy(
                        newlyAchieved = newAchievements
                    )
                }

                if(currentLvl!! < updatedLvl!!){
                    _uiState.value = _uiState.value.copy(
                        newLevel = Pair(true,updatedLvl - currentLvl)
                    )
                }
            }
        }
    }

    private fun getAchievedAchievements(achievements: Achievements):Pair<MutableList<Achievement>, MutableList<Achievement>> {
        val achievementsList: MutableList<Achievement> = mutableListOf()
        val notAchievedList: MutableList<Achievement> = mutableListOf()

        if(achievements.firstSteps.achieved) achievementsList.add(achievements.firstSteps) else notAchievedList.add(achievements.firstSteps)
        if(achievements.consistencyChampion.achieved) achievementsList.add(achievements.consistencyChampion) else notAchievedList.add(achievements.consistencyChampion)
        if(achievements.nightOwl.achieved) achievementsList.add(achievements.nightOwl) else notAchievedList.add(achievements.nightOwl)
        if(achievements.earlyBird.achieved) achievementsList.add(achievements.earlyBird) else notAchievedList.add(achievements.earlyBird)
        if(achievements.centuryClub.achieved) achievementsList.add(achievements.centuryClub) else notAchievedList.add(achievements.centuryClub)
        if(achievements.collector.achieved) achievementsList.add(achievements.collector) else notAchievedList.add(achievements.collector)
        if(achievements.fiveKfinisher.achieved) achievementsList.add(achievements.fiveKfinisher) else notAchievedList.add(achievements.fiveKfinisher)
        if(achievements.marathoner.achieved) achievementsList.add(achievements.marathoner) else notAchievedList.add(achievements.marathoner)
        //if(achievements.globetrotter.achieved) achievementsList.add(achievements.globetrotter) else notAchievedList.add(achievements.globetrotter)
        if(achievements.hourglass.achieved) achievementsList.add(achievements.hourglass) else notAchievedList.add(achievements.hourglass)
        if(achievements.theLongHaul.achieved) achievementsList.add(achievements.theLongHaul) else notAchievedList.add(achievements.theLongHaul)
        if(achievements.socialButterfly.achieved)achievementsList.add(achievements.socialButterfly) else notAchievedList.add(achievements.socialButterfly)
        if(achievements.motivator.achieved) achievementsList.add(achievements.motivator) else notAchievedList.add(achievements.motivator)
        if(achievements.newYearsResolution.achieved) achievementsList.add(achievements.newYearsResolution) else notAchievedList.add(achievements.newYearsResolution)
        if(achievements.christmasGrind.achieved) achievementsList.add(achievements.christmasGrind) else notAchievedList.add(achievements.christmasGrind)
        if(achievements.levelUp.achieved) achievementsList.add(achievements.levelUp) else notAchievedList.add(achievements.levelUp)
        if(achievements.xpCollector.achieved) achievementsList.add(achievements.xpCollector) else notAchievedList.add(achievements.xpCollector)


        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        achievementsList.sortByDescending {
            Log.d("UserDataViewModel","DateTime: ${it.dateAchieved}")
            LocalDateTime.parse(it.dateAchieved, dateFormatter)
        }

        _uiState.value = _uiState.value.copy(achievementTotal = "${achievementsList.size}/${achievementsList.size + notAchievedList.size}")

        return Pair(achievementsList, notAchievedList)
    }


    fun processData(heartRate:Double, distance:Double, duration:Long, date: String?){
        viewModelScope.launch {

            val heartRateConverted = floor(heartRate).toInt()
            val distanceConverted = distance.toInt()


            val totalSeconds = duration / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            val durationConverted = String.format("%02d:%02d:%02d", hours, minutes, seconds)


            Log.d("UserDataViewModel", "DA KEY3 ${_uiState.value.sessionKey}")
            Log.d("UserDataViewModel", "DA KEY4 ${_uiState.value.sessionKey}")


            Log.d("UserDataViewModel","SUP")

            val key = _uiState.value.sessionKey
            Log.d("UserDataViewModel", "DA KEY5 $key")
            Api().upLoadData(heartRateConverted,distanceConverted,durationConverted,date, key)
            getUserData()
        }
    }

    suspend fun updateProfilePicture(profilePicture: String){
        viewModelScope.launch {

            Api().updateProfilePicture(_uiState.value.sessionKey, profilePicture)
            _uiState.value = _uiState.value.copy(profilePicture = profilePicture)
        }
    }


    fun updateNewLevel(){
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                newLevel = Pair(false, 0)
            )
        }
    }

    fun streakVisual(streak: Int): String{

        return if(streak == -1) {
            "No streaks created yet."
        } else if (streak == 0){
            "Streak: \uD83E\uDDCA $streak"
        } else {
            "Streak: \uD83D\uDD25 $streak"
        }
    }

    fun isSessionExpired(context: Context){
        viewModelScope.launch {
            val response = Api().statusCheck(_uiState.value.sessionKey)  //
            if (response?.status.equals("error")) {
                val intent = Intent(context, SignInActivity::class.java).apply {
                    putExtra("message", response?.errorMessage)
                }
                context.startActivity(intent)
            }
        }
    }
    fun logout(){
        viewModelScope.launch {
            delay(1000)
            _uiState.value = UserDataViewState()
        }
    }

}

