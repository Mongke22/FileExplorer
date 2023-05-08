package com.example.fileexplorer

import java.io.File


fun hashCode(file: File): Long{
    return file.hashCode().toLong() + (file.lastModified() * file.length())
}

object Constants{
    const val EMPTY_STRING = ""
    const val SELECTED_ELEVATION_FLOAT = 8f
    const val DEFAULT_ELEVATION_FLOAT = 2f
    const val EXTERNAL_STORAGE = "EXTERNAL_STORAGE"
    const val NO_PATH_ERROR = "no path name"


}
enum class Filter {
    DateCreation, FileSize, FileType, FileName
}
