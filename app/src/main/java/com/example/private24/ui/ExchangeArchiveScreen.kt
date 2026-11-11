package com.example.private24.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExchangeArchiveScreen(
    modifier: Modifier = Modifier,
    viewModel: ExchangeArchiveViewModel = viewModel()
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = uiState is ArchiveUiState.Loading

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Архів курсів ПриватБанку",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = selectedDate,
            onValueChange = { viewModel.updateDate(it) },
            label = { Text("Дата (дд.мм.рррр)") },
            singleLine = true,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.fetchRatesForSelectedDate() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Завантаження..." else "Показати курси")
        }

        OutlinedButton(
            onClick = { viewModel.clearAll() },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("скинути та очистити")
        }

        Spacer(modifier = Modifier.height(8.dp))

        when (val state = uiState) {
            ArchiveUiState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Оберіть дату та натисніть «Показати курси»")
                }
            }
            ArchiveUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ArchiveUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { viewModel.fetchRatesForSelectedDate() }) {
                        Text("Спробувати знову")
                    }
                }
            }
            is ArchiveUiState.Success -> {
                Text(
                    text = "Курси станом на: ${state.date}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (state.rates.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("За цю дату курси валют відсутні.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.rates) { rate ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "${rate.currency} / ${rate.baseCurrency ?: "UAH"}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Банк купує: ${rate.purchaseRate ?: "—"}")
                                    Text("Банк продає: ${rate.saleRate ?: "—"}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}