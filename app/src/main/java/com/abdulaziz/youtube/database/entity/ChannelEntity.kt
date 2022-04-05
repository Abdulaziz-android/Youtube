package com.abdulaziz.youtube.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val channel_id:String,
    val title:String,
    val image:String,
)
