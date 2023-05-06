package com.example.fileexplorer.presentation.recycler

import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fileexplorer.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class FileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private var name: TextView = view.findViewById(R.id.tvFileName)
    private var size: TextView = view.findViewById(R.id.tvFileSize)
    private var picture: ImageView = view.findViewById(R.id.imgFileType)

    fun setName(file: File) {
        Log.i("Имя", file.name)
        name.isSelected = true
        name.text = file.name
        picture.setImageResource(getPictureByFileType(file))
    }

    fun setSize(file: File) {
        var sizeString =
            Formatter.formatShortFileSize(view.context, Files.size(Paths.get(file.absolutePath)))
        if (file.isDirectory) {
            var itemsCount = 0
            for (item in file.listFiles() ?: emptyArray()) {
                if (!item.isHidden) {
                    itemsCount++
                }
            }
            sizeString += ", $itemsCount элем."
        }
        size.text = sizeString
    }

    private fun getPictureByFileType(file: File): Int {
        return if (file.isDirectory) {
            R.drawable.folder
        } else if (file.name.lowercase().endsWith(".png")) {
            R.drawable.png
        } else if (file.name.lowercase().endsWith(".jpg") || file.name.lowercase()
                .endsWith(".jpeg")
        ) {
            R.drawable.jpg
        } else if (file.name.lowercase().endsWith(".txt")) {
            R.drawable.txt
        } else if (file.name.lowercase().endsWith(".mp3") ||
            file.name.lowercase().endsWith(".wav")
        ) {
            R.drawable.mp3
        } else if (file.name.lowercase().endsWith(".mp4")) {
            R.drawable.mp4
        } else if (file.name.lowercase().endsWith(".pdf")) {
            R.drawable.pdf
        } else if (file.name.lowercase().endsWith(".doc")) {
            R.drawable.doc
        } else if (file.name.lowercase().endsWith(".apk")) {
            R.drawable.apk
        } else {
            R.drawable.unknown
        }
    }

}