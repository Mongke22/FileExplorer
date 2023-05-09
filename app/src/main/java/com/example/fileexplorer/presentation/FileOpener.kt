package com.example.fileexplorer.presentation

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.fileexplorer.R
import java.io.File

class FileOpener {
    companion object {
        fun openFile(context: Context, file: File) {

            val uri = FileProvider.getUriForFile(context,
                context.applicationContext.packageName + ".provider",
                file)
            val intent = Intent(Intent.ACTION_VIEW)

            if (uri.toString().lowercase().contains(".jpg") || uri.toString().lowercase()
                    .contains(".jpeg") || uri.toString().lowercase().contains(".png")
            ) {
                intent.setDataAndType(uri, "image/*")
            } else if (uri.toString().contains(".mp3") ||
                uri.toString().contains(".wav") ||
                uri.toString().contains(".ogg")
            ) {
                intent.setDataAndType(uri, "audio/*")
            } else if (uri.toString().contains(".mp4")) {
                intent.setDataAndType(uri, "video/*")
            } else if (uri.toString().contains(".pdf")) {
                intent.setDataAndType(uri, "application/pdf")
            } else if (uri.toString().contains(".doc")) {
                intent.setDataAndType(uri, "application/msword")
            } else {
                intent.setDataAndType(uri, "*/*")
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }
    }
}