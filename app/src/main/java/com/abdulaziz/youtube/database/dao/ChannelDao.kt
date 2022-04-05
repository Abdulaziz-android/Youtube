package com.abdulaziz.youtube.database.dao

import androidx.room.*
import com.abdulaziz.youtube.database.entity.ChannelEntity

@Dao
interface ChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChannel(channelEntity: ChannelEntity)

    @Delete
    fun removeChannel(channelEntity: ChannelEntity)

    @Query("select exists(select * from channelentity)")
    fun isChannelExist(): Boolean

    @Query("select *from ChannelEntity where channel_id = :id")
    fun getChannelById(id: String): ChannelEntity

    @Query("select * from ChannelEntity")
    fun getAllChannels(): List<ChannelEntity>
}