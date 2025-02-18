package com.example.weatherflows
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.example.weatherflows.model.HomeWeatherResponse

class WeatherViewModel(private val weatherRepository: WeatherRepository): ViewModel() {

//    companion object {
//        var latestHomeLocation: String =""
//        var latestHomeTemp:String = ""
//    }

    val currentWeather: StateFlow<WeatherResponse>
        get() = weatherRepository.weather

    val homeWeather: StateFlow<HomeWeatherResponse>
        get() = weatherRepository.homeWeather

    private val _homeLocation = MutableStateFlow<String>("")
    val homeLocation: StateFlow<String>
        get() = _homeLocation

    // we take  this location from user inorder to fetch the temperature
    private val _userLocation = MutableStateFlow<String>("")
    val userLocation: StateFlow<String> get() = _userLocation



    val error: LiveData<String> = weatherRepository.error

    fun setUserCountry(location: String){
        viewModelScope.launch{
            _userLocation.emit(location)
        }

    }

     fun fetchCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO){
            weatherRepository.fetchWeather(userLocation.value?:"")
        }
    }

    fun fetchHomeCurrentWeather(){
        viewModelScope.launch(Dispatchers.IO){
            weatherRepository.fetchHomeWeather(homeLocation.value?:"")
        }
    }

    //Yes, exactly! You can collect StateFlow not only in the UI but also inside the ViewModel using viewModelScope.launch.
    suspend fun updateUserHomeLoc(home: String){
        _homeLocation.emit(home)
    }
}