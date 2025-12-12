package com.app.moneymanager.di

import com.app.moneymanager.data.repository.CategoryRepositoryImpl
import com.app.moneymanager.data.repository.TransactionRepositoryImpl
import com.app.moneymanager.domain.repository.CategoryRepository
import com.app.moneymanager.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Связываем интерфейс TransactionRepository с его реализацией TransactionRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository

    /**
     * Связываем интерфейс CategoryRepository с его реализацией CategoryRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
}