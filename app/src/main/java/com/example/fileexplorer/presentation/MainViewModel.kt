package com.example.fileexplorer.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fileexplorer.Filter
import com.example.fileexplorer.data.FileRepositoryImpl
import com.example.fileexplorer.domain.useCase.GetModifiedFilesListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.time.ZoneId
import kotlin.io.path.readAttributes

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FileRepositoryImpl(application)
    private val getModifiedFiles = GetModifiedFilesListUseCase(repository)

    private val _modifiedFiles = MutableLiveData<ArrayList<File>>(null)
    val modifiedFiles: LiveData<ArrayList<File>?>
        get() = _modifiedFiles

    private val _filesToShow = MutableLiveData<ArrayList<File>>()
    val filesToShow: LiveData<ArrayList<File>>
        get() = _filesToShow

    var fromMaxToMin = false
    private var filter = Filter.FileName
    private var comparator =
        Comparator { file1: File, file2: File -> file1.name.compareTo(file2.name) }

    fun getModifiedFiles() {
        _modifiedFiles
        viewModelScope.launch(Dispatchers.Default) {
            _modifiedFiles.postValue(getModifiedFiles.invoke())
        }
    }

    fun setFilter(newFilter: Filter){
        filter = newFilter
        comparator = when (filter) {
            Filter.FileName -> {
                Comparator { file1: File, file2: File -> file1.name.compareTo(file2.name) }
            }
            Filter.FileType -> {
                Comparator { file1: File, file2: File -> file1.extension.compareTo(file2.extension) }
            }
            Filter.FileSize -> {
                Comparator { file1: File, file2: File -> (file1.length() - file2.length()).toInt() }
            }
            Filter.DateCreation -> {
                Comparator { file1: File, file2: File ->
                    val attr1 =
                        Paths.get(file1.absolutePath).readAttributes<BasicFileAttributes>()
                    val zonedTime1 = attr1.creationTime().toInstant().atZone(ZoneId.systemDefault())

                    val attr2 =
                        Paths.get(file2.absolutePath).readAttributes<BasicFileAttributes>()
                    val zonedTime2 = attr2.creationTime().toInstant().atZone(ZoneId.systemDefault())

                    (zonedTime1.toInstant().toEpochMilli() - zonedTime2.toInstant()
                        .toEpochMilli()).toInt()
                }
            }
        }
        displayFiles(ArrayList(filesToShow.value ?: ArrayList()))
    }

    fun getFilter(): Filter{
        return filter
    }


    fun findFiles(file: File): ArrayList<File> {
        val result = ArrayList<File>()
        val files = file.listFiles()
        if (files != null) {
            result.addAll(files)
        }
        return result
    }

    fun switchSortDirection() {
        fromMaxToMin = !fromMaxToMin
        displayFiles(ArrayList(filesToShow.value ?: ArrayList()))
    }

    fun displayFiles(files: ArrayList<File>) {
        Log.i("files", files.toString())
        if (fromMaxToMin)
            files.sortWith(comparator.reversed())
        else files.sortWith(comparator)
        _filesToShow.value = files
    }

}