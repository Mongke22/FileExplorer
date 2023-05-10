package com.example.fileexplorer.presentation

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
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

/*
* При любой сортировке список прокручивается к началу
* При клике на файл выполняется его открытие
* При клике на папку осуществляется переход в нее
* При длительном клике на файл появляется возможность поделиться им
* Кнопка назад возвращает к родителю, если такой имеется
* */
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    //Адаптер recyclerView
    private lateinit var fileAdapter: FilesListAdapter

    //Текущая отображаемая папка
    private lateinit var currentFile: File
    private lateinit var viewModel: MainViewModel

    //Флаг для отображения модифицированных данных
    private var showModifiedFiles = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupRecyclerView()

        runtimePermission()

        //Запуск загрузки изменных файлов
        viewModel.getModifiedFiles()

        setOnClickListeners()
        setupObserver()

    }

    private fun setupRecyclerView() {
        fileAdapter = FilesListAdapter()
        binding.rvFiles.adapter = fileAdapter
        binding.rvFiles.setHasFixedSize(true)

        //установка слушателя клика на элемент
        fileAdapter.fileItemOnClickListener = { file ->
            if (file.isDirectory) {
                switchBackButtonEnabled(true)
                moveToDirectory(file)
            } else {
                FileOpener.openFile(this, file)
            }
        }
        //установка слушателя длинного клика на элемент
        fileAdapter.fileItemOnLongClickListener = { file ->
            if (!file.isDirectory)
                shareFile(file)
        }
        setupOnDataChangeListener()
    }

    private fun setOnClickListeners() {
        //Обработка клика на кнопку отображения всех файлов
        binding.storageAll.setOnClickListener {
            binding.pgLoading.visibility = View.VISIBLE
            showAllFiles()
        }
        //Обработка клика на кнопку отображения измененных файлов
        binding.storageChanged.setOnClickListener {
            binding.pgLoading.visibility = View.VISIBLE
            showChangedFiles()
        }
        //Обработка клика на стрелку назад
        binding.backButton.setOnClickListener {
            currentFile.parentFile?.let { parent ->
                moveToDirectory(parent)
            }
        }
        //Обработка клика на смену направления сортировки
        binding.buttonSortDirection.setOnClickListener { view ->
            switchSortDirection(view)
        }
        //Обработка клика на фильтры
        binding.buttonSort.setOnClickListener {
            startFilterDialog()
        }
    }

    /*
    * меняет тень у кнопок для отображения выбранной секции
    * показывает файлы текущей папки
    * */
    private fun showAllFiles() {
        binding.storageAll.cardElevation = Constants.SELECTED_ELEVATION_FLOAT
        binding.storageChanged.cardElevation = Constants.DEFAULT_ELEVATION_FLOAT
        showModifiedFiles = false
        switchBackButtonEnabled(true)
        moveToDirectory(currentFile)
    }

    /*
    * меняет тень у кнопок для отображения выбранной секции
    * если список уже доступен - инициирует его тотбражение
    * очищается строка с путем сверху экрана
    * */
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

    /*
    * слушатели готовности списков для отображения
    * */
    private fun setupObserver() {
        viewModel.modifiedFiles.observe(this) { files ->
            if (showModifiedFiles) {
                viewModel.displayFiles(files ?: mutableListOf())
            }
        }
        viewModel.filesToShow.observe(this) { files ->
            binding.pgLoading.visibility = View.GONE
            if(files.isEmpty()){
                binding.tvNoData.visibility = View.VISIBLE
            }
            else{
                binding.tvNoData.visibility = View.GONE
            }
            fileAdapter.submitList(files)
        }
    }

    /*
    * Запрос с помощью библиотеки Dexter необходимых прав
    * при получении всего необходимого переход к папке
    * */
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

    /*
    * сохранение текущей папки
    * установка пути к ней
    * отображение вложенных файлов
    * */
    private fun moveToDirectory(file: File) {
        currentFile = file
        if (currentFile.absolutePath == Constants.ROOT_FILE.absolutePath) {
            switchBackButtonEnabled(false)
        }
        binding.pathTv.text = currentFile.absolutePath
        binding.pathTv.isSelected = true
        viewModel.displayFiles(currentFile.listFiles()?.toMutableList() ?: mutableListOf())
    }

    //Переключение доступности кнопки назад
    private fun switchBackButtonEnabled(enabled: Boolean) {
        with(binding) {
            if (!enabled) {
                backButton.setColorFilter(Color.GRAY)
                backButton.isEnabled = false
            } else {
                backButton.setColorFilter(resources.getColor(R.color.orange))
                backButton.isEnabled = true
            }
        }
    }

    //Функция чтобы делиться файлом в других приложениях
    private fun shareFile(file: File) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "*/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this,
            this.applicationContext.packageName + ".provider",
            file))
        startActivity(Intent.createChooser(shareIntent, "Поделиться " + file.name))
    }

    //Скрол в начало при обновлении данных
    private fun setupOnDataChangeListener() {
        fileAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding.rvFiles.scrollToPosition(0)
            }
        })
    }

    //Переключчение направления сортировки и иконки кнопки
    private fun switchSortDirection(view: View) {
        viewModel.switchSortDirection()
        if (viewModel.fromMaxToMin) {
            view.setBackgroundResource(R.drawable.sort_down)
        } else {
            view.setBackgroundResource(R.drawable.sort_up)
        }
    }

    //Показ диалога с фильтром
    private fun startFilterDialog() {
        val filterFragment = FilterDialog(viewModel.getFilter())
        filterFragment.onApplyFunc = {
            viewModel.setFilter(filterFragment.filterParameter)
        }
        filterFragment.show(supportFragmentManager, "dialog")
    }

    //Обработка нажатия на системную кнопку назад
    override fun onBackPressed() {
        if (binding.backButton.isEnabled) {
            binding.backButton.callOnClick()
        }
    }

}



