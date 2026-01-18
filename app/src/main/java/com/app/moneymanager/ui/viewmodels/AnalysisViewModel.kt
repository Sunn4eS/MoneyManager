package com.app.moneymanager.ui.viewmodels

import android.R
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

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getAllTransactionByUseCase(),
                getAllCategoriesUseCase(),
                _uiState
            ) { transactions, categories, currentState ->
                val categoryMap = categories.associateBy { it.id }
                calculateChartData(transactions, categoryMap, currentState)
            }.collect { updatedSlices ->
                _uiState.update { it.copy(chartSlice = updatedSlices.first, totalAmount = updatedSlices.second, isLoading = false) }
            }
            setPeriod(AnalysisPeriod.MONTH)
        }
    }



    private fun calculateChartData(
        allTransactions: List<Transaction>,
        categoryMap: Map<Long, Category>,
        currentState: AnalysisUiState
    ): Pair<List<ChartSlice>, Double> {
        val filtered = allTransactions.filter { t ->
            val d = t.date.toLocalDate()
            t.type == currentState.selectedType && !d.isBefore(currentState.startDate) && !d.isAfter(currentState.endDate)
        }

        val grouped = filtered.groupBy { it.category.id }.mapValues { it.value.sumOf { trans -> trans.amount } }
        val total = grouped.values.sum()

        val slices = grouped.map { (catId, amount) ->
            val cat = categoryMap[catId] ?: Category(id = catId, name = "Прочее", isExpense = true, colorHex = "")
            val percent = if (total > 0) (amount / total).toFloat() else 0f
            val colorIdx = (catId % 8).let { if (it == 0L) 8L else it }
            ChartSlice(cat, amount, percent * 100f, categoryColorMap[colorIdx] ?: Color.Gray)
        }.sortedByDescending { it.amount }

        return Pair(slices, total)
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