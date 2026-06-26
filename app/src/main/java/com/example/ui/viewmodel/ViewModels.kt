package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SplitFlowDatabase
import com.example.data.model.*
import com.example.data.remote.FirebaseSyncManager
import com.example.data.remote.GeminiOcr
import com.example.data.remote.ReceiptAnalysisResult
import com.example.data.repository.SplitFlowRepository
import com.example.data.repository.PdfExporter
import android.content.Context
import java.io.File
import com.example.domain.DebtSimplifier
import com.example.domain.SimplifiedDebt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import com.example.BuildConfig

data class ExpenseWithGroupInfo(val expense: ExpenseEntity, val groupName: String)

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SplitFlowRepository

    init {
        val database = SplitFlowDatabase.getDatabase(application)
        repository = SplitFlowRepository(database.splitFlowDao())
    }

    val activeUser: StateFlow<UserEntity?> = repository.getActiveUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isProfileComplete = MutableStateFlow<Boolean?>(null)
    val isProfileComplete: StateFlow<Boolean?> = _isProfileComplete.asStateFlow()

    private val _isProgress = MutableStateFlow(false)
    val isProgress: StateFlow<Boolean> = _isProgress.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        viewModelScope.launch {
            activeUser.collect { user ->
                if (user == null) {
                    _isProfileComplete.value = null
                } else {
                    _isProfileComplete.value = user.name.isNotEmpty() && user.email.isNotEmpty()
                }
            }
        }
    }

    fun signIn(email: String, name: String) {
        viewModelScope.launch {
            _isProgress.value = true
            _authError.value = null
            try {
                // Determine user ID deterministically or randomly
                val userId = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                val existingUser = repository.getActiveUserSuspended()
                
                // Set up default or retrieved user details
                val user = UserEntity(
                    id = userId,
                    name = name,
                    email = email,
                    isLoggedIn = true
                )
                repository.clearActiveUsers()
                repository.insertUser(user)

                // Try Firebase Auth if available
                withContext(Dispatchers.IO) {
                    FirebaseSyncManager.authenticateUser(getApplication(), email, name)
                }
            } catch (e: Exception) {
                _authError.value = e.message ?: "Authentication failed"
            } finally {
                _isProgress.value = false
            }
        }
    }

    fun signUp(email: String, name: String, phone: String, upiId: String) {
        viewModelScope.launch {
            _isProgress.value = true
            _authError.value = null
            try {
                val userId = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                val user = UserEntity(
                    id = userId,
                    name = name,
                    email = email,
                    phone = phone,
                    upiId = upiId,
                    isLoggedIn = true
                )
                repository.clearActiveUsers()
                repository.insertUser(user)
                
                withContext(Dispatchers.IO) {
                    FirebaseSyncManager.authenticateUser(getApplication(), email, name)
                }
            } catch (e: Exception) {
                _authError.value = e.message ?: "Registration failed"
            } finally {
                _isProgress.value = false
            }
        }
    }

    fun updateProfile(name: String, phone: String, upiId: String) {
        viewModelScope.launch {
            val currentUser = activeUser.value ?: return@launch
            val updatedUser = currentUser.copy(name = name, phone = phone, upiId = upiId)
            repository.insertUser(updatedUser)
        }
    }

    fun enterUserMode(name: String) {
        viewModelScope.launch {
            _isProgress.value = true
            try {
                val email = "${name.replace(" ", "").lowercase()}@splitsmart.app"
                val userId = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                val user = UserEntity(
                    id = userId,
                    name = name,
                    email = email,
                    isLoggedIn = true
                )
                repository.clearActiveUsers()
                repository.insertUser(user)
            } catch (e: Exception) {
                _authError.value = e.message ?: "Failed to start User Mode"
            } finally {
                _isProgress.value = false
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.clearActiveUsers()
        }
    }

    fun clearError() {
        _authError.value = null
    }
}

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SplitFlowRepository

    init {
        val database = SplitFlowDatabase.getDatabase(application)
        repository = SplitFlowRepository(database.splitFlowDao())
    }

    val groups: StateFlow<List<GroupEntity>> = repository.getAllGroups()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activities: StateFlow<List<ActivityEntity>> = repository.getAllActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _allExpenses = MutableStateFlow<List<ExpenseWithGroupInfo>>(emptyList())
    val allExpenses: StateFlow<List<ExpenseWithGroupInfo>> = _allExpenses.asStateFlow()

    private val _netBalance = MutableStateFlow(0.0)
    val netBalance: StateFlow<Double> = _netBalance.asStateFlow()

    private val _totalOwed = MutableStateFlow(0.0)
    val totalOwed: StateFlow<Double> = _totalOwed.asStateFlow()

    private val _totalToReceive = MutableStateFlow(0.0)
    val totalToReceive: StateFlow<Double> = _totalToReceive.asStateFlow()

    init {
        // Collect groups and update aggregated expenses and balances
        viewModelScope.launch {
            combine(groups, repository.getActiveUser()) { groupList, user ->
                Pair(groupList, user)
            }.collect { (groupList, user) ->
                if (user == null) {
                    _allExpenses.value = emptyList()
                    _netBalance.value = 0.0
                    _totalOwed.value = 0.0
                    _totalToReceive.value = 0.0
                    return@collect
                }
                
                val expensesList = mutableListOf<ExpenseWithGroupInfo>()
                var owedSum = 0.0
                var receiveSum = 0.0

                for (group in groupList) {
                    val groupExpenses = repository.getExpensesForGroupList(group.id)
                    val groupMembers = repository.getMembersForGroupList(group.id)
                    
                    expensesList.addAll(groupExpenses.map { ExpenseWithGroupInfo(it, group.name) })

                    // Find corresponding member for current user
                    val currentMember = groupMembers.find { 
                        it.email.equals(user.email, ignoreCase = true) || it.name.equals(user.name, ignoreCase = true)
                    }

                    if (currentMember != null) {
                        val groupSplits = mutableListOf<ExpenseSplitEntity>()
                        for (expense in groupExpenses) {
                            groupSplits.addAll(repository.getSplitsForExpenseList(expense.id))
                        }
                        val simplified = DebtSimplifier.simplify(groupMembers, groupExpenses, groupSplits)
                        
                        for (debt in simplified) {
                            if (debt.fromMemberId == currentMember.id) {
                                owedSum += debt.amount
                            }
                            if (debt.toMemberId == currentMember.id) {
                                receiveSum += debt.amount
                            }
                        }
                    }
                }

                _allExpenses.value = expensesList.sortedByDescending { it.expense.date }
                _totalOwed.value = owedSum
                _totalToReceive.value = receiveSum
                _netBalance.value = receiveSum - owedSum
            }
        }
    }

    fun refreshBalances() {
        // Handled automatically via database Flow collection
    }

    fun createGroup(name: String, description: String, passcode: String, upiId: String = "") {
        viewModelScope.launch {
            val user = repository.getActiveUserSuspended() ?: return@launch
            val groupId = UUID.randomUUID().toString()
            val group = GroupEntity(
                id = groupId,
                name = name,
                description = description,
                createdBy = user.id,
                passcode = passcode,
                upiId = upiId
            )
            repository.insertGroup(group)

            // Add current user as member automatically
            val member = MemberEntity(
                id = UUID.randomUUID().toString(),
                groupId = groupId,
                name = user.name,
                email = user.email,
                phone = user.phone,
                upiId = user.upiId,
                photoUrl = user.photoUrl
            )
            repository.insertMember(member)

            repository.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    groupId = groupId,
                    title = "Group Created",
                    description = "${user.name} created the group \"$name\"",
                    type = "GROUP"
                )
            )

            withContext(Dispatchers.IO) {
                FirebaseSyncManager.syncGroupToFirestore(getApplication(), group)
            }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repository.deleteGroupById(groupId)
            withContext(Dispatchers.IO) {
                FirebaseSyncManager.deleteGroup(getApplication(), groupId)
            }
        }
    }

    private val _isJudgeMode = MutableStateFlow(false)
    val isJudgeMode: StateFlow<Boolean> = _isJudgeMode.asStateFlow()

    fun setJudgeMode(enabled: Boolean) {
        _isJudgeMode.value = enabled
        viewModelScope.launch {
            if (enabled) {
                repository.ensureDemoDataPopulated()
            } else {
                repository.clearAllData()
            }
            refreshBalances()
        }
    }

    fun resetDemo() {
        viewModelScope.launch {
            repository.clearDemoData()
            repository.ensureDemoDataPopulated()
            refreshBalances()
        }
    }

    private val _aiInsight = MutableStateFlow<String?>(null)
    val aiInsight: StateFlow<String?> = _aiInsight.asStateFlow()

    fun generateDebtInsight() {
        viewModelScope.launch {
            _aiInsight.value = "Analyzing splitting dynamics..."
            val allGroups = repository.getAllGroupsListSuspended()
            val debts = mutableListOf<SimplifiedDebt>()
            val activeUserObj = repository.getActiveUserSuspended()
            if (activeUserObj == null) {
                _aiInsight.value = null
                return@launch
            }

            for (group in allGroups) {
                val groupExpenses = repository.getExpensesForGroupList(group.id)
                val groupMembers = repository.getMembersForGroupList(group.id)
                val groupSplits = mutableListOf<ExpenseSplitEntity>()
                for (expense in groupExpenses) {
                    groupSplits.addAll(repository.getSplitsForExpenseList(expense.id))
                }
                debts.addAll(DebtSimplifier.simplify(groupMembers, groupExpenses, groupSplits))
            }

            val userDebts = debts.filter {
                it.fromMemberName.equals(activeUserObj.name, ignoreCase = true) ||
                it.toMemberName.equals(activeUserObj.name, ignoreCase = true)
            }

            if (userDebts.isEmpty()) {
                _aiInsight.value = "🎉 You're all settled up! No outstanding debts."
                return@launch
            }

            val debtSummary = userDebts.take(5).joinToString("; ") {
                "${it.fromMemberName} owes ${it.toMemberName} ₹${it.amount}"
            }

            try {
                val prompt = "User has these debts: $debtSummary. Give ONE actionable tip max 20 words on who they should pay first or settle up with to minimize transactions."
                val response = callGeminiDirect(prompt)
                _aiInsight.value = response
            } catch (e: Exception) {
                _aiInsight.value = "💡 Tip: Settle with the highest creditor first to simplify your balance!"
            }
        }
    }

    private suspend fun callGeminiDirect(promptText: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "💡 Tip: Settle with the highest creditor first to simplify your balance!"
        }
        val client = okhttp3.OkHttpClient()
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", promptText))
                    })
                })
            })
        }
        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()
        val response = client.newCall(request).execute()
        val responseBodyString = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            return@withContext "💡 Tip: Settle with the highest creditor first to simplify your balance!"
        }
        val responseJson = JSONObject(responseBodyString)
        val candidates = responseJson.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val firstPart = parts?.optJSONObject(0)
        firstPart?.optString("text")?.trim() ?: "💡 Tip: Settle with the highest creditor first to simplify your balance!"
    }
}

class GroupDetailViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SplitFlowRepository
    private var currentGroupId: String? = null

    init {
        val database = SplitFlowDatabase.getDatabase(application)
        repository = SplitFlowRepository(database.splitFlowDao())
    }

    private val _group = MutableStateFlow<GroupEntity?>(null)
    val group: StateFlow<GroupEntity?> = _group.asStateFlow()

    private val _members = MutableStateFlow<List<MemberEntity>>(emptyList())
    val members: StateFlow<List<MemberEntity>> = _members.asStateFlow()

    private val _expenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val expenses: StateFlow<List<ExpenseEntity>> = _expenses.asStateFlow()

    private val _splits = MutableStateFlow<List<ExpenseSplitEntity>>(emptyList())
    val splits: StateFlow<List<ExpenseSplitEntity>> = _splits.asStateFlow()

    private val _settlements = MutableStateFlow<List<SettlementEntity>>(emptyList())
    val settlements: StateFlow<List<SettlementEntity>> = _settlements.asStateFlow()

    private val _simplifiedDebts = MutableStateFlow<List<SimplifiedDebt>>(emptyList())
    val simplifiedDebts: StateFlow<List<SimplifiedDebt>> = _simplifiedDebts.asStateFlow()

    fun loadGroup(groupId: String) {
        currentGroupId = groupId
        checkRecurringExpenses(groupId)
        viewModelScope.launch {
            combine(
                repository.getGroupById(groupId),
                repository.getMembersForGroup(groupId),
                repository.getExpensesForGroup(groupId),
                repository.getSettlementsForGroup(groupId)
            ) { g, m, e, s ->
                Quad(g, m, e, s)
            }.collect { quad ->
                _group.value = quad.first
                _members.value = quad.second
                _expenses.value = quad.third
                _settlements.value = quad.fourth

                val allSplits = mutableListOf<ExpenseSplitEntity>()
                for (expense in quad.third) {
                    allSplits.addAll(repository.getSplitsForExpenseList(expense.id))
                }
                _splits.value = allSplits

                _simplifiedDebts.value = DebtSimplifier.simplify(quad.second, quad.third, allSplits)
            }
        }
    }

    fun addMember(name: String, email: String, phone: String, upiId: String) {
        val groupId = currentGroupId ?: return
        viewModelScope.launch {
            val memberId = UUID.randomUUID().toString()
            val member = MemberEntity(
                id = memberId,
                groupId = groupId,
                name = name,
                email = email,
                phone = phone,
                upiId = upiId
            )
            repository.insertMember(member)

            repository.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    groupId = groupId,
                    title = "Member Added",
                    description = "$name added to the group",
                    type = "MEMBER"
                )
            )
        }
    }

    fun removeMember(memberId: String, name: String) {
        val groupId = currentGroupId ?: return
        viewModelScope.launch {
            repository.deleteMemberById(memberId)
            repository.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    groupId = groupId,
                    title = "Member Removed",
                    description = "$name was removed from the group",
                    type = "MEMBER"
                )
            )
        }
    }

    fun recordSettlement(fromMemberId: String, toMemberId: String, amount: Double, method: String, transactionRef: String) {
        val groupId = currentGroupId ?: return
        viewModelScope.launch {
            val settlementId = UUID.randomUUID().toString()
            val settlement = SettlementEntity(
                id = settlementId,
                groupId = groupId,
                fromMemberId = fromMemberId,
                toMemberId = toMemberId,
                amount = amount,
                isResolved = true,
                paymentMethod = method,
                transactionRef = transactionRef
            )
            repository.insertSettlement(settlement)

            val fromName = _members.value.find { it.id == fromMemberId }?.name ?: "Someone"
            val toName = _members.value.find { it.id == toMemberId }?.name ?: "Someone"

            repository.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    groupId = groupId,
                    title = "Debt Settled",
                    description = "$fromName settled ₹$amount to $toName via $method",
                    type = "SETTLEMENT"
                )
            )

            // Adjust simplified balance by inserting balancing "reversing" transaction
            // We implement this by adding a balancing expense of equal amount, or the caller settles.
            // SettleX treats settlements as visual logs, but since balances are dynamically derived from expenses,
            // we should save a compensating expense paid by the debtor for the creditor so that their net balance correctly offsets!
            // This is a crucial fintech detail: a settlement is essentially an expense of "Settle Debt" paid by debtor, split exactly to creditor.
            val compensationExpenseId = UUID.randomUUID().toString()
            val compExpense = ExpenseEntity(
                id = compensationExpenseId,
                groupId = groupId,
                title = "Settlement: $fromName to $toName",
                amount = amount,
                category = "Other",
                paidByMemberId = fromMemberId,
                splitType = "EXACT"
            )
            val compSplit = ExpenseSplitEntity(
                id = "${compensationExpenseId}_${toMemberId}",
                expenseId = compensationExpenseId,
                memberId = toMemberId,
                owedAmount = amount,
                exactAmount = amount
            )
            repository.insertExpense(compExpense, listOf(compSplit))

            withContext(Dispatchers.IO) {
                FirebaseSyncManager.syncSettlementToFirestore(getApplication(), settlement)
            }
        }
    }

    fun deleteExpense(expenseId: String, title: String) {
        viewModelScope.launch {
            repository.deleteExpenseById(expenseId)
            currentGroupId?.let { gId ->
                repository.insertActivity(
                    ActivityEntity(
                        id = UUID.randomUUID().toString(),
                        groupId = gId,
                        title = "Expense Deleted",
                        description = "Expense \"$title\" was deleted",
                        type = "EXPENSE"
                    )
                )
            }
        }
    }

    fun refreshFromFirestore() {
        // Local state updates automatically
    }

    fun exportGroupToPdf(context: Context): File? {
        val g = _group.value ?: return null
        val m = _members.value
        val e = _expenses.value
        val d = _simplifiedDebts.value
        return PdfExporter.exportExpensesToPdf(context, g, m, e, d)
    }

    private val _overdueRecurringExpenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val overdueRecurringExpenses: StateFlow<List<ExpenseEntity>> = _overdueRecurringExpenses.asStateFlow()

    fun checkRecurringExpenses(groupId: String) {
        viewModelScope.launch {
            val groupExpenses = repository.getExpensesForGroupList(groupId)
            val overdue = groupExpenses.filter { it.isRecurring && (System.currentTimeMillis() - it.date > 30L * 24 * 60 * 60 * 1000) }
            _overdueRecurringExpenses.value = overdue
        }
    }

    fun recreateRecurringExpense(expenseId: String) {
        viewModelScope.launch {
            val oldExpense = _expenses.value.find { it.id == expenseId } ?: return@launch
            val oldSplits = repository.getSplitsForExpenseList(expenseId)
            
            val newExpenseId = UUID.randomUUID().toString()
            val newExpense = oldExpense.copy(
                id = newExpenseId,
                date = System.currentTimeMillis()
            )
            val newSplits = oldSplits.map {
                it.copy(id = "${newExpenseId}_${it.memberId}", expenseId = newExpenseId)
            }
            repository.insertExpense(newExpense, newSplits)
            
            repository.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    groupId = oldExpense.groupId,
                    title = "Recurring Expense Renewed",
                    description = "Renewed \"${oldExpense.title}\" of ₹${oldExpense.amount}",
                    type = "EXPENSE"
                )
            )

            checkRecurringExpenses(oldExpense.groupId)
        }
    }

    private val _aiNarration = MutableStateFlow<String?>(null)
    val aiNarration: StateFlow<String?> = _aiNarration.asStateFlow()

    fun generateGroupNarration(groupName: String, memberCount: Int, expenseCount: Int, debts: List<SimplifiedDebt>) {
        viewModelScope.launch {
            _aiNarration.value = "AI is composing narration for evaluation..."
            val debtSummary = debts.map { "${it.fromMemberName} owes ${it.toMemberName} ₹${it.amount}" }.joinToString("; ")
            val prompt = """
            You are explaining a debt-splitting app to a hackathon judge.
            Group: "$groupName", $memberCount members, $expenseCount expenses.
            Simplified debts: $debtSummary
            Write 3 short bullet points (max 25 words each) explaining what's impressive about this, highlighting how SettleX's Greedy Debt Simplifier minimized transactions.
            Format: bullet symbol + space + text. No headers.
            """
            try {
                val result = callGeminiDirect(prompt)
                _aiNarration.value = result
            } catch (e: Exception) {
                _aiNarration.value = "• 🧮 Debt Simplification: Reduces multiple overlapping balances into minimum overall payments.\n• ⚡ UPI One-Tap: Direct deep linking lets members settle instantly with standard UPI apps.\n• 📱 Zero Friction: Seeded with rich, realistic fintech scenarios for instant judge review."
            }
        }
    }

    fun clearNarration() {
        _aiNarration.value = null
    }

    private suspend fun callGeminiDirect(promptText: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "• 🧮 Debt Simplification: Reduces multiple overlapping balances into minimum overall payments.\n• ⚡ UPI One-Tap: Direct deep linking lets members settle instantly with standard UPI apps.\n• 📱 Zero Friction: Seeded with rich, realistic fintech scenarios for instant judge review."
        }
        val client = okhttp3.OkHttpClient()
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", promptText))
                    })
                })
            })
        }
        val requestBody = requestJson.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(requestBody).build()
        val response = client.newCall(request).execute()
        val responseBodyString = response.body?.string() ?: ""
        if (!response.isSuccessful) {
            return@withContext "• 🧮 Debt Simplification: Reduces multiple overlapping balances into minimum overall payments.\n• ⚡ UPI One-Tap: Direct deep linking lets members settle instantly with standard UPI apps.\n• 📱 Zero Friction: Seeded with rich, realistic fintech scenarios for instant judge review."
        }
        val responseJson = JSONObject(responseBodyString)
        val candidates = responseJson.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val firstPart = parts?.optJSONObject(0)
        firstPart?.optString("text")?.trim() ?: "• 🧮 Debt Simplification: Reduces multiple overlapping balances into minimum overall payments."
    }
}

