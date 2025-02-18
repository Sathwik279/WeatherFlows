package com.example.weatherflows
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import com.example.weatherflows.api.WeatherService
import com.example.weather.model.WeatherResponse
import com.example.weatherflows.model.CurrentWeather
import com.example.weatherflows.model.HomeWeatherResponse
import com.example.weatherflows.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherRepository(private val weatherService: WeatherService){
    private val apiKey = "c322af79212f437080680656251302"
    
    private val weatherLiveData = MutableStateFlow<WeatherResponse>(WeatherResponse(
        Location("N/A"),
        CurrentWeather(0.0)
    ))
    val weather: StateFlow<WeatherResponse>
        get() = weatherLiveData

    private val homeWeatherFlowData = MutableStateFlow<HomeWeatherResponse>(HomeWeatherResponse(
        Location("N/A"),
        CurrentWeather(0.0)
    ))
    val homeWeather: StateFlow<HomeWeatherResponse>
        get() = homeWeatherFlowData


    private val errorLiveData = MutableLiveData<String>()
    val error: LiveData<String>
        get() = errorLiveData


    suspend fun fetchWeather(location:String){
        Log.d("weatherLo","Wind is low")

        try{
            val weatherList = weatherService.getCurrentWeather(apiKey,location,"no")
            Log.d("weatherLo",weatherList.location.name+" "+weatherList.current.temp_c)
            weatherLiveData.emit(weatherList)
        }catch(exception: Exception){
            errorLiveData.postValue("An error occured: ${exception.message}")
        }
    }

    suspend fun fetchHomeWeather(location:String){
        Log.d("weatherLo","Wind is low")

        try{
            val weatherList = weatherService.getHomeCurrentWeather(apiKey,location,"no")
            Log.d("weatherLo",weatherList.location.name+" "+weatherList.current.temp_c)
            homeWeatherFlowData.emit(weatherList)
        }catch(exception: Exception){
            errorLiveData.postValue("An error occured: ${exception.message}")
        }
    }
}
