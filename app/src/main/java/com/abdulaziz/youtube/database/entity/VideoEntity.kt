package com.abdulaziz.youtube.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VideoEntity(
    val id:Int =0,
    @PrimaryKey
    val video_id:String,
    val photo:String,
    val duration:Int,
    val title:String,
    val channel_name:String,
    val channel_id:String,
    val time:Int = 0,
    val isSeen:Boolean = false,
    var isSaved:Boolean = false,
    var isLiked:Boolean = false,
)
