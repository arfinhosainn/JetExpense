package com.example.jetexpense.presentation.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetexpense.common.Constants
import com.example.jetexpense.data.local.entity.TransactionDto
import com.example.jetexpense.domain.model.Transaction
import com.example.jetexpense.domain.usecase.GetDateUseCase
import com.example.jetexpense.domain.usecase.GetFormattedDateUseCase
import com.example.jetexpense.domain.usecase.read_database.*
import com.example.jetexpense.domain.usecase.read_datastore.GetCurrencyUseCase
import com.example.jetexpense.domain.usecase.read_datastore.GetExpenseLimitUseCase
import com.example.jetexpense.domain.usecase.read_datastore.GetLimitDurationUseCase
import com.example.jetexpense.domain.usecase.read_datastore.GetLimitKeyUseCase
import com.example.jetexpense.domain.usecase.write_database.InsertAccountsUseCase
import com.example.jetexpense.domain.usecase.write_database.InsertNewTransactionUseCase
import com.example.jetexpense.presentation.common.UiEvents
import com.example.jetexpense.util.AccountType
import com.example.jetexpense.util.CategoryType
import com.example.jetexpense.util.TabButtonType
import com.example.jetexpense.util.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDateUseCase: GetDateUseCase,
    private val getFormattedDateUseCase: GetFormattedDateUseCase,
    private val insertDailyTransactionUseCase: InsertNewTransactionUseCase,
    private val insertAccountsUseCase: InsertAccountsUseCase,
    private val getDailyTransactionUseCase: GetDailyTransactionUseCase,
    private val getAllTransactionUseCase: GetAllTransactionUseCase,
    private val getAccountUseCase: GetAccountUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val getExpenseLimitUseCase: GetExpenseLimitUseCase,
    private val getLimitDurationUseCase: GetLimitDurationUseCase,
    private val getLimitKeyUseCase: GetLimitKeyUseCase,
    private val getCurrentDayExpTransactionUseCase: GetCurrentDayExpTransactionUseCase,
    private val getWeeklyExpTransactionUseCase: GetWeeklyExpTransactionUseCase,
    private val getMonthlyExpTransactionUse: GetMonthlyExpTransactionUse
) : ViewModel() {


    private var decimal: String = String()
    private var isDecimal = MutableStateFlow(false)
    private var duration = MutableStateFlow(0)


    var tabButton = MutableStateFlow(TabButtonType.TODAY)
        private set

    var category = MutableStateFlow(CategoryType.FOOD_DRINK)
        private set

    var account = MutableStateFlow(AccountType.CASH)
        private set

    var transactionAmount = MutableStateFlow("0.00")
        private set

    var dailyTransaction = MutableStateFlow(emptyList<Transaction>())
        private set

    var monthlyTransaction = MutableStateFlow(mapOf<String, List<Transaction>>())
        private set

    var currentExpenseAmount = MutableStateFlow(0.0)
        private set

    var transactionTitle = MutableStateFlow(String())
        private set

    var showInfoBanner = MutableStateFlow(false)
        private set

    var totalIncome = MutableStateFlow(0.0)
        private set

    var totalExpense = MutableStateFlow(0.0)
        private set

    var formattedDate = MutableStateFlow(String())
        private set

    var date = MutableStateFlow(String())
        private set

    var currentTime = MutableStateFlow(Calendar.getInstance().time)
        private set

    var selectedCurrencyCode = MutableStateFlow(String())
        private set

    var limitAlert = MutableSharedFlow<UiEvents>(replay = 1)
        private set

    var limitKey = MutableStateFlow(false)
        private set


    init {
        val currentDate = getDateUseCase()
        formattedDate.value = getFormattedDateUseCase(currentTime.value)
        date.value = currentDate
        currencyFormat()

        viewModelScope.launch(IO) {
            getLimitDurationUseCase().collect { pref ->
                duration.value = pref
            }
        }
        viewModelScope.launch(IO) {
            getLimitKeyUseCase().collectLatest { pref ->
                limitKey.value = pref
            }
        }

        viewModelScope.launch(IO) {
            when (duration.value) {
                0 -> {
                    getCurrentDayExpTransactionUseCase().collect { result ->
                        val trx = result.map {
                            it.toTransaction()
                        }
                        currentExpenseAmount.value = calculateTransaction(trx.map { it.amount })
                    }
                }
                1 -> {
                    getWeeklyExpTransactionUseCase().collect { result ->

                        val trx = result.map { trans -> trans.toTransaction() }
                        currentExpenseAmount.value = calculateTransaction(trx.map { it.amount })
                    }
                }
                else -> {
                    getMonthlyExpTransactionUse().collect { result ->
                        val trx = result.map { trans -> trans.toTransaction() }
                        currentExpenseAmount.value = calculateTransaction(trx.map { it.amount })
                    }
                }
            }
        }
        viewModelScope.launch(IO) {
            getDailyTransactionUseCase(currentDate).collect {
                it?.let { expenses ->
                    dailyTransaction.value = expenses.map { dailyExpenses ->
                        dailyExpenses.toTransaction()
                    }.reversed()

                }
            }
        }
        viewModelScope.launch(IO) {
            getAllTransactionUseCase().collect { allTransaction ->
                allTransaction?.let {
                    val allSortedTrx = allTransaction.map { it.toTransaction() }.reversed()
                    monthlyTransaction.value = allSortedTrx.groupBy { monthlyExpense ->
                        getFormattedDateUseCase(monthlyExpense.date)
                    }
                }
            }
        }
        viewModelScope.launch(IO) {
            getAccountsUseCase().collect { accountsDto ->
                val accounts = accountsDto.map { it.toAccount() }
                val income = calculateTransaction(accounts.map { it.income })
                val expense = calculateTransaction(accounts.map { it.expense })

                totalIncome.value = income
                totalExpense.value = expense
            }
        }
    }


    private fun calculateTransaction(transaction: kotlin.collections.List<Double>): Double {
        return transaction.sumOf {
            it
        }
    }

    fun selectTabButton(button: TabButtonType) {
        tabButton.value = button
    }

    fun selectCategory(categoryType: CategoryType) {
        category.value = categoryType
    }

    fun selectAccount(accountType: AccountType) {
        account.value = accountType
    }

    fun setTransactionTitle(title: String) {
        transactionTitle.value = title
    }

    fun setCurrentTime(time: Date) {
        currentTime.value = time
    }

    fun insertDailyTransaction(
        date: String,
        amount: Double,
        category: String,
        transactionType: String,
        transactionTitle: String,
        navigateBack: () -> Unit
    ) {
        viewModelScope.launch(IO) {
            if (amount <= 0.00) {
                showInfoBanner.value = true
                delay(2000)
                showInfoBanner.value = false
                return@launch
            }
            val newTransaction = TransactionDto(
                date = currentTime.value,
                dateOfEntry = date,
                account = account.value.title,
                category = category,
                transactionType = transactionType,
                title = transactionTitle,
                amount = amount
            )
            insertDailyTransactionUseCase(dailyExpense = newTransaction)
            if (transactionType == Constants.INCOME) {
                val currentAccount = getAccountUseCase(account = account.value.title).first()
                val newIncomeAmount = currentAccount.income + amount
                val balance = newIncomeAmount - currentAccount.expense

                currentAccount.income = newIncomeAmount
                currentAccount.balance = balance

                insertAccountsUseCase(listOf(currentAccount))

            } else {
                val currentAccount = getAccountUseCase(account = account.value.title).first()
                val newExpenseAmount = currentAccount.expense + amount
                val balance = currentAccount.income - newExpenseAmount

                currentAccount.expense = newExpenseAmount
                currentAccount.balance = balance

                insertAccountsUseCase(listOf(currentAccount))
            }
            withContext(Main) {
                navigateBack()
            }
        }
    }

    fun setTransaction(amount: String) {
        val value = transactionAmount.value
        val whole = value.substring(0, value.indexOf("."))

        if (amount == ".") {
            isDecimal.value = true
            return
        }
        if (isDecimal.value) {
            if (decimal.length == 2) {
                decimal = decimal.substring(0, decimal.length - 1) + amount
            } else {
                decimal += amount
            }
            val newDecimal = decimal.toDouble() / 100.0
            transactionAmount.value = String.format("%.2f", whole.toDouble() + newDecimal)
            return
        }
        if (whole == "0") {
            transactionAmount.value = String.format("%.2f", amount.toDouble())
        } else {
            transactionAmount.value = String.format("%.2f", (whole + amount).toDouble())
        }
    }

    fun displayTransaction(
        transactionDate: String?,
        transactionPos: Int?,
        transactionStatus: Int?
    ) {
        if (transactionPos != -1 && transactionStatus != -1) {
            val transaction = if (transactionStatus == 0)
                dailyTransaction.value[transactionPos!!]
            else {
                transactionDate?.let {
                    monthlyTransaction.value[it]!![transactionPos!!]
                }
            }
            setTransactionTitle(transaction!!.title)
            currentTime.value = transaction.date
            AccountType.values().forEach {
                if (it.title == transaction.account)
                    selectAccount(it)
                return@forEach
            }
            transactionAmount.value = transaction.amount.toString()
            CategoryType.values().forEach {
                if (it.title == transaction.category)
                    selectCategory(it)
            }
        }
    }

    fun updateTransaction(
        transactionDate: String?,
        transactionPos: Int?,
        transactionStatus: Int?,
        navigateBack: () -> Unit
    ) {
        if (transactionPos != -1 && transactionStatus != -1) {
            val updateTransaction = if (transactionStatus == 0)
                dailyTransaction.value[transactionPos!!]
            else {
                transactionDate?.let {
                    monthlyTransaction.value[it]!![transactionPos!!]
                }
            }
            viewModelScope.launch(IO) {
                if (transactionAmount.value.toDouble() != updateTransaction!!.amount) {
                    val currentAccount = getAccountUseCase(account.value.title).first()
                    if (updateTransaction.transactionType == TransactionType.INCOME.title) {
                        currentAccount.income = currentAccount.income - updateTransaction.amount
                        currentAccount.income =
                            currentAccount.income + transactionAmount.value.toDouble()
                        currentAccount.balance = currentAccount.income - currentAccount.expense
                    } else {
                        currentAccount.expense = currentAccount.expense - updateTransaction.amount
                        currentAccount.expense =
                            currentAccount.expense + transactionAmount.value.toDouble()
                        currentAccount.balance = currentAccount.income - currentAccount.expense
                    }
                    insertAccountsUseCase(listOf(currentAccount))
                }

                val updatedTransaction = TransactionDto(
                    updateTransaction.date,
                    updateTransaction.dateOfEntry,
                    transactionAmount.value.toDouble(),
                    account.value.title,
                    category.value.title,
                    updateTransaction.transactionType,
                    transactionTitle.value
                )
                insertDailyTransactionUseCase(dailyExpense = updatedTransaction)
                withContext(Main) {
                    navigateBack()
                }
            }
        }
    }

    fun displayExpenseLimitWarning() {
        viewModelScope.launch(IO) {
            getExpenseLimitUseCase().collectLatest { expenseAmount ->
                if (expenseAmount <= 0.0) return@collectLatest
                val threshHold = 0.8 * expenseAmount
                when {
                    currentExpenseAmount.value > expenseAmount -> {
                        val expenseOverFlow =
                            (currentExpenseAmount.value - expenseAmount).toString().amountFormat()
                        val info =
                            "${selectedCurrencyCode.value} $expenseOverFlow over specified limit"
                        limitAlert.emit(UiEvents.Alert(info))
                    }
                    currentExpenseAmount.value > threshHold -> {
                        val expenseAvailable =
                            (expenseAmount - currentExpenseAmount.value).toString().amountFormat()
                        val info =
                            "${selectedCurrencyCode.value} $expenseAvailable away from specified limit"
                        limitAlert.emit(UiEvents.Alert(info))
                    }
                    else -> limitAlert.emit(UiEvents.NoAlert())
                }
            }
        }
    }


    private fun currencyFormat() {
        viewModelScope.launch(IO) {
            getCurrencyUseCase().collect { selectedCurrency ->
                selectedCurrencyCode.value = selectedCurrency


            }
        }
    }

    private fun String.amountFormat(): String {
        val amountFormatter = DecimalFormat("#,##0.00")
        return " " + amountFormatter.format(this.toDouble())
    }

}