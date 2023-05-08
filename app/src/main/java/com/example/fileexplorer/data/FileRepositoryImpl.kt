package com.example.fileexplorer.data

import android.app.Application
import com.example.fileexplorer.data.database.AppDataBase
import com.example.fileexplorer.data.database.FileDbModel
import com.example.fileexplorer.domain.FileRepository
import com.example.fileexplorer.hashCode
import java.io.File

class FileRepositoryImpl(private val application: Application) : FileRepository {

    private val filesDao = AppDataBase.getInstance(application).filesDao()

    private val modifiedFiles = ArrayList<File>()

    override suspend fun getModifiedFiles(): ArrayList<File> {
        modifiedFiles.clear()

        val internalStorage = System.getenv("EXTERNAL_STORAGE")
        val file = File(internalStorage ?: throw Exception("no path name"))

        checkIfModified(file)

        return modifiedFiles
    }

    private suspend fun checkIfModified(file: File) {
        val hash = hashCode(file)
        if (file.isDirectory) {
            for (singleFile in file.listFiles() ?: emptyArray()) {
                checkIfModified(singleFile)
            }
        } else {
            if (filesDao.checkFileExists(file.absolutePath) == 1) {
                if (filesDao.getFileHashCode(file.absolutePath) !=
                    hash
                ) {
                    modifiedFiles.add(file)
                    filesDao.insertFileInfo(
                        FileDbModel(file.absolutePath,
                            hash
                        )
                    )
                }
            } else {
                modifiedFiles.add(file)
                filesDao.insertFileInfo(
                    FileDbModel(file.absolutePath,
                        hash
                    )
                )
            }
        }
    }
}