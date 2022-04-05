package com.abdulaziz.youtube.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz.youtube.database.entity.VideoEntity

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideo(videoEntity: VideoEntity)

    @Query("select * from VideoEntity")
    fun getAllVideos(): List<VideoEntity>

    @Query("select *from VideoEntity where video_id = :id")
    fun getVideoById(id: String): VideoEntity

    @Query("SELECT MAX(id) FROM videoentity")
    fun getVideoCount(): Int

    @Query("select exists(select * from videoentity)")
    fun isVideoExist(): Boolean
}