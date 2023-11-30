package com.example.kalpataru

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kalpataru.View.MainViewModel
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
