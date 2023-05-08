package com.example.fileexplorer.presentation

import android.app.Dialog
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.fileexplorer.Filter
import com.example.fileexplorer.R
import com.example.fileexplorer.databinding.FilterDialogBinding

//В конструктор передается состояние фильтров
class FilterDialog(
    var filterParameter: Filter
) : DialogFragment() {

    //Функция, которая будет вызвана по закрытию диалога
    //Задается снаружи, чтобы была возможность использовать любую логику с viewModel
    var onApplyFunc: (() -> Unit)? = null

    private var _binding: FilterDialogBinding? = null
    val binding
        get() = _binding ?: throw RuntimeException("FragmentBinding is null")

    //Восстановление состояния фильтров
    private fun initFilter() {
        when(filterParameter){
            Filter.FileType -> {
                binding.rbOptionFileType.isChecked = true
            }
            Filter.DateCreation -> {
                binding.rbOptionDate.isChecked = true
            }
            Filter.FileSize -> {
                binding.rbOptionSize.isChecked = true
            }
            Filter.FileName -> {
                binding.rbOptionName.isChecked = true
            }
            else -> {
                throw Exception()
            }
        }
    }

    private fun filterLogic() {
        setupFilterParameterChangeListener()
    }

    private fun setupFilterParameterChangeListener() {
        binding.rgFilterParameters.setOnCheckedChangeListener { _, buttonId ->
            filterParameter =
                when (binding.rgFilterParameters.findViewById<RadioButton>(buttonId).text.toString()) {
                    requireContext().resources.getString(R.string.option_date) -> {
                        Filter.DateCreation
                    }
                    requireContext().resources.getString(R.string.option_name) -> {
                        Filter.FileName
                    }
                    requireContext().resources.getString(R.string.option_size) -> {
                        Filter.FileSize
                    }
                    requireContext().resources.getString(R.string.option_type) -> {
                        Filter.FileType
                    }
                    else -> {
                        throw Exception()
                    }
                }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            _binding = FilterDialogBinding.inflate(layoutInflater)

            initFilter()
            filterLogic()

            builder
                .setView(binding.root)
                .setPositiveButton("Применить"
                ) { dialog, id ->
                    onApplyFunc?.invoke()
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

}