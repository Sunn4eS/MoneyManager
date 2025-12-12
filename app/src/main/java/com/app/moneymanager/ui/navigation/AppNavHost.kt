package com.app.moneymanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.app.moneymanager.ui.screens.TransactionScreen
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoutes.TRANSACTION,
        modifier = modifier
    ) {
        composable(ScreenRoutes.TRANSACTION) {
            TransactionScreen(
                onNavigateToEdit = {transactionId ->
                    navController.navigate("add_edit_transaction/$transactionId")
                }
            )
        }
        composable(ScreenRoutes.ADD_EDIT_TRANSACTION) { backStackEntry ->
            // TODO: Создать AddEditTransactionScreen
            TextPlaceholder(
                title = "Добавление/Редактирование",
                description = "ID: ${backStackEntry.arguments?.getString("transactionId") ?: "Новая"}"
            )
        }
        composable(ScreenRoutes.ANALYSIS) {
            // TODO: Создать AnalysisScreen
            TextPlaceholder(
                title = "Аналитика",
                description = "Экран графиков и отчетов"
            )
        }
    }
}

@Composable
private fun TextPlaceholder(title: String, description: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "$title\n$description",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}