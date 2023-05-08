package com.example.fileexplorer.domain

import java.io.File

interface FileRepository {
    suspend fun getModifiedFiles(): ArrayList<File>
}