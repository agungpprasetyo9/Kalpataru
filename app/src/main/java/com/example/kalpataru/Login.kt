package com.example.kalpataru

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    lateinit var loginBtn: Button
    lateinit var usernameEditText: EditText
    lateinit var passwordEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginBtn = findViewById(R.id.Login_btn)
        usernameEditText = findViewById(R.id.Username_edit_text)
        passwordEditText = findViewById(R.id.Password_edit_text)

        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateLogin(username, password)) {
                Toast.makeText(this, "Logged in Berhasil!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // You can add more sophisticated login validation here, such as calling a web service
        return username == "user" && password == "password"
    }
}