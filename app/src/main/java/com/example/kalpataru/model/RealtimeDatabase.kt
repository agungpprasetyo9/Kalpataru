package com.example.kalpataru.model

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object RealtimeDatabase {
    fun instance() = Firebase.database
}
