package com.example.jetexpense.presentation.common

sealed class UiEvents {
    data class Alert(val info: String) : UiEvents()
    data class NoAlert(val info: String = String()) : UiEvents()
}
