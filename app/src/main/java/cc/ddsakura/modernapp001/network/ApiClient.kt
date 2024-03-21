package cc.ddsakura.modernapp001.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

object APIClient {
    val apiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://httpstat.us")
            .client(okHttpClient)
            .build()
            .create(APIInterface::class.java)
    }
}

interface APIInterface {
    // testing what happened when timeout
    @GET("/200?sleep=5000")
    suspend fun get200(): Response<ResponseBody>
}