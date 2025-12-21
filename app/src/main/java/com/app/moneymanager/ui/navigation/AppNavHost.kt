package com.app.moneymanager.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.app.moneymanager.ui.screens.TransactionScreen
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.app.moneymanager.ui.screens.AddEditCategoryScreen
import com.app.moneymanager.ui.screens.AddEditTransactionScreen
import com.app.moneymanager.ui.screens.AnalysisScreen
import com.app.moneymanager.ui.screens.CategoriesScreen


sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Transactions: BottomNavItem(ScreenRoutes.TRANSACTION, Icons.Default.Home, "Транзакции")
    object Categories: BottomNavItem(ScreenRoutes.CATEGORIES, Icons.Default.Refresh, "Категории")
    object Analysis: BottomNavItem(ScreenRoutes.ANALYSIS, Icons.Default.AddCircle, "Аналитика")
}
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Transactions.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Transactions.route) {
            TransactionScreen(
                onNavigateToEdit = {transactionId ->
                    navController.navigate("add_edit_transaction/$transactionId")
                }
            )
        }
        composable(
            route = ScreenRoutes.ADD_EDIT_TRANSACTION,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) {
            AddEditTransactionScreen(
                onBack = { navController.popBackStack() }, // Вернуться на предыдущий экран
                onActionSuccess = {
                    // Успешное сохранение: вернуться на главный экран
                    navController.popBackStack()
                }
            )
        }

        composable(BottomNavItem.Categories.route) {
            // TODO: Создать CategoryScreen
            CategoriesScreen(
                onNavigateToAddEdit = { categoryId ->
                    navController.navigate(
                        ScreenRoutes.ADD_EDIT_CATEGORY.replace(
                            "{categoryId}",
                            categoryId.toString()
                        )
                    )
                }
            )
        }

        composable(
            route = ScreenRoutes.ADD_EDIT_CATEGORY,
            arguments = listOf(navArgument("categoryId") {type = NavType.LongType })
        ) {
            AddEditCategoryScreen(
                onBack = { navController.popBackStack()},
                onActionSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(BottomNavItem.Analysis.route) {
            // TODO: Создать AnalysisScreen
            AnalysisScreen()
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

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Transactions,
        BottomNavItem.Categories,
        BottomNavItem.Analysis
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isBottomBarVisible = items.any { it.route == currentRoute }
    if (isBottomBarVisible) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            items.forEach {
                item ->
                val isSelected = currentRoute == item.route
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label)},
                    label = {
                        Text(
                            item.label
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    }
                )
            }
        }
    }

}