package com.example.kalpataru.View

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kalpataru.R
import com.example.kalpataru.databinding.ActivityDashboardBinding
import com.example.kalpataru.model.MyResponse
import com.example.kalpataru.model.WeatherApi
import com.example.kalpataru.model.WeatherService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

@AndroidEntryPoint
class Dashboard : AppCompatActivity() {
    @Inject
    lateinit var newsAdapter: NewsAdapter
    private val viewModel: MainViewModel by viewModels()

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
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var recyclerVieww: RecyclerView // Adding RecyclerView variable

    // nav control
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsAdapter = NewsAdapter(this)

        recyclerVieww = findViewById<RecyclerView>(R.id.recyclervieww)
        recyclerVieww.apply {
            layoutManager = LinearLayoutManager(this@Dashboard, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
        }

        val logoutButton: ImageButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            logout()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_home -> return@setOnItemSelectedListener true
                R.id.bottom_berita -> {
                    val intent = Intent(applicationContext, ArtikelPage::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_grafik -> {
                    val intent = Intent(applicationContext, Grafik::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    finish()
                    return@setOnItemSelectedListener true
                }
                R.id.bottom_sensor -> {
                    val intent = Intent(applicationContext, Sensor::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
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
        linearLayoutView = findViewById(R.id.subinfobg)
        runTextView = findViewById(R.id.runtext)

        runTextView.isSelected = true

        val apiKey = "75035ebf2f6741bdbd6130923230212"
        val apiUrl = "https://api.weatherapi.com/v1/current.json?q=Yogyakarta&key=$apiKey"

        val weatherService: WeatherService by lazy {
            createWeatherService()
        }

        lifecycleScope.launch {
            try {
                val weatherResponse = weatherService.getCurrentWeather("Yogyakarta", "$apiKey", "yes")
                updateUI(weatherResponse)
            } catch (e: Exception) {
                e.printStackTrace()
                locationView.text = "Failed to fetch weather information."
            }
        }

        setupViews()
        observeNewsData()
        viewModel.getAllNotes()
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

        binding.lokasi.text = "${location.name}"
        binding.suhu.text = "${current.temp_c}°C"
        binding.kelembapan.text = "${current.humidity}%"
        binding.tanggal.text = "${current.last_updated}"
        binding.udara.text = "${current.wind_kph} kph"

        val pm25Value = airQuality.pm25
        val aqi = calculateAQI(pm25Value)

        setUIForAQI(aqi.toInt())
        binding.AQI.text = "$aqi"
    }

    private fun calculateAQI(pm25: Double): String {
        if (pm25 < 0 || pm25 > 500) {
            return "Invalid input. PM2.5 concentration should be between 0 and 500."
        }

        val breakpoints = listOf(0.0, 12.0, 35.4, 55.4, 150.4, 250.4, 350.4, 500.4)
        val concentrations = listOf(0, 50, 100, 150, 200, 300, 400, 500)
        val index = calculateIndex(pm25, breakpoints, concentrations)

        return "$index"
    }

    private fun calculateIndex(value: Double, breakpoints: List<Double>, concentrations: List<Int>): Int {
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

    private fun setUIForAQI(aqi: Int) {
        when (aqi) {
            in 0..50 -> {
                binding.infoAQI.text = "Baik"
                binding.runtext.text = getString(R.string.zero)
            }
            in 51..100 -> {
                binding.infoAQI.text = "Sedang"
                binding.infoAQI.setTextColor(Color.parseColor("#FFF7D460"))
                binding.kualitasudara.setTextColor(Color.parseColor("#FFF7D460"))
                binding.AQI.setTextColor(Color.parseColor("#FFF7D460"))
                binding.subinfobg.setBackgroundResource(R.drawable.bg_subinfo1)
                binding.runtext.text = getString(R.string.one)
            }
            in 101..150 -> {
                binding.infoAQI.text = "Tidak Sehat untuk Kelompok Sensitif"
                binding.infoAQI.setTextColor(Color.parseColor("#FFFF7E00"))
                binding.kualitasudara.setTextColor(Color.parseColor("#FFFF7E00"))
                binding.AQI.setTextColor(Color.parseColor("#FFFF7E00"))
                binding.subinfobg.setBackgroundResource(R.drawable.bg_subinfo2)
                binding.runtext.text = getString(R.string.two)
            }
            in 151..200 -> {
                binding.infoAQI.text = "Tidak Sehat"
                binding.infoAQI.setTextColor(Color.parseColor("#FFFF0000"))
                binding.kualitasudara.setTextColor(Color.parseColor("#FFFF0000"))
                binding.AQI.setTextColor(Color.parseColor("#FFFF0000"))
                binding.subinfobg.setBackgroundResource(R.drawable.bg_subinfo3)
                binding.runtext.text = getString(R.string.three)
            }
            in 201..300 -> {
                binding.infoAQI.text = "Sangat Tidak Sehat"
                binding.infoAQI.setTextColor(Color.parseColor("#FFA37DB8"))
                binding.kualitasudara.setTextColor(Color.parseColor("#FFA37DB8"))
                binding.AQI.setTextColor(Color.parseColor("#FFA37DB8"))
                binding.subinfobg.setBackgroundResource(R.drawable.bg_subinfo4)
                binding.runtext.text = getString(R.string.four)
            }
            in 301..500 -> {
                binding.infoAQI.text = "Berbahaya"
                binding.infoAQI.setTextColor(Color.parseColor("#FFA07684"))
                binding.kualitasudara.setTextColor(Color.parseColor("#FFA07684"))
                binding.AQI.setTextColor(Color.parseColor("#FFA07684"))
                binding.subinfobg.setBackgroundResource(R.drawable.bg_subinfo5)
                binding.runtext.text = getString(R.string.five)
            }
        }
    }

    private fun logout() {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun observeNewsData() {
        viewModel.newsData.observe(this@Dashboard) { response ->
            when (response.status) {
                MyResponse.Status.LOADING -> {
                    binding.loading.visibility = View.VISIBLE
                }
                MyResponse.Status.SUCCESS -> {
                    binding.loading.visibility = View.GONE
                    response?.data?.articles?.let { newsAdapter.submitData(it) }
                }
                MyResponse.Status.ERROR -> {
                    binding.loading.visibility = View.GONE
                    Toast.makeText(this@Dashboard, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViews() {
        binding.recyclervieww.apply {
            layoutManager = LinearLayoutManager(this@Dashboard, LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
        }
    }
}
