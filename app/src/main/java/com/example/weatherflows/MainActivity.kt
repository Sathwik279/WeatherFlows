package com.example.weatherflows

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            weatherViewModel.homeWeather.collect { homeWeather ->
                val location = homeWeather.location.name
                val temp = homeWeather.current.temp_c.toString()

                val service = NotificationService(this@MainActivity)
                service.showNotification(location, temp)
            }
        }



        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val weatherRepository = (application as Weather).weatherRepository
        weatherViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WeatherViewModel(weatherRepository) as T
            }
        })[WeatherViewModel::class.java]

        if (checkPermissions()) {
            getUserLocation { loc ->
                lifecycleScope.launch { weatherViewModel.updateUserHomeLoc(loc)
                    weatherViewModel.fetchHomeCurrentWeather()
                }
            }
        } else {
            requestPermissions()
        }

        val prefs = getSharedPreferences("my-prefs-file", MODE_PRIVATE)
        val userName = prefs.getString("username", null)
        if (userName == null) {
            askForUserName(prefs)
        } else {
            showUserName(userName)
        }

        val countryName = findViewById<TextView>(R.id.countryName)
        val countryTemp = findViewById<TextView>(R.id.countryTemp)
        val errorView = findViewById<TextView>(R.id.error)
        val userCountry = findViewById<EditText>(R.id.country)
        val fetchButton = findViewById<Button>(R.id.fetch)
        val homeLocation = findViewById<TextView>(R.id.userHomeLoc)
        val homeLocationTemp = findViewById<TextView>(R.id.homeLocationTemp)

        fetchButton.setOnClickListener { weatherViewModel.fetchCurrentWeather() }

        userCountry.addTextChangedListener { country ->
            weatherViewModel.setUserCountry(country.toString())
        }

        lifecycleScope.launch {
            weatherViewModel.currentWeather.collect {
                countryName.text = it.location.name
                countryTemp.text = it.current.temp_c.toString()
            }
        }

        lifecycleScope.launch {
            weatherViewModel.homeWeather.collect { homeLocation.text = it.location.name
                homeLocationTemp.text = it.current.temp_c.toString()
            }
        }

        weatherViewModel.error.observe(this) { errorView.text = it }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation { loc ->
                lifecycleScope.launch { weatherViewModel.updateUserHomeLoc(loc)
                    weatherViewModel.fetchHomeCurrentWeather()
                }
            }
        }
    }

    private fun askForUserName(prefs: SharedPreferences) {
        val builder = AlertDialog.Builder(this)
            .setTitle("Enter your Name")
        val input = EditText(this)
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val enteredName = input.text.toString().trim()
            if (enteredName.isNotEmpty()) {
                prefs.edit().putString("username", enteredName).apply()
                showUserName(enteredName)
            }
        }
        builder.setCancelable(false).show()
    }

    private fun showUserName(userName: String) {
        findViewById<TextView>(R.id.username).text = "Hi, $userName!"
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1001)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation(onResult: (String) -> Unit) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val city = addresses?.firstOrNull()?.locality ?: "City not found"
                onResult(city)
            } else {
                onResult("Location not available")
            }
        }
    }
}
