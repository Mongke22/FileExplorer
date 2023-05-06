package com.example.fileexplorer.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.fileexplorer.R
import java.io.File

class FilesListAdapter : ListAdapter<File, FileViewHolder>(FilesDiffUtilCallBack()) {

    var fileItemOnClickListener: ((File) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_container, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = getItem(position)
        holder.setName(file)
        holder.setSize(file)
        holder.view.setOnClickListener{
            fileItemOnClickListener?.invoke(file)
        }
    }

}