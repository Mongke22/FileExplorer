package com.example.fileexplorer.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class FileDbModel (
    @PrimaryKey
    val path: String,
    val fileHashCode: Long
)