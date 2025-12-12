package com.app.moneymanager.di

import android.content.Context
import androidx.room.Room
import com.app.moneymanager.data.local.AppDatabase
import com.app.moneymanager.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Предоставляет синглтон AppDatabase.
     * Hilt автоматически внедрит Context с помощью аннотации @ApplicationContext.
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "money_manager_db" // Имя файла базы данных
        )
            // В продакшене это нужно избегать, но для учебных целей полезно:
            // .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Предоставляет синглтон TransactionDao из созданной базы данных.
     * Hilt автоматически внедрит AppDatabase.
     */
    @Provides
    @Singleton
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }
}