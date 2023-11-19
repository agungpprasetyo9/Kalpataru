package com.example.kalpataru

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Assuming you have a Button in your layout with the ID "button"
        val button: Button = findViewById(R.id.button)

        // Set an OnClickListener for the button
        button.setOnClickListener {
            // Create an Intent to start SecondActivity
            val intent = Intent(this, Dashboard::class.java)

            // Optionally, you can pass data to the second activity using extras
            intent.putExtra("key", "Hello from MainActivity!")

            // Start the second activity
            startActivity(intent)
        }
    }
}