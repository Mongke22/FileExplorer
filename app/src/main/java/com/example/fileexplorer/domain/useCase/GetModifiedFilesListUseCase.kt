package com.example.fileexplorer.domain.useCase

import com.example.fileexplorer.domain.FileRepository

class GetModifiedFilesListUseCase(
    private val repository: FileRepository
) {
    suspend operator fun invoke() = repository.getModifiedFiles()
}