package com.app.moneymanager.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import com.app.moneymanager.ui.viewmodels.TransactionViewModel


@Composable
fun TransactionScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    onNavigateToEdit: (Long) -> Unit
) {
    val state = viewModel.uiState.collectAsState().value
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            Text("Загрузка данных...")
        } else {
            // Здесь будет реальный UI со списком транзакций и балансом
            Text(
                "Баланс: ${state.currentBalance}\n" +
                        "Транзакций загружено: ${state.transactions.size}"
            )
        }
    }
    FloatingActionButton(
        onClick = {onNavigateToEdit(0L)},
        modifier = Modifier
            .padding(end = 16.dp, bottom = 16.dp)
    ) { }
}
