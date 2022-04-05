package com.abdulaziz.youtube.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val playlistName:String,
    val photo_link:String,
    val list: List<String?>
)
