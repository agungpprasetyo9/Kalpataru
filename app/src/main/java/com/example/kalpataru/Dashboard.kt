package com.example.kalpataru

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView

import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kalpataru.model.WeatherApi
import com.example.kalpataru.model.WeatherService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Dashboard : AppCompatActivity() {
    private lateinit var locationView: TextView
    private lateinit var suhuView: TextView
    private lateinit var AQIView: TextView
    private lateinit var infoAQIView: TextView
    private lateinit var timeView: TextView
    private lateinit var kelembapanView : TextView
    private lateinit var udaraView : TextView
    private lateinit var kualitasUdaraView: TextView
    private lateinit var linearLayoutView: LinearLayout
    private lateinit var runTextView: TextView

    //nav control
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_home -> return@setOnItemSelectedListener true
                R.id.bottom_berita -> {
                    startActivity(Intent(applicationContext, Sensor::class.java))
                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_grafik -> {
                    startActivity(Intent(applicationContext, Sensor::class.java))
                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_sensor -> {
                    startActivity(Intent(applicationContext, Sensor::class.java))
                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
                    finish()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }


        locationView = findViewById(R.id.lokasi)
        suhuView = findViewById(R.id.suhu)
        AQIView = findViewById(R.id.AQI)
        infoAQIView = findViewById(R.id.infoAQI)
        timeView = findViewById(R.id.tanggal)
        kelembapanView = findViewById(R.id.kelembapan)
        udaraView = findViewById(R.id.udara)
        kualitasUdaraView = findViewById(R.id.kualitasudara)
        linearLayoutView =  findViewById(R.id.subinfobg)
        runTextView = findViewById(R.id.runtext)



        runTextView.isSelected = true


        // Replace "YOUR_API_KEY" with the actual API key you obtain from the weather service.
        val apiKey = "8884e1a7a804409096d20524232411"
        val apiUrl = "https://api.weatherapi.com/v1/current.json?q=Yogyakarta&key=$apiKey"

        val weatherService: WeatherService by lazy {
            createWeatherService()
        }
        lifecycleScope.launch {
            try {
                val weatherResponse = weatherService.getCurrentWeather("Yogyakarta", "$apiKey","yes")
                updateUI(weatherResponse)
            } catch (e: Exception) {
                e.printStackTrace()
                locationView.text = "Failed to fetch weather information."
            }
        }
    }

    private fun createWeatherService(): WeatherService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherService::class.java)
    }

    private fun updateUI(weatherResponse: WeatherApi) {
        val location = weatherResponse.location
        val current = weatherResponse.current
        val airQuality = current.air_quality

        locationView.text = "${location.name}"
        suhuView.text = "${current.temp_c}°C"
        kelembapanView.text = "${current.humidity}%"
        timeView.text = "${current.last_updated}"
        udaraView.text = "${current.wind_kph} kph"

//        val pm25Value = 300.0
        val pm25Value = 10.0
//        val pm25Value = airQuality.pm25

        val aqi = calculateAQI(pm25Value)

        setUIForAQI(aqi.toInt())
        AQIView.text = "$aqi"

    }

    fun calculateAQI(pm25: Double): String {

        if (pm25 < 0 || pm25 > 500) {
            return "Invalid input. PM2.5 concentration should be between 0 and 500."
        }

        val breakpoints = listOf(0.0, 12.0, 35.4, 55.4, 150.4, 250.4, 350.4, 500.4)
        val concentrations = listOf(0, 50, 100, 150, 200, 300, 400, 500)
        val index = calculateIndex(pm25, breakpoints, concentrations)

        return "$index"
//
//        return when {
//            pm25 in 0.0..12.0 -> 0
//            pm25 in 12.1..35.4 -> 1
//            pm25 in 35.5..55.4 -> 2
//            pm25 in 55.5..150.4 -> 3
//            pm25 in 150.5..250.4 -> 4
//            else -> 5
//        }
    }
