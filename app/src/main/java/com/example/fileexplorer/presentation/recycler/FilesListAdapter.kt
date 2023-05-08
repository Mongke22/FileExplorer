package com.example.fileexplorer.presentation.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.fileexplorer.R
import java.io.File
import java.io.FileWriter
import java.nio.file.attribute.FileTime

class FilesListAdapter : ListAdapter<File, FileViewHolder>(FilesDiffUtilCallBack()) {

    var fileItemOnClickListener: ((File) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_container, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        Log.i("hashcode", "${file.name}:${file.hashCode()}")
        if(file.name == "NewTextFile1.txt"){
            try {
                FileWriter(file.absolutePath, false).use { writer ->
                    // запись всей строки
                    val text = "Hello цйцуйцуваывп!"
                    writer.write(text)
                    // запись по символам
                    writer.append('\n')
                    writer.append('E')
                    writer.flush()
                }
            } catch (ex: Exception) {
                Log.i("exception",ex.message?:"")
            }
            Log.i("hashcode", "${file.name}:${file.hashCode()}")
        }

        holder.setName(file)
        holder.setSize(file)
        holder.setTime(file)
        holder.view.setOnClickListener{
            fileItemOnClickListener?.invoke(file)
        }
    }

}