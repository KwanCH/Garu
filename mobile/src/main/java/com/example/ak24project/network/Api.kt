package com.example.ak24project.network

import android.content.ClipDescription
import android.content.Context
import android.net.wifi.WifiManager
import android.service.autofill.UserData
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Date


//const val base = "http://192.168.1.4:8080/api"
//const val base = "http://127.0.0.1:8080/api"
//const val base = "http://10.0.2.2:8080/api"
//const val base = "http://192.168.50.54:8080/api"
//const val base = "http://192.168.10.134:8080/api"
//const val base = "http://193.157.236.73:8080/api"
//const val base = "http://192.168.50.53:8080/api"
//const val base = "http://193.157.205.45:8080/api"
//const val base = "http://193.157.180.21:8080/api"
//const val base = "http://192.168.39.110:8080/api"
//const val base = "http://193.157.238.11:8080/api"
//const val base = "http://192.168.50.91:8080/api"
//const val base = "http://193.157.236.175:8080/api"
//const val base = "http://172.20.10.2:8080/api"
//const val base = "http://172.26.0.244:8080/api"
const val base = "http://garu.uiocloud.no:8080/api"


data class SessionData (
    val heartRate:Int,
    val distance:Int,
    val duration:String,
    val date: String,
    val id : String
)

data class User(
    val username: String,
    val profilePicture: String,
    val longestDistance: Number,
    val totalDistance: Number,
    val numberOfRuns: Number,
    val lastRun: String,
    val levelDetail: LevelDetail,
    val achievements: Achievements,
    val highestStreak: Number
)

data class Achievements(
    val totalAchieved: Number,
    val firstSteps: Achievement,
    val consistencyChampion: Achievement,
    val nightOwl: Achievement,
    val earlyBird: Achievement,
    val centuryClub: Achievement,
    val collector: Achievement,
    val fiveKfinisher: Achievement,
    val marathoner: Achievement,
    val globetrotter: Achievement,
    val hourglass: Achievement,
    val theLongHaul: Achievement,
    val socialButterfly: Achievement,
    val motivator: Achievement,
    val newYearsResolution: Achievement,
    val christmasGrind: Achievement,
    val levelUp: Achievement,
    val xpCollector: Achievement
)

data class Achievement(
    val name: String,
    val description: String,
    val category: String,
    val achieved: Boolean,
    val dateAchieved: String,
)
data class Friend(
    val profilePicture: String,
    val username: String,
    val streak: Int,
)

data class LevelDetail(
    val lvl: Int,
    val xp: Double,
    val nextLvl: Int
)


//-------------------------
data class SignupRequest(val email: String, val username: String, val password: String, val passwordConfirm: String, val profilePicture: String)
data class LoginRequest(val username: String, val password: String)

data class DataUpdateRequest(val heartRate: Int, val distance: Int, val duration: String, val date: String?, val jwt: String)
data class FetchUserDataRequest(val jwt: String)

data class RemoveFriendRequest(val jwt: String, val friendname: String)
data class RemoveSessionRequest(val jwt: String, val sessionID: String)

data class AddFriendRequest(val jwt: String, val friendname: String)

data class GetAllFriendsRequest(val jwt: String)
data class DataFetchRequest(val jwt: String)

data class GetStreakRequest(val jwt: String, val friendname: String)

data class CreateStreakRequest(val jwt: String, val friendname: String, val targetRuns: Int)

data class RemoveStreakRequest(val jwt: String, val friendname: String)

data class UpdatePictureRequest(val jwt: String, val profilePicture: String)
data class StatusRequest(val jwt: String)
//-------------------------
data class ErrorResponse(val error: String)
data class StatusResponse(val status: String, val errorMessage: String? = null)

data class SignupResponse(
    val status: String,
    val token: String?,
    val data: UserData?,
    val errorMessage: String? = null // Add this field to include error messages

)
data class LoginResponse(
    val status: String,
    val token: String?,
    val data: UserData?,
    val errorMessage: String? = null // Add this field to include error messages
)

data class UpdatePictureResponse(val status: String, )

data class GetAllFriendsResponse(val friendList: List<Friend>)

data class RemoveFriendResponse(val status: String)

data class RemoveSessionResponse(val status: String)


data class AddFriendResponse(val status: String)

