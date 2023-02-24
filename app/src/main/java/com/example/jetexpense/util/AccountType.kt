package com.example.jetexpense.util

import androidx.compose.ui.graphics.Color
import com.example.jetexpense.R
import com.example.jetexpense.ui.theme.healthBg
import com.example.jetexpense.ui.theme.leisureBg
import com.example.jetexpense.ui.theme.subBg

enum class AccountType(val title: String, val iconRes: Int, val color: Color) {
    CASH("Cash", R.drawable.cash, leisureBg),
    BANK("Bank", R.drawable.bank, subBg),
    CARD("Card", R.drawable.credit_card, healthBg)
}
