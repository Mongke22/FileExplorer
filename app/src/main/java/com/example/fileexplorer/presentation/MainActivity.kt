package com.example.fileexplorer.presentation

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fileexplorer.Constants
import com.example.fileexplorer.R
import com.example.fileexplorer.databinding.ActivityMainBinding
import com.example.fileexplorer.presentation.recycler.FilesListAdapter
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File


class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var fileAdapter: FilesListAdapter
    private lateinit var currentFile: File
    private lateinit var viewModel: MainViewModel

    private var showModifiedFiles = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()

        runtimePermission()

        viewModel.getModifiedFiles()

        setOnClickListeners()
        setupObserver()


    }

    private fun setupRecyclerView() {
        fileAdapter = FilesListAdapter()
        binding.rvFiles.adapter = fileAdapter
        binding.rvFiles.setHasFixedSize(true)
        fileAdapter.fileItemOnClickListener = { file ->
            if (file.isDirectory) {
                switchBackButtonEnabled(true)
                moveToDirectory(file)
            }
        }
        fileAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.rvFiles.scrollToPosition(0)
            }
        })
    }

    private fun setOnClickListeners() {
        binding.storageAll.setOnClickListener {
            showAllFiles()
        }
        binding.storageChanged.setOnClickListener {
            showChangedFiles()
        }
        binding.backButton.setOnClickListener {
            currentFile.parentFile?.let { parent ->
                moveToDirectory(parent)
            }
        }
        binding.buttonSortDirection.setOnClickListener { view ->
            viewModel.switchSortDirection()
            if (viewModel.fromMaxToMin) {
                view.setBackgroundResource(R.drawable.sort_down)
            } else {
                view.setBackgroundResource(R.drawable.sort_up)
            }
        }
        binding.buttonSort.setOnClickListener {
            val filterFragment = FilterDialog(viewModel.getFilter())
            filterFragment.onApplyFunc = {
                viewModel.setFilter(filterFragment.filterParameter)
            }
            filterFragment.show(supportFragmentManager, "dialog")
        }
    }

    private fun showAllFiles() {
        binding.storageAll.cardElevation = Constants.SELECTED_ELEVATION_FLOAT
        binding.storageChanged.cardElevation = Constants.DEFAULT_ELEVATION_FLOAT
        showModifiedFiles = false
        switchBackButtonEnabled(true)
        moveToDirectory(currentFile)
    }

    private fun showChangedFiles() {
        binding.storageAll.cardElevation = Constants.DEFAULT_ELEVATION_FLOAT
        binding.storageChanged.cardElevation = Constants.SELECTED_ELEVATION_FLOAT
        showModifiedFiles = true
        if (viewModel.modifiedFiles.value != null) {
            viewModel.displayFiles(viewModel.modifiedFiles.value ?: ArrayList())
        }
        switchBackButtonEnabled(false)
        binding.pathTv.text = Constants.EMPTY_STRING
    }

    private fun setupObserver() {
        viewModel.modifiedFiles.observe(this) { files ->
            if (showModifiedFiles) {
                viewModel.displayFiles(files ?: ArrayList())
            }
        }
        viewModel.filesToShow.observe(this) { files ->
            fileAdapter.submitList(files)
        }
    }

    private fun runtimePermission() {
        Dexter.withContext(applicationContext).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                moveToDirectory(Constants.ROOT_FILE)
            }

            override fun onPermissionRationaleShouldBeShown(
                requests: MutableList<PermissionRequest>?,
                permissionToken: PermissionToken?,
            ) {
                permissionToken?.continuePermissionRequest()
            }

        }).check()
    }


    private fun moveToDirectory(file: File) {
        currentFile = file
        if (currentFile.absolutePath == Constants.ROOT_FILE.absolutePath) {
            switchBackButtonEnabled(false)
        }
        binding.pathTv.text = currentFile.absolutePath
        binding.pathTv.isSelected = true
        viewModel.displayFiles(viewModel.findFiles(currentFile))
    }

    private fun switchBackButtonEnabled(enabled: Boolean) {
        with(binding) {
            if (!enabled) {
                backButton.setColorFilter(Color.GRAY)
                backButton.isEnabled = false
            } else {
                backButton.setColorFilter(Color.YELLOW)
                backButton.isEnabled = true
            }
        }
    }

}



