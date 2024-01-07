package com.example.iot

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.3:8080/api/"
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }

    fun getLatestTreeStatus(context: Context, callback: (TreeStatusResponse?) -> Unit) {
        apiService.getLatestTreeStatus().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    // Xử lý dữ liệu JSON từ responseBody và chuyển đổi thành TreeStatusResponse
                    val treeStatusResponse = handleTreeStatusResponse(responseBody, context)
                    callback(treeStatusResponse)
                } else {
                    Log.e("TAG", "Error code: ${response.code()}")
                    callback(null) // Gọi callback với giá trị null khi có lỗi
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "Loi ${t.message}")
                callback(null) // Gọi callback với giá trị null khi có lỗi
            }
        })
    }

    // Hàm để xử lý dữ liệu JSON và chuyển đổi thành TreeStatusResponse
    private fun handleTreeStatusResponse(
        responseBody: String?,
        context: Context
    ): TreeStatusResponse? {
        try {
            val jsonObject = JSONObject(responseBody)
            Log.d("TAG", "JsonObject: ${jsonObject}")
            val type = jsonObject.getString("type")

            if (type == "success") {
                val data = jsonObject.getJSONObject("data")
                val humidity = data.getInt("humidity")
                val temperature = data.getDouble("temperature").toFloat()
                val createdAt = data.getString("createdAt")

                return TreeStatusResponse(humidity, temperature, createdAt)
            } else {
                val message = jsonObject.getString("message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            Log.d("TAG", "Fetch failed. Error: ${e.message}")
        }

        return null
    }

    fun getPumpAuto(context: Context, id: Int, callback: (Boolean, Boolean) -> Unit) {
        apiService.getPumpAuto(id).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    // Xử lý dữ liệu JSON từ responseBody và cập nhật trạng thái của Switch
                    handlePumpAutoResponse(responseBody, callback, context)
                } else {
                    // Xử lý trường hợp có lỗi khi gọi API
                    Log.e("TAG", "Error code: ${response.code()}")
                    callback(false, false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Xử lý trường hợp lỗi khi không thể kết nối tới API
                Log.d("TAG", "Loi ${t.message}")
                callback(false, false)
            }
        })
    }

    // Hàm để xử lý dữ liệu JSON và cập nhật trạng thái của Switch
    private fun handlePumpAutoResponse(
        responseBody: String?,
        callback: (Boolean, Boolean) -> Unit,
        context: Context
    ) {
        try {
            val jsonObject = JSONObject(responseBody)
            val type = jsonObject.getString("type")

            if (type == "success") {
                val data = jsonObject.getJSONObject("data")
                val pump = data.getBoolean("pump")
                val auto = data.getBoolean("auto")

                // Gọi callback để cập nhật trạng thái của Switch
                callback(pump, auto)
            } else {
                val message = jsonObject.getString("message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            Log.d("TAG", "Fetch failed. Error: ${e.message}")
        }
    }

    fun updatePump(context: Context, isChecked: Boolean, callback: (Boolean?) -> Unit) {
        apiService.updatePump(isChecked).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    handleUpdateResponse(responseBody, callback, context)
                } else {
                    // Xử lý trường hợp có lỗi khi gọi API
                    Log.e("TAG", "Error code: ${response.code()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Xử lý trường hợp lỗi khi không thể kết nối tới API
                Log.d("TAG", "Loi ${t.message}")
                callback(false)
            }
        })
    }

    private fun handleUpdateResponse(
        responseBody: String?,
        callback: (Boolean) -> Unit,
        context: Context
    ) {
        try {
            val jsonObject = JSONObject(responseBody)
            val type = jsonObject.getString("type")

            if (type == "success") {
                callback(true)
            } else {
                val message = jsonObject.getString("message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                callback(false)
            }
        } catch (e: JSONException) {
            Log.d("TAG", "Fetch failed. Error: ${e.message}")
        }
    }

    fun updateAuto(context: Context, isChecked: Boolean, callback: (Boolean?) -> Unit) {
        apiService.updateAuto(isChecked).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    handleUpdateResponse(responseBody, callback, context)
                } else {
                    // Xử lý trường hợp có lỗi khi gọi API
                    Log.e("TAG", "Error code: ${response.code()}")
                    callback(false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Xử lý trường hợp lỗi khi không thể kết nối tới API
                Log.d("TAG", "Loi ${t.message}")
                callback(false)
            }
        })
    }
    fun getTop10TreeStatus(context: Context, callback: (List<TreeStatusResponse>?) -> Unit) {
        apiService.getTop10TreeStatus().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    // Xử lý dữ liệu JSON từ responseBody và chuyển đổi thành danh sách TreeStatusResponse
                    val treeStatusList = handleTop10TreeStatusResponse(responseBody, context)
                    callback(treeStatusList)
                } else {
                    Log.e("TAG", "Error code: ${response.code()}")
                    callback(null) // Gọi callback với giá trị null khi có lỗi
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("TAG", "Loi ${t.message}")
                callback(null) // Gọi callback với giá trị null khi có lỗi
            }
        })
    }
    private fun handleTop10TreeStatusResponse(
        responseBody: String?,
        context: Context
    ): List<TreeStatusResponse>? {
        try {
            val jsonObject = JSONObject(responseBody)
            val type = jsonObject.getString("type")

            if (type == "success") {
                val dataArray = jsonObject.getJSONArray("data")
                val treeStatusList = mutableListOf<TreeStatusResponse>()

                for (i in 0 until dataArray.length()) {
                    val dataObject = dataArray.getJSONObject(i)
                    val humidity = dataObject.getInt("humidity")
                    val temperature = dataObject.getDouble("temperature").toFloat()
                    val createdAt = dataObject.getString("createdAt")

                    val treeStatus = TreeStatusResponse(humidity, temperature, createdAt)
                    treeStatusList.add(treeStatus)
                }

                return treeStatusList
            } else {
                val message = jsonObject.getString("message")
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: JSONException) {
            Log.d("TAG", "Fetch failed. Error: ${e.message}")
        }

        return null
    }
}