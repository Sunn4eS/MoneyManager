package com.app.moneymanager.domain.usecase

import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getAllCategories()
    }
}

class DeleteCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: Long) {
        repository.deleteCategory(categoryId)
    }
}

class AddCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(category: Category) {
        if (category.name == "") {
            throw IllegalArgumentException("Category name can't be empty")
        }
        repository.saveCategory(category)
    }
}