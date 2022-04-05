package com.abdulaziz.youtube.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdulaziz.youtube.database.entity.SearchEntity

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertText(searchEntity: SearchEntity)

    @Query("SELECT MAX(id) FROM searchentity")
    fun getSearchCount(): Int

    @Query("SELECT * FROM searchentity")
    fun getSearchTexts(): List<SearchEntity>

}