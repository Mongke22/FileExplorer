package com.example.fileexplorer.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FilesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileInfo(file: FileDbModel)

    @Query("SELECT fileHashCode FROM files WHERE path == :filePath LIMIT 1")
    suspend fun getFileHashCode(filePath: String): Long

    @Query("SELECT COUNT(*) FROM files where path == :filePath")
    suspend fun checkFileExists(filePath: String): Int
}