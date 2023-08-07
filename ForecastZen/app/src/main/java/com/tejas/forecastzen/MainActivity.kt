package com.tejas.forecastzen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tejas.forecastzen.Model.ApiUtilities
import com.tejas.forecastzen.Model.ModelClass
import com.tejas.forecastzen.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var n = 0

    override fun onCreate(savedInstanceState: Bundle?) {


        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        val currentDateTime = Calendar.getInstance().time

        binding.day.text = SimpleDateFormat("EEE", Locale.getDefault()).format(currentDateTime)

        binding.date.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(currentDateTime)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.apply {
                decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

                statusBarColor = Color.TRANSPARENT
            }
        }


        val currentTimeMillis = System.currentTimeMillis()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis


        binding.searchBar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // "Done" action was clicked on the keyboard
                getCityWeather(binding.searchBar.text.toString())
                true // Return true to indicate that you've handled the action
            } else {
                false // Return false to indicate that you haven't handled the action
            }
        }



//        Toast.makeText(this, "$hourOfDay", Toast.LENGTH_SHORT).show()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 1..3 -> {
                // MidNight
                binding.backgroundColor.setBackgroundResource(R.drawable.midnight_background)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            in 4..6 -> {
                // Sunrise
                binding.backgroundColor.setBackgroundResource(R.drawable.sunrise_background)
            }
            in 7..9 -> {
                // EarlyMorning
                binding.backgroundColor.setBackgroundResource(R.drawable.early_morning_background)
            }

            in 10..12 -> {
                // Morning
                binding.backgroundColor.setBackgroundResource(R.drawable.morning_background)

            }
            in 13..15 -> {
                // Afternoon
                binding.backgroundColor.setBackgroundResource(R.drawable.afternoon_background)
            }
            in 16..18 -> {
                // Evening
                binding.backgroundColor.setBackgroundResource(R.drawable.evening_background)
            }
            in 19..21 -> {
                // Twilight
                binding.backgroundColor.setBackgroundResource(R.drawable.twilight_background)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            in 22..24 -> {
                // Night
                binding.backgroundColor.setBackgroundResource(R.drawable.night_background)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

    }

    private fun getCityWeather(cityName: String) {

        ApiUtilities.getApiInterface()?.getCityWeatherData(cityName, API_KEY)?.enqueue(object : Callback<ModelClass>
        {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

//                    below two lines are to hide keyboard in Activity
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                    binding.searchBar.clearFocus()
                    binding.searchBar.setText("")

                    setDataOnViews(responseBody)
                } else {
                    // Showing toast for invalid city name
                    Toast.makeText(applicationContext, "No Place Found", Toast.LENGTH_SHORT).show()

                }
            }

            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                // Show toast for network error
                Toast.makeText(applicationContext, "Network Error", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        n = 0
        getCurrentLocation()
    }


    private fun getCurrentLocation() {

        if (checkPermissions())
        {
            if (isLocationEnabled())
            {
//                Toast.makeText(this, "Location Enabled", Toast.LENGTH_SHORT).show()

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }

                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location: Location?=task.result
                    if (location==null)
                    {
                        if (n<3) {
                            Toast.makeText(this, "Loading...", Toast.LENGTH_LONG).show()
                            Handler().postDelayed({ getCurrentLocation() }, 4000)
                            n++
                        }
                        else{
                            Toast.makeText(this, "Can't Get Current Location, Try Searching Instead", Toast.LENGTH_LONG).show()
                        }
                    }
                    else
                    {
//                        Toast.makeText(this, "Latitude: ${location.latitude}", Toast.LENGTH_SHORT).show()
//                        Toast.makeText(this, "Longitude: ${location.longitude}", Toast.LENGTH_SHORT).show()
//                        binding.progressBar.visibility = View.VISIBLE
                        getCurrentLocationWeather(location.latitude.toString(), location.longitude.toString())
                    }
                }
            }

            else
            {
                Toast.makeText(this, "Turn On Location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }

        else
        {
            Toast.makeText(this, "Allow Access to Location", Toast.LENGTH_SHORT).show()
            requestPermission()
        }

    }

    private fun getCurrentLocationWeather(latitude: String, longitude: String) {


        ApiUtilities.getApiInterface()?.getCurrentWeatherData(latitude, longitude, API_KEY)?.enqueue(object : Callback<ModelClass>{
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                if (response.isSuccessful){
                    setDataOnViews(response.body())
                }
            }

            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                Toast.makeText(applicationContext, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }

        })


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDataOnViews(body: ModelClass?) {

        binding.city.text = body!!.name
        binding.country.text = body.sys.country
        binding.type.text = body.weather[0].main
        binding.desc.text = body.weather[0].description.capitalize()
        binding.temp.text = String.format("%.1f°", kelvinToCelcius(body!!.main.temp))
        binding.maxTemp.text = String.format("%.1f°", kelvinToCelcius(body!!.main.temp_max))
        binding.minTemp.text = String.format("%.1f°", kelvinToCelcius(body!!.main.temp_min))
        binding.sunriseTime.text = timeStampToLocalDate(body.sys.sunrise.toLong())
        binding.sunsetTime.text = timeStampToLocalDate(body.sys.sunset.toLong())
        binding.windSpeed.text = body.wind.speed.toString()
        binding.humidity.text = body.main.humidity.toString()
        binding.pressure.text = body.main.pressure.toString()
        binding.celsiusTemp.text = String.format("%.1f°C", kelvinToCelcius(body!!.main.temp))
        binding.fahrenheitTemp.text = String.format("%.1f°F", kelvinToFahrenheit(body!!.main.temp))
        binding.clouds.text = body.clouds.all.toString()
        binding.feelsLike.text = String.format("%.1f", kelvinToCelcius(body.main.feels_like))
        binding.visibility.text = String.format("%.1f", (body.visibility)/1000.0)

        setUi(body.weather[0].id)

    }

    private fun setUi(id: Int) {

//        Toast.makeText(this, "id=$id", Toast.LENGTH_SHORT).show()

        when (id) {
            in 200..202 -> {
                // thunderstorm & rain

                clearAnimations()

                val thunderstorm = binding.animation3
                thunderstorm.visibility = View.VISIBLE
                thunderstorm.setAnimation(R.raw.thunderstorm_animation)
                thunderstorm.playAnimation()

                val rain = binding.animation5
                rain.visibility = View.VISIBLE
                rain.setAnimation(R.raw.rain_animation)
                rain.playAnimation()

                val animation = binding.animation
                animation.setAnimation(R.raw.icon_thunderstorm_rain)
                animation.playAnimation()
            }
            in 210..232 -> {
                // thunderstorm

                clearAnimations()

                val thunderstorm = binding.animation3
                thunderstorm.visibility = View.VISIBLE
                thunderstorm.setAnimation(R.raw.thunderstorm_animation)
                thunderstorm.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_thunderstorm)
                animation.playAnimation()
            }
            in 300..311 -> {
                // light rain

                clearAnimations()

                val light_rain = binding.animation5
                light_rain.visibility = View.VISIBLE
                light_rain.setAnimation(R.raw.rain_animation)
                light_rain.speed = 0.5F
                light_rain.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_light_rain)
                animation.playAnimation()
            }
            in 312..321 -> {
                // heavy rain

                clearAnimations()

                val heavy_rain = binding.animation5
                heavy_rain.visibility = View.VISIBLE
                heavy_rain.setAnimation(R.raw.rain_animation)
                heavy_rain.speed = 1.5F
                heavy_rain.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_heavy_rain)
                animation.playAnimation()
            }
            in 500..501 -> {
                // light rain

                clearAnimations()

                val light_rain = binding.animation5
                light_rain.visibility = View.VISIBLE
                light_rain.setAnimation(R.raw.rain_animation)
                light_rain.speed = 0.5F
                light_rain.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_light_rain)
                animation.playAnimation()
            }
            in 502..531 -> {
                // heavy rain

                clearAnimations()

                val heavy_rain = binding.animation5
                heavy_rain.visibility = View.VISIBLE
                heavy_rain.setAnimation(R.raw.rain_animation)
                heavy_rain.speed = 1.5F
                heavy_rain.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_heavy_rain)
                animation.playAnimation()
            }
            in 600..611 -> {
                // snow

                clearAnimations()

                val snow = binding.animation4
                snow.visibility = View.VISIBLE
                snow.setAnimation(R.raw.snow_animation)
                snow.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_snow)
                animation.playAnimation()
            }
            in 612..620 -> {
                // snow & light rain

                clearAnimations()

                val snow = binding.animation4
                snow.visibility = View.VISIBLE
                snow.setAnimation(R.raw.snow_animation)
                snow.playAnimation()

                val light_rain = binding.animation5
                light_rain.visibility = View.VISIBLE
                light_rain.setAnimation(R.raw.rain_animation)
                light_rain.speed = 0.5F
                light_rain.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_snow_light_rain)
                animation.playAnimation()
            }
            in 621..622 -> {
                // snow & heavy rain

                clearAnimations()

                val snow = binding.animation4
                snow.visibility = View.VISIBLE
                snow.setAnimation(R.raw.snow_animation)
                snow.playAnimation()

                val heavy_rain = binding.animation5
                heavy_rain.visibility = View.VISIBLE
                heavy_rain.setAnimation(R.raw.rain_animation)
                heavy_rain.speed = 1.5F
                heavy_rain.playAnimation()

                val animation = binding.animation
                animation.setAnimation(R.raw.icon_snow_light_rain)
                animation.speed = 1.5F
                animation.playAnimation()
            }
            in 701..781 -> {
                // atmosphere(mist, smoke, fog, haze, sand, dusk, tornado)

                clearAnimations()

                val animation = binding.animation
                animation.setAnimation(R.raw.icon_clear)
                animation.playAnimation()
            }
            800 -> {
                // clear

                clearAnimations()

                val animation = binding.animation
                animation.setAnimation(R.raw.icon_clear)
                animation.playAnimation()
            }
            801 -> {
                // slow clouds

                clearAnimations()

                val slow_clouds = binding.animation1
                slow_clouds.visibility = View.VISIBLE
                slow_clouds.setAnimation(R.raw.clouds_animation)
                slow_clouds.speed = 0.5F
                slow_clouds.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_clouds)
                animation.speed = 0.5F
                animation.playAnimation()
            }
            in 802..803 -> {
                // clouds

                clearAnimations()

                val clouds = binding.animation1
                clouds.visibility = View.VISIBLE
                clouds.setAnimation(R.raw.clouds_animation)
                clouds.playAnimation()

                val animation = binding.animation
                animation.setAnimation(R.raw.icon_clouds)
                animation.playAnimation()
            }
            804 -> {
                // fast clouds

                clearAnimations()

                val fast_clouds = binding.animation1
                fast_clouds.visibility = View.VISIBLE
                fast_clouds.setAnimation(R.raw.clouds_animation)
                fast_clouds.speed = 1.5F
                fast_clouds.playAnimation()


                val animation = binding.animation
                animation.setAnimation(R.raw.icon_clouds)
                animation.speed = 1.5F
                animation.playAnimation()
            }
        }


    }

    private fun clearAnimations() {

        binding.animation1.pauseAnimation()
        binding.animation1.visibility = View.GONE
        binding.animation2.pauseAnimation()
        binding.animation2.visibility = View.GONE
        binding.animation3.pauseAnimation()
        binding.animation3.visibility = View.GONE
        binding.animation4.pauseAnimation()
        binding.animation4.visibility = View.GONE
        binding.animation5.pauseAnimation()
        binding.animation5.visibility = View.GONE
        binding.animation6.pauseAnimation()
        binding.animation6.visibility = View.GONE

    }

    private fun kelvinToCelcius(tempMax: Double): Double {
        return tempMax.minus(273)
    }

    private fun kelvinToFahrenheit(tempKelvin: Double): Double {
        return tempKelvin * 9/5 - 459.67
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun timeStampToLocalDate(timeStamp : Long) : String {
        val localTime = timeStamp.let {
            Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, applicationContext.resources.configuration.locales[0])
        return timeFormat.format(Date.from(localTime.atZone(ZoneId.systemDefault()).toInstant()))
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    private fun checkPermissions(): Boolean
    {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }


    companion object
    {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
        const val API_KEY = "318271193c50f9dd27aee84c78918d52"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
//                getCurrentLocation()

            }
            else
            {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


}