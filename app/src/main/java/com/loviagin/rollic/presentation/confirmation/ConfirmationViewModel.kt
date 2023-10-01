package com.loviagin.rollic.presentation.confirmation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.loviagin.rollic.data.dto.Payment
import com.loviagin.rollic.data.dto.Response
import com.loviagin.rollic.domain.usecase.GetPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class State(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val payment: Payment? = null
)

@HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val getPaymentUseCase: GetPaymentUseCase
): ViewModel() {

    val state = MutableLiveData(State())

    fun checkPayment(id: String) {
        viewModelScope.launch {

            state.postValue(State(isLoading = true))

            getPaymentUseCase(id)
                .catch {
                    state.postValue(
                        State(
                            isError = true,
                            errorMessage = it.message
                        )
                    )
                }
                .collect { response: Response ->
                    state.postValue(
                        State(
                            isSuccess = true,
                            payment = response.payment
                        )
                    )
                }
        }
    }
}