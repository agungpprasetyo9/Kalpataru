package com.example.kalpataru

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Dashboard : AppCompatActivity() {
    private lateinit var locationView: TextView
    private lateinit var suhuView: TextView
    private lateinit var AQIView: TextView
    private lateinit var timeView: TextView
    private lateinit var kelembapanView : TextView
    private lateinit var udaraView : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        locationView = findViewById(R.id.lokasi)
        suhuView = findViewById(R.id.suhu)
        AQIView = findViewById(R.id.AQI)
        timeView = findViewById(R.id.tanggal)
        kelembapanView = findViewById(R.id.kelembapan)
        udaraView = findViewById(R.id.udara)


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

        val aqi = calculateAQI(airQuality)
        AQIView.text = "$aqi"
    }

    private fun calculateAQI(airQuality: AirQuality): Int {
        return (airQuality.o3 ).toInt()
    }

}