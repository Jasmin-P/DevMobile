package com.devmobilejasmin.todo.network

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.content.edit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object Api {

    // constantes qui serviront à faire les requêtes
    private const val BASE_URL = "https://android-tasks-api.herokuapp.com/api/"
    private const val SHARED_PREF_TOKEN_KEY = "auth_token_key"

    lateinit var appContext: Context

    // client HTTP
    private val okHttpClient by lazy {
        //val token = PreferenceManager.getDefaultSharedPreferences(appContext).getString(SHARED_PREF_TOKEN_KEY, "")
        //val token = "aze"
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                //val token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo1ODYsImV4cCI6MTY3MjY5MDgyMH0.d5Ry6Wq3vXOtbHpIyicQtYrUhq-hj5OdabpaOYlkseg"
                val response = PreferenceManager.getDefaultSharedPreferences(appContext).getString(SHARED_PREF_TOKEN_KEY, "error")
                var token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo1ODYsImV4cCI6MTY3Mjc2Mzk2M30.9PvZZ69lWjEX2iWm7NqwNUTJ6nd5YcA80iBro2bep-U"
                //Log.i("debug Token", response.toString())
                    if (response != null && response != "error") token = response.split("=")[1].split(")")[0]
                // intercepteur qui ajoute le `header` d'authentification avec votre token:
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    // sérializeur JSON: transforme le JSON en objets kotlin et inversement
    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun setUpContext(context: Context) {
        appContext = context
    }

    // instance de convertisseur qui parse le JSON renvoyé par le serveur:
    private val converterFactory =
        jsonSerializer.asConverterFactory("application/json".toMediaType())

    // permettra d'implémenter les services que nous allons créer:
    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build()

    val userWebService by lazy {
        retrofit.create(UserWebService::class.java)
    }

    val taskWebService by lazy {
        retrofit.create(TaskWebService::class.java)
    }

    fun reloadRetrofit(){
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val token = PreferenceManager.getDefaultSharedPreferences(appContext).getString(SHARED_PREF_TOKEN_KEY, "")!!.split("=")[1].split(")")[0]

                    if (token != null){
                        Log.i("CURRENT TOKEN ", token)
                    }

                    // intercepteur qui ajoute le `header` d'authentification avec votre token:
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    chain.proceed(newRequest)
                }
                .build())
            .addConverterFactory(converterFactory)
            .build()
    }
}