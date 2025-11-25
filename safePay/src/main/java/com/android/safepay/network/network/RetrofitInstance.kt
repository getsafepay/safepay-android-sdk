package com.android.safepay.network.network

import com.android.safepay.network.APIService
import com.android.safepay.network.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance
{

    private var baseUrl:String=""

    fun setBaseURL(sandbox:Boolean)
    {
        baseUrl = if(sandbox)
            Constants.SANDBOX_BASE_URL
        else
            Constants.DEV_BASE_URL
    }
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson converter for JSON parsing
            .build()
    }

    fun createRetrofit(baseUrl: String, authToken: String? = null): Retrofit {
        val okHttpClientBuilder = client.newBuilder()
        authToken?.let {
            okHttpClientBuilder.addInterceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $authToken")
                    .build()
                chain.proceed(newRequest)
            }
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}