package com.example.fileexplorer.presentation.recycler

import android.text.format.Formatter
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fileexplorer.R
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.io.path.readAttributes

class FileViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private var name: TextView = view.findViewById(R.id.tvFileName)
    private var size: TextView = view.findViewById(R.id.tvFileSize)
    private var time: TextView = view.findViewById(R.id.tvFileCreationTime)
    private var picture: ImageView = view.findViewById(R.id.imgFileType)

    fun setName(file: File) {
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

    fun setTime(file: File){
        val attr =
            Paths.get(file.absolutePath).readAttributes<BasicFileAttributes>()
        val zonedTime = attr.creationTime().toInstant().atZone(ZoneId.systemDefault())
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yy - HH:mm")
        time.text = zonedTime.format(formatter)

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