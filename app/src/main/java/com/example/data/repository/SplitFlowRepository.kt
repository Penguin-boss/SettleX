package com.example.data.repository

import com.example.data.local.SplitFlowDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class SplitFlowRepository(private val dao: SplitFlowDao) {

    // --- Users ---
    fun getUser(id: String): Flow<UserEntity?> = dao.getUser(id)
    
    fun getActiveUser(): Flow<UserEntity?> = dao.getActiveUser()
    
    suspend fun getActiveUserSuspended(): UserEntity? = dao.getActiveUserSuspended()
    
    suspend fun insertUser(user: UserEntity) {
        dao.insertUser(user)
    }
    
    suspend fun clearActiveUsers() {
        dao.clearActiveUsers()
    }

    // --- Groups ---
    fun getAllGroups(): Flow<List<GroupEntity>> = dao.getAllGroups()
    
    suspend fun getAllGroupsListSuspended(): List<GroupEntity> = dao.getAllGroupsListSuspended()
    
    fun getGroupById(id: String): Flow<GroupEntity?> = dao.getGroupById(id)
    
    suspend fun getGroupByIdSuspended(id: String): GroupEntity? = dao.getGroupByIdSuspended(id)
    
    suspend fun insertGroup(group: GroupEntity) {
        dao.insertGroup(group)
    }
    
    suspend fun deleteGroupById(id: String) {
        dao.deleteGroupById(id)
        dao.deleteMembersByGroupId(id)
        dao.deleteExpensesByGroupId(id)
        dao.deleteSettlementsByGroupId(id)
    }

    // --- Members ---
    fun getMembersForGroup(groupId: String): Flow<List<MemberEntity>> = dao.getMembersForGroup(groupId)
    
    suspend fun getMembersForGroupList(groupId: String): List<MemberEntity> = dao.getMembersForGroupList(groupId)
    
    suspend fun insertMember(member: MemberEntity) {
        dao.insertMember(member)
    }
    
    suspend fun insertMembers(members: List<MemberEntity>) {
        dao.insertMembers(members)
    }
    
    suspend fun deleteMemberById(memberId: String) {
        dao.deleteMemberById(memberId)
    }

    // --- Expenses ---
    fun getExpensesForGroup(groupId: String): Flow<List<ExpenseEntity>> = dao.getExpensesForGroup(groupId)
    
    suspend fun getExpensesForGroupList(groupId: String): List<ExpenseEntity> = dao.getExpensesForGroupList(groupId)
    
    suspend fun insertExpense(expense: ExpenseEntity, splits: List<ExpenseSplitEntity>) {
        dao.insertExpense(expense)
        dao.deleteSplitsByExpenseId(expense.id)
        dao.insertSplits(splits)
    }
    
    suspend fun deleteExpenseById(id: String) {
        dao.deleteExpenseById(id)
        dao.deleteSplitsByExpenseId(id)
    }

    // --- Splits ---
    fun getSplitsForExpense(expenseId: String): Flow<List<ExpenseSplitEntity>> = dao.getSplitsForExpense(expenseId)
    
    suspend fun getSplitsForExpenseList(expenseId: String): List<ExpenseSplitEntity> = dao.getSplitsForExpenseList(expenseId)

    // --- Settlements ---
    fun getSettlementsForGroup(groupId: String): Flow<List<SettlementEntity>> = dao.getSettlementsForGroup(groupId)
    
    suspend fun getSettlementsForGroupList(groupId: String): List<SettlementEntity> = dao.getSettlementsForGroupList(groupId)
    
    suspend fun insertSettlement(settlement: SettlementEntity) {
        dao.insertSettlement(settlement)
    }

    // --- Activities ---
    fun getActivitiesForGroup(groupId: String): Flow<List<ActivityEntity>> = dao.getActivitiesForGroup(groupId)
    
    fun getAllActivities(): Flow<List<ActivityEntity>> = dao.getAllActivities()
    
    suspend fun insertActivity(activity: ActivityEntity) {
        dao.insertActivity(activity)
    }

    suspend fun clearAllData() {
        dao.clearAllGroups()
        dao.clearAllMembers()
        dao.clearAllExpenses()
        dao.clearAllSplits()
        dao.clearAllSettlements()
        dao.clearAllActivities()
    }

    suspend fun clearDemoData() {
        dao.deleteMembersByGroupId("demo_grp_goa")
        dao.deleteMembersByGroupId("demo_grp_flat")
        dao.deleteMembersByGroupId("demo_grp_office")
        dao.deleteExpensesByGroupId("demo_grp_goa")
        dao.deleteExpensesByGroupId("demo_grp_flat")
        dao.deleteExpensesByGroupId("demo_grp_office")
        dao.deleteSettlementsByGroupId("demo_grp_goa")
        dao.deleteSettlementsByGroupId("demo_grp_flat")
        dao.deleteSettlementsByGroupId("demo_grp_office")
        dao.deleteGroupById("demo_grp_goa")
        dao.deleteGroupById("demo_grp_flat")
        dao.deleteGroupById("demo_grp_office")
    }

    suspend fun ensureDemoDataPopulated() {
        // 1. Create Demo User Chanakya as Active User
        val demoUser = UserEntity(
            id = "demo_user_chanakya",
            name = "Chanakya",
            email = "demo@splitsmart.app",
            phone = "9876543210",
            upiId = "chanakya@upi",
            isLoggedIn = true
        )
        dao.clearActiveUsers()
        dao.insertUser(demoUser)

        // 2. Group 1: Goa Trip 🏖️
        val groupGoa = GroupEntity(
            id = "demo_grp_goa",
            name = "Goa Trip 🏖️",
            description = "Year-end reunion at Candolim beach! Food, fun, and sun.",
            createdBy = "demo_user_chanakya",
            upiId = "chanakya@upi"
        )
        dao.insertGroup(groupGoa)

        val membersGoa = listOf(
            MemberEntity("demo_user_chanakya_goa", "demo_grp_goa", "Chanakya", "demo@splitsmart.app", "9876543210", "chanakya@upi"),
            MemberEntity("demo_member_rahul", "demo_grp_goa", "Rahul", "rahul@gmail.com", "9876543211", "rahul@upi"),
            MemberEntity("demo_member_priya", "demo_grp_goa", "Priya", "priya@gmail.com", "9876543212", "priya@upi")
        )
        dao.insertMembers(membersGoa)

        // Expenses for Goa
        val expResort = ExpenseEntity("demo_exp_resort", "demo_grp_goa", "Resort Booking", 5800.0, "Utilities", "demo_user_chanakya_goa")
        val splitsResort = listOf(
            ExpenseSplitEntity("demo_exp_resort_c", "demo_exp_resort", "demo_user_chanakya_goa", 1933.34),
            ExpenseSplitEntity("demo_exp_resort_r", "demo_exp_resort", "demo_member_rahul", 1933.33),
            ExpenseSplitEntity("demo_exp_resort_p", "demo_exp_resort", "demo_member_priya", 1933.33)
        )
        dao.insertExpense(expResort)
        dao.insertSplits(splitsResort)

        val expDinner = ExpenseEntity("demo_exp_dinner", "demo_grp_goa", "Seafood Dinner", 1500.0, "Food", "demo_member_rahul")
        val splitsDinner = listOf(
            ExpenseSplitEntity("demo_exp_dinner_c", "demo_exp_dinner", "demo_user_chanakya_goa", 500.0),
            ExpenseSplitEntity("demo_exp_dinner_r", "demo_exp_dinner", "demo_member_rahul", 500.0),
            ExpenseSplitEntity("demo_exp_dinner_p", "demo_exp_dinner", "demo_member_priya", 500.0)
        )
        dao.insertExpense(expDinner)
        dao.insertSplits(splitsDinner)

        val expCab = ExpenseEntity("demo_exp_cab", "demo_grp_goa", "Airport Cab", 1200.0, "Transport", "demo_member_priya")
        val splitsCab = listOf(
            ExpenseSplitEntity("demo_exp_cab_c", "demo_exp_cab", "demo_user_chanakya_goa", 400.0),
            ExpenseSplitEntity("demo_exp_cab_r", "demo_exp_cab", "demo_member_rahul", 400.0),
            ExpenseSplitEntity("demo_exp_cab_p", "demo_exp_cab", "demo_member_priya", 400.0)
        )
        dao.insertExpense(expCab)
        dao.insertSplits(splitsCab)

        // 3. Group 2: Flat Expenses 🏠
        val groupFlat = GroupEntity(
            id = "demo_grp_flat",
            name = "Flat Expenses 🏠",
            description = "Monthly bills, groceries, and shared rent for Flat 302.",
            createdBy = "demo_user_chanakya",
            upiId = "chanakya@upi"
        )
        dao.insertGroup(groupFlat)

        val membersFlat = listOf(
            MemberEntity("demo_user_chanakya_flat", "demo_grp_flat", "Chanakya", "demo@splitsmart.app", "9876543210", "chanakya@upi"),
            MemberEntity("demo_member_sumit", "demo_grp_flat", "Sumit", "sumit@gmail.com", "9876543213", "sumit@upi"),
            MemberEntity("demo_member_neha", "demo_grp_flat", "Neha", "neha@gmail.com", "9876543214", "neha@upi")
        )
        dao.insertMembers(membersFlat)

        // Expenses for Flat
        val rentDate = System.currentTimeMillis() - (31L * 24 * 60 * 60 * 1000)
        val expRent = ExpenseEntity("demo_exp_rent", "demo_grp_flat", "Monthly Rent", 12000.0, "Other", "demo_user_chanakya_flat", date = rentDate, isRecurring = true)
        val splitsRent = listOf(
            ExpenseSplitEntity("demo_exp_rent_c", "demo_exp_rent", "demo_user_chanakya_flat", 4000.0),
            ExpenseSplitEntity("demo_exp_rent_s", "demo_exp_rent", "demo_member_sumit", 4000.0),
            ExpenseSplitEntity("demo_exp_rent_n", "demo_exp_rent", "demo_member_neha", 4000.0)
        )
        dao.insertExpense(expRent)
        dao.insertSplits(splitsRent)

        val expWifi = ExpenseEntity("demo_exp_wifi", "demo_grp_flat", "Wi-Fi Bill", 900.0, "Utilities", "demo_member_sumit")
        val splitsWifi = listOf(
            ExpenseSplitEntity("demo_exp_wifi_c", "demo_exp_wifi", "demo_user_chanakya_flat", 300.0),
            ExpenseSplitEntity("demo_exp_wifi_s", "demo_exp_wifi", "demo_member_sumit", 300.0),
            ExpenseSplitEntity("demo_exp_wifi_n", "demo_exp_wifi", "demo_member_neha", 300.0)
        )
        dao.insertExpense(expWifi)
        dao.insertSplits(splitsWifi)

        val expGroceries = ExpenseEntity("demo_exp_groceries", "demo_grp_flat", "Organic Groceries", 1800.0, "Food", "demo_member_neha")
        val splitsGroceries = listOf(
            ExpenseSplitEntity("demo_exp_groceries_c", "demo_exp_groceries", "demo_user_chanakya_flat", 600.0),
            ExpenseSplitEntity("demo_exp_groceries_s", "demo_exp_groceries", "demo_member_sumit", 600.0),
            ExpenseSplitEntity("demo_exp_groceries_n", "demo_exp_groceries", "demo_member_neha", 600.0)
        )
        dao.insertExpense(expGroceries)
        dao.insertSplits(splitsGroceries)

        // 4. Group 3: Office Lunch 🥪
        val groupOffice = GroupEntity(
            id = "demo_grp_office",
            name = "Office Lunch 🥪",
            description = "Quick splitting for daily corporate lunches & coffee breaks.",
            createdBy = "demo_user_chanakya",
            upiId = "chanakya@upi"
        )
        dao.insertGroup(groupOffice)

        val membersOffice = listOf(
            MemberEntity("demo_user_chanakya_office", "demo_grp_office", "Chanakya", "demo@splitsmart.app", "9876543210", "chanakya@upi"),
            MemberEntity("demo_member_amit", "demo_grp_office", "Amit", "amit@gmail.com", "9876543215", "amit@upi")
        )
        dao.insertMembers(membersOffice)

        // Expenses for Office
        val expPizza = ExpenseEntity("demo_exp_pizza", "demo_grp_office", "Pizza Party", 1500.0, "Food", "demo_user_chanakya_office")
        val splitsPizza = listOf(
            ExpenseSplitEntity("demo_exp_pizza_c", "demo_exp_pizza", "demo_user_chanakya_office", 750.0),
            ExpenseSplitEntity("demo_exp_pizza_a", "demo_exp_pizza", "demo_member_amit", 750.0)
        )
        dao.insertExpense(expPizza)
        dao.insertSplits(splitsPizza)

        val expCoffee = ExpenseEntity("demo_exp_coffee", "demo_grp_office", "Specialty Coffee", 500.0, "Food", "demo_member_amit")
        val splitsCoffee = listOf(
            ExpenseSplitEntity("demo_exp_coffee_c", "demo_exp_coffee", "demo_user_chanakya_office", 250.0),
            ExpenseSplitEntity("demo_exp_coffee_a", "demo_exp_coffee", "demo_member_amit", 250.0)
        )
        dao.insertExpense(expCoffee)
        dao.insertSplits(splitsCoffee)

        // 5. Seed Activities
        dao.insertActivity(ActivityEntity("demo_act_1", "demo_grp_goa", "Goa Trip Created", "Chanakya created the group \"Goa Trip 🏖️\"", type = "GROUP"))
        dao.insertActivity(ActivityEntity("demo_act_2", "demo_grp_goa", "Resort Logged", "Chanakya added \"Resort Booking\" of ₹5,800.00", type = "EXPENSE"))
        dao.insertActivity(ActivityEntity("demo_act_3", "demo_grp_flat", "Flat Expenses Created", "Chanakya created the group \"Flat Expenses 🏠\"", type = "GROUP"))
        dao.insertActivity(ActivityEntity("demo_act_4", "demo_grp_flat", "Monthly Rent Logged", "Chanakya added \"Monthly Rent\" of ₹12,000.00", type = "EXPENSE"))
    }
}
