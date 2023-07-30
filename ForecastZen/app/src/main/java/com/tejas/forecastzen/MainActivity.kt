package com.tejas.forecastzen

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.View
import android.view.WindowManager
import android.widget.TextClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.tejas.forecastzen.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.hide()


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


//        Toast.makeText(this, "$hourOfDay", Toast.LENGTH_SHORT).show()
        when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 6..11 -> {
                // Morning
                binding.backgroundColor.setBackgroundResource(R.drawable.morning_background)

            }
            in 12..17 -> {
                // Afternoon
                binding.backgroundColor.setBackgroundResource(R.drawable.afternoon_background)
            }
            else -> {
                // Night
                binding.backgroundColor.setBackgroundResource(R.drawable.night_background)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}