class ExpenseEntryViewModel(application: Application) : AndroidViewModel(application) {
    val repository: SplitFlowRepository

    init {
        val database = SplitFlowDatabase.getDatabase(application)
        repository = SplitFlowRepository(database.splitFlowDao())
    }

    private val _ocrResult = MutableStateFlow<ReceiptAnalysisResult?>(null)
    val ocrResult: StateFlow<ReceiptAnalysisResult?> = _ocrResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _suggestedCategory = MutableStateFlow<String?>(null)
    val suggestedCategory: StateFlow<String?> = _suggestedCategory.asStateFlow()

    fun analyzeReceipt(bitmap: Bitmap) {
        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val base64 = withContext(Dispatchers.Default) {
                    GeminiOcr.bitmapToBase64(bitmap)
                }
                val result = GeminiOcr.analyzeReceipt(base64)
                _ocrResult.value = result
                if (result.error == null && result.title.isNotEmpty()) {
                    suggestCategory(result.title)
                }
            } catch (e: Exception) {
                _ocrResult.value = ReceiptAnalysisResult("", 0.0, System.currentTimeMillis(), emptyList(), e.message)
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun suggestCategory(title: String) {
        viewModelScope.launch {
            val category = GeminiOcr.suggestCategory(title)
            _suggestedCategory.value = category
        }
    }

    fun clearOcr() {
        _ocrResult.value = null
        _suggestedCategory.value = null
    }

    fun saveExpense(
        groupId: String,
        title: String,
        amount: Double,
        category: String,
        paidByMemberId: String,
        splitType: String,
        splits: List<ExpenseSplitEntity>
    ) {
        viewModelScope.launch {
            val expenseId = UUID.randomUUID().toString()
            val expense = ExpenseEntity(
                id = expenseId,
                groupId = groupId,
                title = title,
                amount = amount,
                category = category,
                paidByMemberId = paidByMemberId,
                splitType = splitType
            )

            // Map split entities with the generated expenseId
            val finalSplits = splits.map {
                it.copy(id = "${expenseId}_${it.memberId}", expenseId = expenseId)
            }

            repository.insertExpense(expense, finalSplits)

            repository.insertActivity(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    groupId = groupId,
                    title = "Expense Added",
                    description = "Added \"$title\" of ₹$amount",
                    type = "EXPENSE"
                )
            )

            withContext(Dispatchers.IO) {
                FirebaseSyncManager.syncExpenseToFirestore(getApplication(), expense, finalSplits)
            }
        }
    }
}

// Simple Quad Tuple helper for combining 4 flows
data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
