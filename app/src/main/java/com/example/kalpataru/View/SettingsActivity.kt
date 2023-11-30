package com.example.kalpataru.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kalpataru.R


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
//        bottomNavigationView.selectedItemId = R.id.bottom_settings
//        bottomNavigationView.setOnItemSelectedListener { item: MenuItem ->
//            when (item.itemId) {
//                R.id.bottom_home -> {
//                    startActivity(Intent(applicationContext, MainActivity::class.java))
//                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
//                    finish()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.bottom_search -> {
//                    startActivity(Intent(applicationContext, ArtikelPage::class.java))
//                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
//                    finish()
//                    return@setOnItemSelectedListener true
//                }
//
//                R.id.bottom_settings -> return@setOnItemSelectedListener true
//                R.id.bottom_profile -> {
//                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
//                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
//                    finish()
//                    return@setOnItemSelectedListener true
//                }
//            }
//            false
//        }
    }
}