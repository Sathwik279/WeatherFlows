package com.example.weatherflows

import android.app.Application
import android.app.NotificationChannel
import android.os.Build
import com.example.weatherflows.api.WeatherService
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import android.app.NotificationManager
import android.content.Context


class Weather: Application() {

    lateinit var weatherRepository: WeatherRepository
    val COUNTER_CHANNEL_ID = "counter_channel" // this is the channel to which u want to send notification to

    override fun onCreate(){
        super.onCreate()
        createNotificationChannel()

        val retrofit = Retrofit.Builder().baseUrl("https://api.weatherapi.com/v1/").addConverterFactory(MoshiConverterFactory.create()).build()
        val weatherService = retrofit.create(WeatherService::class.java)

        // as weather service is one of the source of data we abstract it with the repository
        weatherRepository = WeatherRepository(weatherService)
        // when we instantiate the appliacation using this class automatically thsi repo will be instantiated as the weatherService is already instantiated and used to instatiate the repo





    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                COUNTER_CHANNEL_ID,
                "Counter",
                        NotificationManager.IMPORTANCE_HIGH
            )

            channel.description = "Used to show the user home location weather"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}