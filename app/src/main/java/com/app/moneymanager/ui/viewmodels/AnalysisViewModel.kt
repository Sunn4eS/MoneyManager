package com.app.moneymanager.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.moneymanager.domain.model.Category
import com.app.moneymanager.domain.model.Transaction
import com.app.moneymanager.domain.model.TransactionType
import com.app.moneymanager.domain.usecase.GetAllCategoriesUseCase
import com.app.moneymanager.domain.usecase.GetTransactionByUseCase
import com.app.moneymanager.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
   private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
   private val getAllTransactionByUseCase: GetTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisUiState())
    val uiState: StateFlow<AnalysisUiState> = _uiState

    private val categoryColorMap: Map<Long, Color> = mapOf(
        1L to Color(0xFFE57373), // Red
        2L to Color(0xFFFFB74D), // Orange
        3L to Color(0xFF81C784), // Green
        4L to Color(0xFF64B5F6), // Blue
        5L to Color(0xFFBA68C8), // Purple
        6L to Color(0xFF4DB6AC), // Cyan
        7L to Color(0xFFF06292), // Pink
        8L to Color(0xFF90A4AE), // Grey
    )

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getAllCategoriesUseCase(),
                getAllTransactionByUseCase()
            ) { transactions, categories ->
                val categoryMap = categories.associateBy {it.id}

//                calculateCharData(
//                    allTransactions =
//                )
            }.collect {  }
        }
    }

    private fun calculateChartData(
        allTransactions: List<Transaction>,
        categoryMap: Map<Long, Category>,
        currentState: AnalysisUiState
    ) {
        val filteredTransactions = allTransactions.filter { transaction ->
            val transactionDate = transaction.date.toLocalDate()
            val isCorrectType = transaction.type == currentState.selectedType
            val isWithinPeriod = (transactionDate.isEqual((currentState.startDate)) || transactionDate.isAfter(currentState.startDate) &&
                    transactionDate.isEqual(currentState.endDate) || transactionDate.isBefore(currentState.endDate))
            isCorrectType && isWithinPeriod
        }
        val groupedAmounts = filteredTransactions
            .groupBy { it.category.id }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

        val totalAmount = groupedAmounts.values.sum()

        val chartSlices = groupedAmounts.map { (categoryId, amount) ->
            val category = categoryMap[categoryId] ?: Category(id = categoryId, name = "", isExpense = true, colorHex = "ff")
            val percentage =
                if (totalAmount > 0)
                    ((amount/totalAmount) * 100).toFloat()
                else
                    0F

            val colorIndex = (categoryId.toLong() % categoryColorMap.size.toLong()).let {
                if (it == 0L) categoryColorMap.size.toLong() else it
            }

            val color = categoryColorMap[colorIndex] ?: Color.LightGray

            ChartSlice(
                category = category,
                amount = amount,
                percentage = percentage,
                color = color
            )
        }.sortedByDescending { it.amount }

        _uiState.update {
            it.copy(
                chartSlice = chartSlices,
                totalAmount = totalAmount,
                isLoading = false
            )
        }
    }

    fun setTransactionType(type: TransactionType) {
        _uiState.update { it.copy(selectedType = type) }
    }

    fun navigatePeriod(direction: Int) {
        val currentStart = _uiState.value.startDate
        val period = _uiState.value.selectedPeriod

        val newStart: LocalDate
        val newEnd: LocalDate

        when (period) {
            AnalysisPeriod.DAY -> {
                newStart = currentStart.plusDays(direction.toLong())
                newEnd = newStart
            }
            AnalysisPeriod.WEEK -> {
                newStart = currentStart.plusWeeks(direction.toLong())
                newEnd = newStart.plusDays(6)
            }
            AnalysisPeriod.MONTH -> {
                newStart = currentStart.plusMonths(direction.toLong()).withDayOfMonth(1)
                newEnd = newStart.withDayOfMonth(newStart.lengthOfMonth())
            }

            AnalysisPeriod.YEAR -> {
                newStart = currentStart.plusYears(direction.toLong()).withDayOfYear(1)
                newEnd = newStart.withDayOfYear(newStart.lengthOfYear())
            }
            AnalysisPeriod.CUSTOM -> return
        }

        val newLabel = when(period) {
            AnalysisPeriod.DAY -> newStart.formatDate()
            AnalysisPeriod.WEEK -> "${newStart.formatDate()} - ${newEnd.formatDate()}"
            AnalysisPeriod.MONTH -> newStart.formatMonthYear()
            AnalysisPeriod.YEAR -> newStart.year.toString()
            AnalysisPeriod.CUSTOM -> _uiState.value.currentPeriodLabel
        }

        _uiState.update { it.copy(startDate = newStart, endDate = newEnd, currentPeriodLabel = newLabel) }


    }

    fun setPeriod(period: AnalysisPeriod) {
        val now = LocalDate.now()
        val (newStart, newEnd, newLabel) = when (period) {
            AnalysisPeriod.DAY -> {
                Triple(now,now, "Текущий день")
            }
            AnalysisPeriod.WEEK -> {
                val weekFields = WeekFields.of(Locale.getDefault())
                val startOfWeek = now.with(weekFields.dayOfWeek(), 1)
                Triple(startOfWeek, now, "Текущая неделя")
            }
            AnalysisPeriod.MONTH -> Triple(now.withDayOfMonth(1), now, "Текущий месяц")
            AnalysisPeriod.YEAR -> Triple(now.withDayOfYear(1), now, "Текущий год")
            AnalysisPeriod.CUSTOM -> Triple(_uiState.value.startDate, _uiState.value.endDate, "Выбранный период")

        }
        _uiState.update {
            it.copy(
                selectedPeriod = period,
                startDate = newStart,
                endDate = newEnd,
                currentPeriodLabel = newLabel
            )
        }

    }

    private fun Date.toLocalDate():
            LocalDate = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    private fun LocalDate.formatDate():
            String = this.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM"))
    private fun LocalDate.formatMonthYear():
            String = this.format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy",
        Locale("ru")
    ))


}

data class ChartSlice(
    val category: Category,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

enum class AnalysisPeriod {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    CUSTOM
}

data class AnalysisUiState(
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedPeriod: AnalysisPeriod = AnalysisPeriod.MONTH,
    val chartSlice: List<ChartSlice> = emptyList(),
    val totalAmount: Double = 0.0,
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val currentPeriodLabel: String = "Текущий месяц",
    val isLoading: Boolean = true,
    val error: String? = null


)