package com.example.kalpataru

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Sensor : AppCompatActivity() {
    private lateinit var tempTextView: TextView
    private lateinit var humidTextView: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sensor)

        // Initialize UI elements
        tempTextView = findViewById(R.id.tempTextView)
        humidTextView = findViewById(R.id.humidTextView)

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("DHT11_Data")

        // Read data from the "temp" node
        databaseReference.child("Temperature").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempValue = snapshot.value.toString()
                tempTextView.text = "Suhu: $tempValue°C"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Read data from the "humid" node
        databaseReference.child("Humidity").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val humidValue = snapshot.value.toString()
                humidTextView.text = "Kelembapan: $humidValue%"
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}