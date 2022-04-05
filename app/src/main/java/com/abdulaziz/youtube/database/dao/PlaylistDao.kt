package com.abdulaziz.youtube.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz.youtube.database.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlistentity")
    fun getAllPlaylist(): List<PlaylistEntity>

    @Query("SELECT * FROM playlistentity where id=:id")
    fun getPlaylistById(id:Int): PlaylistEntity
}