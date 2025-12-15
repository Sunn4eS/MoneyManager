package com.app.moneymanager.ui.viewmodels

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.usecase.GetAllCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
): ViewModel() {
    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getAllCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            }
        }
    }
}

data class CategoriesUiState (
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)