package com.example.iot

import androidx.fragment.app.activityViewModels
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("treeStatus/latest")
    fun getLatestTreeStatus(): Call<ResponseBody>

    @GET("treeStatus/top10")
    fun getTop10TreeStatus(): Call<ResponseBody>

    @GET("pump/{id}")
    fun getPumpAuto(@Path("id") id: Int): Call<ResponseBody>

    @PUT("pump/updatePump")
    fun updatePump(@Query("pump") pump: Boolean): Call<ResponseBody>

    @PUT("pump/updateAuto")
    fun updateAuto(@Query("auto") auto: Boolean): Call<ResponseBody>
}