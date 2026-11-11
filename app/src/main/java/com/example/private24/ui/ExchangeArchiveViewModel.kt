package com.example.private24.ui

import androidx.lifecycle.ViewModel
import com.example.private24.data.model.ExchangeRateItem
import com.example.private24.data.remote.PrivatArchiveNetworkModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed interface ArchiveUiState {
    object Idle : ArchiveUiState
    object Loading : ArchiveUiState
    data class Success(val date: String, val rates: List<ExchangeRateItem>) : ArchiveUiState
    data class Error(val message: String) : ArchiveUiState
}

class ExchangeArchiveViewModel : ViewModel() {
    private val networkModule = PrivatArchiveNetworkModule()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(dateFormat.format(Calendar.getInstance().time))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _uiState = MutableStateFlow<ArchiveUiState>(ArchiveUiState.Idle)
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    private var currentCall: Call<com.example.private24.data.model.ExchangeArchiveResponse?>? = null

    fun updateDate(newDate: String) {
        _selectedDate.value = newDate
    }

    fun fetchRatesForSelectedDate() {
        val dateStr = _selectedDate.value
        if (!isDateValid(dateStr)) {
            _uiState.value = ArchiveUiState.Error("Введено неіснуючу або некоректну дату (формат дд.мм.рррр)")
            return
        }

        currentCall?.cancel()
        _uiState.value = ArchiveUiState.Loading

        val call = networkModule.service.getExchangeRates(dateStr)
        currentCall = call

        if (call == null) {
            _uiState.value = ArchiveUiState.Error("Не вдалося сформувати запит.")
            return
        }

        call.enqueue(object : Callback<com.example.private24.data.model.ExchangeArchiveResponse?> {
            override fun onResponse(
                call: Call<com.example.private24.data.model.ExchangeArchiveResponse?>,
                response: Response<com.example.private24.data.model.ExchangeArchiveResponse?>
            ) {
                if (call !== currentCall || call.isCanceled) return

                val body = response.body()
                _uiState.value = when {
                    !response.isSuccessful -> ArchiveUiState.Error("Помилка сервера: HTTP ${response.code()}")
                    body == null || body.exchangeRate == null -> ArchiveUiState.Error("За обрану дату дані відсутні.")
                    else -> {
                        val validRates = body.exchangeRate.filter { !it.currency.isNullOrBlank() }
                        ArchiveUiState.Success(date = body.date ?: dateStr, rates = validRates)
                    }
                }
            }

            override fun onFailure(call: Call<com.example.private24.data.model.ExchangeArchiveResponse?>, t: Throwable) {
                if (call !== currentCall || call.isCanceled) return // Виправлено currentCard на currentCall
                _uiState.value = ArchiveUiState.Error("Помилка з'єднання. Перевірте інтернет.")
            }
        })
    }

    private fun isDateValid(dateStr: String): Boolean {
        return try {
            dateFormat.isLenient = false
            dateFormat.parse(dateStr) != null
        } catch (e: Exception) {
            false
        }
    }

    override fun onCleared() {
        currentCall?.cancel()
        super.onCleared()
    }

    fun clearAll() {
        currentCall?.cancel()
        _uiState.value = ArchiveUiState.Idle
        _selectedDate.value = dateFormat.format(Calendar.getInstance().time)
    }
}