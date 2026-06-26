package com.example.domain

import com.example.data.model.MemberEntity
import com.example.data.model.ExpenseEntity
import com.example.data.model.ExpenseSplitEntity
import kotlin.math.min

data class DebtorCreditor(val memberId: String, val name: String, var balance: Double)

data class SimplifiedDebt(
    val fromMemberId: String,
    val fromMemberName: String,
    val toMemberId: String,
    val toMemberName: String,
    val amount: Double
)

object DebtSimplifier {
    /**
     * Simplifies debts using Greedy Graph Algorithm.
     * Returns minimum number of transactions to settle a group.
     */
    fun simplify(
        members: List<MemberEntity>,
        expenses: List<ExpenseEntity>,
        splits: List<ExpenseSplitEntity>
    ): List<SimplifiedDebt> {
        val memberMap = members.associateBy { it.id }
        val netBalances = mutableMapOf<String, Double>()
        members.forEach { netBalances[it.id] = 0.0 }

        for (expense in expenses) {
            netBalances[expense.paidByMemberId] =
                (netBalances[expense.paidByMemberId] ?: 0.0) + expense.amount
            val expenseSplits = splits.filter { it.expenseId == expense.id }
            for (split in expenseSplits) {
                netBalances[split.memberId] =
                    (netBalances[split.memberId] ?: 0.0) - split.owedAmount
            }
        }

        val debtors = mutableListOf<DebtorCreditor>()
        val creditors = mutableListOf<DebtorCreditor>()

        for ((memberId, balance) in netBalances) {
            val name = memberMap[memberId]?.name ?: "Member"
            when {
                balance < -0.01 -> debtors.add(DebtorCreditor(memberId, name, -balance))
                balance > 0.01  -> creditors.add(DebtorCreditor(memberId, name, balance))
            }
        }

        val settlements = mutableListOf<SimplifiedDebt>()
        while (debtors.isNotEmpty() && creditors.isNotEmpty()) {
            debtors.sortByDescending { it.balance }
            creditors.sortByDescending { it.balance }
            val debtor = debtors.first()
            val creditor = creditors.first()
            val amount = min(debtor.balance, creditor.balance)
            val roundedAmount = Math.round(amount * 100.0) / 100.0
            if (roundedAmount > 0) {
                settlements.add(
                    SimplifiedDebt(
                        debtor.memberId, debtor.name,
                        creditor.memberId, creditor.name, roundedAmount
                    )
                )
            }
            debtor.balance -= amount
            creditor.balance -= amount
            if (debtor.balance < 0.01) debtors.removeAt(0)
            if (creditor.balance < 0.01) creditors.removeAt(0)
        }
        return settlements
    }
}