//
//
    fun setUIForAQI(aqi: Int) {
        // Ganti warna latar belakang dan teks sesuai dengan rentang AQI
        when (aqi) {
            in 0 .. 50 -> {

                infoAQIView.text = "Baik"
                runTextView.text = getString(R.string.zero)
            }
            in 51 .. 100 -> {

                infoAQIView.text = "Sedang"
                infoAQIView.setTextColor(Color.parseColor("#FFF7D460"))
                kualitasUdaraView.setTextColor(Color.parseColor("#FFF7D460"))
                AQIView.setTextColor(Color.parseColor("#FFF7D460"))
                linearLayoutView.setBackgroundResource(R.drawable.bg_subinfo1)
                runTextView.text = getString(R.string.one)
            }
            in 101 ..150 -> {

                infoAQIView.text = "Tidak Sehat untuk Kelompok Sensitif"
                infoAQIView.setTextColor(Color.parseColor("#FFFF7E00"))
                kualitasUdaraView.setTextColor(Color.parseColor("#FFFF7E00"))
                AQIView.setTextColor(Color.parseColor("#FFFF7E00"))
                linearLayoutView.setBackgroundResource(R.drawable.bg_subinfo2)
                runTextView.text = getString(R.string.two)

            }
            in 151 .. 200 -> {

                infoAQIView.text = "Tidak Sehat"
                infoAQIView.setTextColor(Color.parseColor("#FFFF0000"))
                kualitasUdaraView.setTextColor(Color.parseColor("#FFFF0000"))
                AQIView.setTextColor(Color.parseColor("#FFFF0000"))
                linearLayoutView.setBackgroundResource(R.drawable.bg_subinfo3)
                runTextView.text = getString(R.string.three)
            }
            in 201 .. 300 -> {

                infoAQIView.text = "Sangat Tidak Sehat"
                infoAQIView.setTextColor(Color.parseColor("#FFA37DB8"))
                kualitasUdaraView.setTextColor(Color.parseColor("#FFA37DB8"))
                AQIView.setTextColor(Color.parseColor("#FFA37DB8"))
                linearLayoutView.setBackgroundResource(R.drawable.bg_subinfo4)
                runTextView.text = getString(R.string.four)
            }
            in 301..500 -> {

                infoAQIView.text = "Berbahaya"
                infoAQIView.setTextColor(Color.parseColor("#FFA07684"))
                kualitasUdaraView.setTextColor(Color.parseColor("#FFA07684"))
                AQIView.setTextColor(Color.parseColor("#FFA07684"))
                linearLayoutView.setBackgroundResource(R.drawable.bg_subinfo5)
                runTextView.text = getString(R.string.five)
            }
        }
    }

//    fun calculateAQI(pm25: Double): String {
//        if (pm25 < 0 || pm25 > 500) {
//            return "Invalid input. PM2.5 concentration should be between 0 and 500."
//        }
//
//        val breakpoints = listOf(0.0, 12.0, 35.4, 55.4, 150.4, 250.4, 350.4, 500.4)
//        val concentrations = listOf(0, 50, 100, 150, 200, 300, 400, 500)
//        val index = calculateIndex(pm25, breakpoints, concentrations)
//
//        return "$index"
//    }

    fun calculateIndex(value: Double, breakpoints: List<Double>, concentrations: List<Int>): Int {
        for (i in 0 until breakpoints.size - 1) {
            if (value >= breakpoints[i] && value <= breakpoints[i + 1]) {
                val bpLow = breakpoints[i]
                val bpHigh = breakpoints[i + 1]
                val concLow = concentrations[i]
                val concHigh = concentrations[i + 1]

                return ((concHigh - concLow) / (bpHigh - bpLow) * (value - bpLow) + concLow).toInt()
            }
        }
        throw IllegalArgumentException("Value not within valid range.")
    }


}