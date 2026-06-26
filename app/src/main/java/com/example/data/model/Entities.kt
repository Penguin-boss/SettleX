package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val upiId: String = "",
    val photoUrl: String = "",
    val isLoggedIn: Boolean = false
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val upiId: String = "",
    val passcode: String = ""
)

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val name: String,
    val email: String,
    val phone: String = "",
    val upiId: String = "",
    val photoUrl: String = ""
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val title: String,
    val amount: Double,
    val category: String,
    val paidByMemberId: String,
    val date: Long = System.currentTimeMillis(),
    val splitType: String = "EQUAL", // EQUAL, PERCENTAGE, EXACT
    val isRecurring: Boolean = false
)

@Entity(tableName = "expense_splits")
data class ExpenseSplitEntity(
    @PrimaryKey val id: String, // expenseId + "_" + memberId
    val expenseId: String,
    val memberId: String,
    val owedAmount: Double,
    val percentage: Double = 0.0,
    val exactAmount: Double = 0.0
)

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val fromMemberId: String,
    val toMemberId: String,
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false,
    val paymentMethod: String = "UPI",
    val transactionRef: String = ""
)

@Entity(tableName = "activities")
data class ActivityEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "EXPENSE" // EXPENSE, GROUP, SETTLEMENT, MEMBER
)
