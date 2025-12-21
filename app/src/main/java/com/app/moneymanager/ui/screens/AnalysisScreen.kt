package com.app.moneymanager.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.app.moneymanager.domain.model.TransactionType
import com.app.moneymanager.ui.theme.ExpenseRed
import com.app.moneymanager.ui.theme.IncomeGreen
import com.app.moneymanager.ui.viewmodels.AnalysisPeriod
import com.app.moneymanager.ui.viewmodels.AnalysisViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AnalysisScreen (
    viewModel: AnalysisViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text("Аналитика")
            })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                //.fillMaxWidth()
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TypeToggle(
                selectionType = state.selectedType,
                onTypeSelect = viewModel::setTransactionType
            )

            Spacer(Modifier.height(40.dp))

            PeriodControls(
                currentPeriodLabel = state.currentPeriodLabel,
                selectedPeriod = state.selectedPeriod,
                onPeriodSelect = viewModel::setPeriod,
                onNavigatePeriod = viewModel::navigatePeriod
            )



        }

    }
}

@Composable
private fun TypeToggle(
    selectionType: TransactionType,
    onTypeSelect: (TransactionType) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TypeToggleButton(
            label = "Расходы",
            type = TransactionType.EXPENSE,
            isSelected = selectionType == TransactionType.EXPENSE,
            color = ExpenseRed,
            onClick = onTypeSelect,
            modifier = Modifier.weight(1f)
        )
        TypeToggleButton(
            label = "Доходы",
            type = TransactionType.INCOME,
            isSelected = selectionType == TransactionType.INCOME,
            color = IncomeGreen,
            onClick = onTypeSelect,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TypeToggleButton(
    label: String,
    type: TransactionType,
    isSelected: Boolean,
    color: Color,
    onClick: (TransactionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(type)},
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(6.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 4.dp else 0.dp)
    ) {
        Text(label, fontSize = 14.sp)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodControls(
    currentPeriodLabel: String,
    selectedPeriod: AnalysisPeriod,
    onPeriodSelect: (AnalysisPeriod) -> Unit,
    onNavigatePeriod: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    //var selectedPeriod by remember { mutableStateOf(AnalysisPeriod.DAY) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { onNavigatePeriod(-1) },
            enabled = selectedPeriod != AnalysisPeriod.CUSTOM
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Предыдущий период"
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(200.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }

            ) {
                TextField(
                    value = currentPeriodLabel, // ← вычисляем прямо здесь
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor()

                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },

                ) {
                    AnalysisPeriod.entries.filter { it != AnalysisPeriod.CUSTOM }.forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period.toWordLabel()) },
                            onClick = {
                                onPeriodSelect(period)
                                //selectedPeriod = period
                                //currentPeriodLabel = period.toWordLabel()   // обновляем текст
                                expanded = false
                            },
                            trailingIcon = {
                                if (period == selectedPeriod) {
                                    Icon(Icons.Default.Done, contentDescription = "Выбран")
                                }
                            }
                        )
                    }

                }
            }
        }

        IconButton(
            onClick = { onNavigatePeriod(1) },
            enabled = selectedPeriod != AnalysisPeriod.CUSTOM
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Следующий период"
            )
        }
    }
}
    fun AnalysisPeriod.toWordLabel(): String = when (this) {
        AnalysisPeriod.DAY -> "День"
        AnalysisPeriod.WEEK -> "Неделя"
        AnalysisPeriod.MONTH -> "Месяц"
        AnalysisPeriod.YEAR -> "Год"
        AnalysisPeriod.CUSTOM -> "Свой"
    }

    fun AnalysisPeriod.toDateRangeLabel(baseDate: LocalDate): String = when (this) {
        AnalysisPeriod.DAY -> baseDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        AnalysisPeriod.WEEK -> {
            val startOfWeek = baseDate.with(DayOfWeek.MONDAY)
            val endOfWeek = baseDate.with(DayOfWeek.SUNDAY)
            "${startOfWeek.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        }
        AnalysisPeriod.MONTH -> {
            val startOfMonth = baseDate.withDayOfMonth(1)
            val endOfMonth = baseDate.withDayOfMonth(baseDate.lengthOfMonth())
            "${startOfMonth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${endOfMonth.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        }
        AnalysisPeriod.YEAR -> {
            val startOfYear = baseDate.withDayOfYear(1)
            val endOfYear = baseDate.withDayOfYear(baseDate.lengthOfYear())
            "${startOfYear.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${endOfYear.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        }
        AnalysisPeriod.CUSTOM -> "Свой период"
    }


@Preview
@Composable
fun Check () {
    AnalysisScreen()
}