data class GetStreakResponse(val currentStreak: String)

data class CreateStreakResponse(val status: String)

data class RemoveStreakResponse(val status: String)

data class DataUpdateResponse(val response: String)
data class DataFetchResponse(
    val status: String,
    val data: List<SessionData>?,
    val errorMessage: String? = null // Add this field to include error messages
)

data class FetchUserDataResponse(
    val status: String,
    val data: User?,
    val errorMessage: String? = null // Add this field to include error messages
)

interface ApiInterface {
    @POST("$base/signup/")
    suspend fun signup(@Body data: SignupRequest): Response<SignupResponse>

    @POST
    suspend fun tokenStatus(@Body data: StatusRequest): Response<StatusResponse>
    @POST("$base/login/")
    suspend fun login(@Body data: LoginRequest): Response<LoginResponse>

    @POST("$base/removefriend/")
    suspend fun removeFriend(@Body data: RemoveFriendRequest): Response<RemoveFriendResponse>

    @POST("$base/removeSession/")
    suspend fun removeSession(@Body data: RemoveSessionRequest): Response<RemoveSessionResponse>

    @POST("$base/addfriend/")
    suspend fun addFriend(@Body data: AddFriendRequest): Response<AddFriendResponse>

    @POST("$base/getallfriends/")
    suspend fun getAllFriends(@Body data: GetAllFriendsRequest): Response<GetAllFriendsResponse>

    @POST("$base/updateFitnessData/")
    suspend fun updateFitnessData(@Body data: DataUpdateRequest): Response<DataUpdateResponse>

    @POST("$base/fetchSessionData/")
    suspend fun fetchSessionData(@Body data: DataFetchRequest): Response<DataFetchResponse>

    @POST("$base/fetchUserData/")
    suspend fun fetchUserData(@Body data: FetchUserDataRequest): Response<FetchUserDataResponse>

    @POST("$base/updateProfilePicture/")
    suspend fun updatePicture(@Body data: UpdatePictureRequest): Response<UpdatePictureResponse>

    @POST("$base/getStreak/")
    suspend fun getStreak(@Body data: GetStreakRequest): Response<GetStreakResponse>

    @POST("$base/createStreak/")
    suspend fun createStreak(@Body data: CreateStreakRequest): Response<CreateStreakResponse>

    @POST("$base/removeStreak/")
    suspend fun removeStreak(@Body data: RemoveStreakRequest): Response<RemoveStreakResponse>
}

class Api {

