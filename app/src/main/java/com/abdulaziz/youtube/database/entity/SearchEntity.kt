package com.abdulaziz.youtube.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchEntity(
    val id: Int = 0,
    @PrimaryKey
    val text:String
)
