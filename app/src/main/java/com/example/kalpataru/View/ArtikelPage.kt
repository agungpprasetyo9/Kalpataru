package com.example.kalpataru.View

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalpataru.R
import com.example.kalpataru.databinding.ItemViewBinding
import com.example.kalpataru.model.MyResponse
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArtikelPage : AppCompatActivity() {
    private lateinit var binding: ItemViewBinding

    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var newsAdapter: AdapterNews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ItemViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //nav control
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.bottom_berita

        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.bottom_berita -> return@setOnItemSelectedListener true
                R.id.bottom_home -> {
                    val intent = Intent(applicationContext, Dashboard::class.java)
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


        setupViews()
        observeNewsData()
        viewModel.getAllNotes()
    }

    private fun observeNewsData() {
        viewModel.newsData.observe(this@ArtikelPage) { response ->
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
                    Toast.makeText(this@ArtikelPage, response.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViews() {
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ArtikelPage, LinearLayoutManager.VERTICAL, false)
            adapter = newsAdapter
        }
    }
}
