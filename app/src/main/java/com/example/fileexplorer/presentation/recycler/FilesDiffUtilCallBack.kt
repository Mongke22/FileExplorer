package com.example.fileexplorer.presentation.recycler

import androidx.recyclerview.widget.DiffUtil
import java.io.File

class FilesDiffUtilCallBack: DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.absolutePath == newItem.absolutePath
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem == newItem
    }
}