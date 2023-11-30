package com.example.kalpataru.View

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.kalpataru.R
import com.google.firebase.auth.FirebaseAuth

class Splash : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        auth = FirebaseAuth.getInstance()

        // Pengecekan apakah pengguna sudah login
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Pengguna sudah login, arahkan ke halaman Dashboard
            navigateToDashboard()
        } else {
            // Pengguna belum login, arahkan ke halaman Login
            navigateToLogin()
        }
    }

    private fun navigateToDashboard() {
        Handler().postDelayed({
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun navigateToLogin() {
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java) // Gantilah dengan kelas LoginActivity yang sesuai
            startActivity(intent)
            finish()
        }, 1000)
    }
}