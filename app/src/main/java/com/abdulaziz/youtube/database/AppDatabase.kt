package com.abdulaziz.youtube.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdulaziz.youtube.database.dao.ChannelDao
import com.abdulaziz.youtube.database.dao.PlaylistDao
import com.abdulaziz.youtube.database.dao.SearchDao
import com.abdulaziz.youtube.database.dao.VideoDao
import com.abdulaziz.youtube.database.entity.ChannelEntity
import com.abdulaziz.youtube.database.entity.PlaylistEntity
import com.abdulaziz.youtube.database.entity.SearchEntity
import com.abdulaziz.youtube.database.entity.VideoEntity
import com.abdulaziz.youtube.utils.Converters


@Database(entities = [ChannelEntity::class, VideoEntity::class, SearchEntity::class, PlaylistEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun youtubeDao(): YoutubeDao
    abstract fun channelDao(): ChannelDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun searchDao(): SearchDao
    abstract fun videoDao(): VideoDao

    companion object {
        private var db: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database-name")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
            return db!!
        }
    }

}