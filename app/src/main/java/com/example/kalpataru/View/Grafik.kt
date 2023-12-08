package com.example.kalpataru.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kalpataru.R
import com.example.kalpataru.databinding.ActivityGrafikBinding
import java.text.SimpleDateFormat
import java.util.*

class Grafik : AppCompatActivity() {

    private var _binding: ActivityGrafikBinding? = null
    private val binding get() = _binding!!
//    private val apiKey = "2dbf726a758b40e2a4d101106202810"

    //agung api key
    private val apiKey = "8884e1a7a804409096d20524232411"
    private val apiUrl =
        "https://api.weatherapi.com/v1/forecast.json?key=$apiKey&q=-7.8011945,110.364917&days=8&aqi=yes&alerts"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityGrafikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //navbar control
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.bottom_grafik

        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_grafik -> return@setOnItemSelectedListener true
                R.id.bottom_home -> {
                    val intent = Intent(applicationContext, Dashboard::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_berita -> {
                    val intent = Intent(applicationContext, ArtikelPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_sensor -> {
                    val intent = Intent(applicationContext, Sensor::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                    finish()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }


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
                val region = location.getString("region")
                val name = location.getString("name")
                val tempInCelcius = current.getDouble("temp_c")
                val lastUpdate = current.getString("last_updated")
                val humidity = current.getInt("humidity")
                val pressureIn = current.getDouble("pressure_in")
                val windDegree = current.getInt("wind_degree")
                val forecastday = forecast.getJSONArray("forecastday")


                val getPm25 = airQuality.getDouble("pm2_5")
                val getCo = airQuality.getDouble("co")
                val getNo2 = airQuality.getDouble("no2")
                val getO3 = airQuality.getDouble("o3")
                val getSo2 = airQuality.getDouble("so2")
                val getPm10 = airQuality.getDouble("pm10")

                // Log the parsed data
                binding.apply {
                    kabupaten.text = "Di kota $name"
                    suhu.text = "$tempInCelcius °C"
                    latestTemperature.text = "Update terakhir ${formatDate(lastUpdate)}"
                    kelembabanUdara.text = "$humidity %"
                    indexUdara.text = "Index udara $pressureIn"


                    pm25.text = getPm25.toString()
                    co.text = getCo.toString()
                    no2.text = getNo2.toString()
                    o3.text = getO3.toString()
                    so2.text = getSo2.toString()
                    pm10.text = getPm10.toString()
                    hc.text = humidity.toString()
                    kualitasUdaraInNumber.text = calculateAQI(getPm25)
                    // Find the maximum value
                    val maxVal = maxOf(getPm25, getCo, getNo2, getO3, getSo2, getPm10, humidity.toDouble())

                    // Calculate the scaling factor
                    val scalingFactor = 100.0 / maxVal

                    // Scale the values and set them to percentages
                    val pm25Percent = getPm25 * scalingFactor
                    val coPercent = getCo * scalingFactor
                    val no2Percent = getNo2 * scalingFactor
                    val o3Percent = getO3 * scalingFactor
                    val so2Percent = getSo2 * scalingFactor
                    val pm10Percent = getPm10 * scalingFactor
                    val humidityPercent = humidity.toDouble() * scalingFactor



                    // Define your percentage values in a map
                    val percentageMap = mapOf(
                        "pm25Percentage" to pm25Percent,
                        "coPercentage" to coPercent,
                        "no2Percentage" to no2Percent,
                        "o3Percentage" to o3Percent,
                        "so2Percentage" to so2Percent,
                        "pm10Percentage" to pm10Percent,
                        "humidityPercentage" to humidityPercent
                    )

                    // Define the maximum percentage (100%)
                    val maxPercentage = 100.0

                    // Define the maximum height in dp when the percentage is 100%
                    val maxDpHeight = 150

                    setPercentageHeights(percentageMap, maxPercentage, maxDpHeight)

                    progressBar.visibility = View.GONE
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

    private fun setPercentageHeights(percentageMap: Map<String, Double>, maxPercentage: Double, maxDpHeight: Int) {
        for ((viewId, percentage) in percentageMap) {
            // Calculate the height in dp based on the percentage
            val heightDp = (percentage / maxPercentage) * maxDpHeight

            // Convert dp to pixels
            val heightPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                heightDp.toFloat(),
                resources.displayMetrics
            ).toInt()

            // Set the calculated height to the view
            when (viewId) {
                "pm25Percentage" -> {
                    binding.pm25Percentage.layoutParams.height = heightPixels
                    binding.pm25Percentage.requestLayout()
                }
                "pm10Percentage" -> {
                    binding.pm10Percentage.layoutParams.height = heightPixels
                    binding.pm10Percentage.requestLayout()
                }
                "coPercentage" -> {
                    binding.coPercentage.layoutParams.height = heightPixels
                    binding.coPercentage.requestLayout()
                }
                "hcPercentage" -> {
                    binding.hcPercentage.layoutParams.height = heightPixels
                    binding.hcPercentage.requestLayout()
                }
                "no2Percentage" -> {
                    binding.no2Percentage.layoutParams.height = heightPixels
                    binding.no2Percentage.requestLayout()
                }
                "o3Percentage" -> {
                    binding.o3Percentage.layoutParams.height = heightPixels
                    binding.o3Percentage.requestLayout()
                }
                "so2Percentage" -> {
                    binding.so2Percentage.layoutParams.height = heightPixels
                    binding.so2Percentage.requestLayout()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
