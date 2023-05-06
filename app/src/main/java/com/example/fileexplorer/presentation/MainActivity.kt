package com.example.fileexplorer.presentation

import android.Manifest
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import com.example.fileexplorer.databinding.ActivityMainBinding
import com.example.fileexplorer.presentation.recycler.FilesListAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var fileAdapter: FilesListAdapter
    private lateinit var currentFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        runtimePermission()

        setOnClickListeners()


    }

    private fun setOnClickListeners() {
        binding.storageAll.setOnClickListener {
            binding.storageAll.cardElevation = 8f
            binding.storageChanged.cardElevation = 2f
        }
        binding.storageChanged.setOnClickListener {
            binding.storageAll.cardElevation = 2f
            binding.storageChanged.cardElevation = 8f
        }
        binding.backButton.setOnClickListener {
            currentFile.parentFile?.let { parent ->
                if(parent.absolutePath != "/")
                    moveToDirectory(parent)
                else {
                    binding.backButton.setColorFilter(Color.GRAY)
                    binding.backButton.isEnabled = false
                }
            }
        }
    }

    private fun runtimePermission() {
        Dexter.withContext(applicationContext).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                displayFiles()
            }

            override fun onPermissionRationaleShouldBeShown(
                requests: MutableList<PermissionRequest>?,
                permissionToken: PermissionToken?,
            ) {
                permissionToken?.continuePermissionRequest()
            }

        }).check()
    }

    private fun findFiles(file: File): ArrayList<File> {
        val result = ArrayList<File>()
        val files = file.listFiles()
        if (files != null) {
            result.addAll(files)
        }

        return result
    }

    private fun displayFiles() {
        val internalStorage = System.getenv("EXTERNAL_STORAGE")
        currentFile = File(internalStorage ?: throw Exception("no path name"))

        binding.pathTv.text = currentFile.absolutePath

        fileAdapter = FilesListAdapter()
        binding.rvFiles.adapter = fileAdapter
        fileAdapter.submitList(findFiles(currentFile))

        binding.rvFiles.setHasFixedSize(true)
        fileAdapter.fileItemOnClickListener = { file ->
            if (file.isDirectory) {
                moveToDirectory(file)
            }
        }
    }

    private fun moveToDirectory(file: File) {
        currentFile = file
        binding.pathTv.text = file.absolutePath
        binding.pathTv.isSelected = true
        fileAdapter.submitList(findFiles(file))
    }

}