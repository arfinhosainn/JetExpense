package com.example.jetexpense.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jetexpense.domain.model.Account

@Entity(tableName = "account_table")
data class AccountDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int,
    @ColumnInfo(name = "account")
    val accountType: String,
    @ColumnInfo(name = "balance")
    var balance: Double,
    @ColumnInfo(name = "income")
    var income: Double,
    @ColumnInfo(name = "expense")
    var expense: Double
){
    fun toAccount(): Account = Account(
        accountType,
        balance,
        income,
        expense
    )

}

//fun AccountDto.toAccount(): Account {
//    return Account(
//        account = accountType,
//        amount = balance,
//        income = income,
//        expense = expense
//    )
//}
