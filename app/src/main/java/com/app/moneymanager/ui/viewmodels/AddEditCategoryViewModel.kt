package com.app.moneymanager.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.repository.CategoryRepository
import com.app.moneymanager.domain.usecase.AddCategoryUseCase
import com.app.moneymanager.domain.usecase.DeleteCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val categoryRepository: CategoryRepository,
): ViewModel() {
    private val categoryId: Long = savedStateHandle.get<Long>("categoryId") ?: 0L
    private val _uiState = MutableStateFlow(AddEditCategoryUiState())
    val uiState: StateFlow<AddEditCategoryUiState> = _uiState

    private val _actionSuccess = MutableStateFlow<CategoryAction?>(null)
    val actionSuccess: StateFlow<CategoryAction?> = _actionSuccess

    init {
        if (categoryId != 0L) {
            _uiState.update { it.copy(isEditing = true) }
            loadCategory(categoryId)
        }
    }

    private fun loadCategory(id: Long) {
        viewModelScope.launch {
            try {
                val category = categoryRepository.getCategoryById(id).first()
                if (category != null) {
                    _uiState.update {
                        it.copy(nameInput = category.name)
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            error = "Категория не найдена."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка загрузки: ${e.localizedMessage}") }
            }
        }
    }

    fun onNameChange(input: String) {
        _uiState.update {
            it.copy(
                nameInput = input,
                nameError = false
            )
        }
    }

    fun saveCategory() {
        val name = _uiState.value.nameInput.trim()

        if (name.isBlank()) {
            _uiState.update { it.copy(nameError = true, error = "Название не может быть пустым") }
            return
        }

        val categoryToSave = Category(
            id = categoryId,
            name = name,
            isExpense = true,
            colorHex = "ff"
        )

        viewModelScope.launch {
            try {
                saveCategoryUseCase(categoryToSave)
                _actionSuccess.value =
                    if (categoryId == 0L)
                        CategoryAction.ADDED
                    else
                        CategoryAction.UPDATED
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка сохранения: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteCategory() {
        if (categoryId == 0L) return

        viewModelScope.launch {
            try {
                deleteCategoryUseCase(categoryId)
                _actionSuccess.value = CategoryAction.DELETED
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Ошибка удаления: ${e.localizedMessage}") }
            }
        }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

}

data class AddEditCategoryUiState(
    val nameInput: String = "",
    val isEditing: Boolean = false,
    val nameError: Boolean = false,
    val error: String? = null
)

enum class CategoryAction {
    ADDED, UPDATED, DELETED
}