    private val baseUrl = "$base/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl) // Use the base URL here
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    private val apiService: ApiInterface = retrofit.create(ApiInterface::class.java)
    suspend fun createAccount(
        email:String,
        username: String,
        password: String,
        confirmPassword: String,
        profilePicture: String,
    ): SignupResponse? {

        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.signup(SignupRequest(email, username, password, confirmPassword, profilePicture))


                if (response.isSuccessful) {
                    val status: SignupResponse? = response.body()
                    return@withContext status!!

                } else {
                    val errorMessage = getErrorMessage(response)
                    return@withContext SignupResponse(status = "error", token = null, data = null, errorMessage = errorMessage)
                }
            }
        } catch (e: Exception) {
            Log.d("My API" ,"ERROR: $e")
            return null
        }
    }

    suspend fun signin(username: String, password: String): LoginResponse? {

        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.login(LoginRequest(username, password))


                if (response.isSuccessful) {
                    val status: LoginResponse? = response.body()

                    return@withContext status!!

                } else {
                    val errorMessage = getErrorMessage(response)
                    return@withContext LoginResponse(status = "error", token = null, data = null, errorMessage = errorMessage)
                }
            }
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getAllFriends(sessionToken: String): GetAllFriendsResponse? {
        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.getAllFriends(GetAllFriendsRequest(sessionToken))
                Log.d("getAllFriendssssssssss", response.body().toString())

                if (response.isSuccessful) {
                    val status: GetAllFriendsResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            Log.d("getAllFriendssssssssss","ERROR: $e")
            Log.d("getAllFriendssssssssss", "Big fail")

            return null
        }
    }

    suspend fun removeSession(sessionToken: String, sessionID: String): RemoveSessionResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.removeSession(RemoveSessionRequest(sessionToken,sessionID))
                Log.d("My API", response.body().toString())


                if (response.isSuccessful) {
                    val status: RemoveSessionResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun createStreak(sessionToken: String, friendname: String, targetRuns: Int): CreateStreakResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.createStreak(CreateStreakRequest(sessionToken, friendname, targetRuns))
                Log.d("My API", response.body().toString())


                if (response.isSuccessful) {
                    val status: CreateStreakResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun removeStreak(sessionToken: String, friendname: String): RemoveStreakResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.removeStreak(RemoveStreakRequest(sessionToken, friendname))
                Log.d("My API", response.body().toString())


                if (response.isSuccessful) {
                    val status: RemoveStreakResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun removeFriend(sessionToken: String, friendname: String): RemoveFriendResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.removeFriend(RemoveFriendRequest(sessionToken, friendname))
                Log.d("My API", response.body().toString())


                if (response.isSuccessful) {
                    val status: RemoveFriendResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun addFriend(sessionToken: String, friendname: String): AddFriendResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.addFriend(AddFriendRequest(sessionToken, friendname))
                Log.d("My API", response.body().toString())


                if (response.isSuccessful) {
                    val status: AddFriendResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun getStreak(sessionToken: String, friendname: String): GetStreakResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.getStreak(GetStreakRequest(sessionToken, friendname))
                Log.d("My API", response.body().toString())
                
                if (response.isSuccessful) {
                    val status: GetStreakResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun upLoadData(heartRate: Int, distance: Int, duration: String, date: String?, sessionToken: String): DataUpdateResponse? {
        try {
            return withContext(Dispatchers.IO) {
                Log.d("My API", "DAAAA KEYE $sessionToken")
                val response = apiService.updateFitnessData(DataUpdateRequest(heartRate, distance, duration, date, sessionToken))


                if (response.isSuccessful) {
                    val status: DataUpdateResponse? = response.body()

                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            println("ERROR: $e")
            return null
        }
    }

    suspend fun fetchDataFromAPI(sessionToken: String) : DataFetchResponse? {

        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.fetchSessionData(DataFetchRequest(sessionToken))
                Log.d("My API", "FetchData status1: $response")

                if (response.isSuccessful) {
                    val status: DataFetchResponse? = response.body()
                    Log.d("My API", "FetchData status2: $status")
                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            Log.d("My API","ERROR: $e")

            return null
        }
    }

    suspend fun fetchUserData(sessionToken: String): FetchUserDataResponse? {
        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.fetchUserData(FetchUserDataRequest(sessionToken))

                if (response.isSuccessful) {
                    val status: FetchUserDataResponse? = response.body()
                    return@withContext status!!

                } else {
                    val errorMessage = getErrorMessage(response)
                    return@withContext FetchUserDataResponse(status = "error", data = null, errorMessage = errorMessage)
                }
            }
        } catch (e: Exception) {
            Log.d("My API","ERROR: $e")
            return null
        }
    }


    suspend fun updateProfilePicture(sessionToken: String, profilePicture: String): UpdatePictureResponse? {
        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.updatePicture(UpdatePictureRequest(sessionToken, profilePicture))

                if (response.isSuccessful) {
                    val status: UpdatePictureResponse? = response.body()
                    Log.d("My API", "fetchUserData result: $status")
                    return@withContext status!!

                } else {
                    return@withContext response.body()!!
                }
            }
        } catch (e: Exception) {
            Log.d("My API","ERROR: $e")
            return null
        }
    }

    suspend fun statusCheck (sessionToken: String): StatusResponse? {

        try {
            return withContext(Dispatchers.IO) {
                val response = apiService.tokenStatus(StatusRequest(sessionToken))

                if (response.isSuccessful) {
                    val status: StatusResponse? = response.body()
                    Log.d("My API", "fetchUserData result: $status")
                    return@withContext status!!

                } else {
                    val errorMessage = getErrorMessage(response)
                    return@withContext StatusResponse(status = "error", errorMessage)
                }
            }
        } catch (e: Exception) {
            Log.d("My API","ERROR: $e")
            return null
        }
    }

    private fun getErrorMessage(response: Response<*>) : String{
        val errorBody = response.errorBody()?.string()
        return try {
            val gson = Gson()
            val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
            errorResponse.error
        } catch (e: Exception) {
            "Unknown error"
        }
    }


}