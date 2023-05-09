package com.example.fileexplorer

import android.os.Environment
import java.io.File


fun hashCode(file: File): Long{
    return file.hashCode().toLong() + (file.lastModified() * file.length())
}

object Constants{
    const val EMPTY_STRING = ""
    const val SELECTED_ELEVATION_FLOAT = 8f
    const val DEFAULT_ELEVATION_FLOAT = 2f

    //Корень отображаемых файлов в приложении
    val ROOT_FILE = Environment.getExternalStorageDirectory()
}

//Фильтры для списка файлов
enum class Filter {
    DateCreation, FileSize, FileType, FileName
}
