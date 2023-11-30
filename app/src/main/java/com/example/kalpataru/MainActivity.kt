package com.example.kalpataru

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView




class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item: MenuItem->
            when (item.itemId) {
                R.id.bottom_home -> return@setOnItemSelectedListener true
                R.id.bottom_search -> {
                    startActivity(Intent(applicationContext, ArtikelPage::class.java))
                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
                    finish()
                    return@setOnItemSelectedListener true
                }

                R.id.bottom_profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_kanan, R.anim.slide_out_kiri)
                    finish()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }


    }
}