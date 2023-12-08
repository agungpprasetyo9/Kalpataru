package com.example.kalpataru.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kalpataru.R
import com.example.kalpataru.databinding.PrediksiAqiBinding
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.round

class Prediksi : AppCompatActivity() {
    private var _binding: PrediksiAqiBinding? = null
    private val binding get() = _binding!!
    private val apiKey = "8884e1a7a804409096d20524232411"
    private val apiUrl =
        "https://api.weatherapi.com/v1/forecast.json?q=-7.8011945,110.364917&days=8&aqi=yes&alerts&Key=8884e1a7a804409096d20524232411"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PrediksiAqiBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialize Volley request queue
        val requestQueue = Volley.newRequestQueue(this)

        // Create a JSON request to fetch data from the API
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, apiUrl, null,
            { response ->
                // Handle the API response here
                val location = response.getJSONObject("location")
                val current = response.getJSONObject("current")
                val forecast = response.getJSONObject("forecast")
                val airQuality = current.getJSONObject("air_quality")
                val name = location.getString("name")
                val tempInCelcius = current.getDouble("temp_c")
                val lastUpdate = current.getString("last_updated")
                val humidity = current.getInt("humidity")
                val pressureIn = current.getDouble("pressure_in")
                val forecastday = forecast.getJSONArray("forecastday")

                for (i in 0 until minOf(forecastday.length(), 5)) {
                    val forecastcurrent = forecastday.getJSONObject(i)
                    val date = forecastcurrent.getString("date")
                    val day = forecastcurrent.getJSONObject("day")
                    val air_quality = day.getJSONObject("air_quality")
                    val pm25 = round(air_quality.getDouble("pm2_5"))
                    val AQI = calculateAQI(pm25).toInt()

                    val AQICategory = when {
                        AQI <= 50 -> "Good"
                        AQI <= 100 -> "Moderate"
                        AQI <= 150 -> "Unhealthy for Sensitive Groups"
                        AQI <= 200 -> "Unhealthy"
                        AQI <= 300 -> "Very Unhealthy"
                        else -> "Hazardous"
                    }

                    Log.d("ForecastDay", "$date - PM2.5: $pm25 - AQI: $AQI - Category: ${AQICategory.toString()}")
                }


            },
            { error ->
                // Handle error
                Log.e("WeatherData", "Error: ${error.message}")
            }
        )

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest)
    }
    fun calculateAQI(pm25: Double): String {

        if (pm25 < 0 || pm25 > 500) {
            return "Invalid input. PM2.5 concentration should be between 0 and 500."
        }

        val breakpoints = listOf(0.0, 12.0, 35.4, 55.4, 150.4, 250.4, 350.4, 500.4)
        val concentrations = listOf(0, 50, 100, 150, 200, 300, 400, 500)
        val index = calculateIndex(pm25, breakpoints, concentrations)

        return "$index"

    }
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
    private fun formatDate(inputDate: String): String {
        // Input date format
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Output date format
        val outputFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        try {
            // Parse the input date string into a Date object
            val date = inputFormat.parse(inputDate)

            // Format the Date object to the desired output format
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "" // Return an empty string in case of an error
    }
}