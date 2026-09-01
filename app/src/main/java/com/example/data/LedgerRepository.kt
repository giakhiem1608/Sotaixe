package com.example.data

import kotlinx.coroutines.flow.Flow

class LedgerRepository(private val dao: LedgerDao) {
    // Revenue Sources
    val activeRevenueSources: Flow<List<RevenueSource>> = dao.getActiveRevenueSources()

    suspend fun insertRevenueSource(source: RevenueSource) {
        dao.insertRevenueSource(source)
    }

    suspend fun updateRevenueSource(source: RevenueSource) {
        dao.updateRevenueSource(source)
    }

    // Expense Categories
    val activeExpenseCategories: Flow<List<ExpenseCategory>> = dao.getActiveExpenseCategories()

    suspend fun insertExpenseCategory(category: ExpenseCategory) {
        dao.insertExpenseCategory(category)
    }

    suspend fun updateExpenseCategory(category: ExpenseCategory) {
        dao.updateExpenseCategory(category)
    }

    // Revenue Entries
    fun getRevenueEntriesByDate(dateString: String): Flow<List<RevenueEntry>> {
        return dao.getRevenueEntriesByDate(dateString)
    }

    fun getRevenueEntriesByMonth(monthPrefix: String): Flow<List<RevenueEntry>> {
        return dao.getRevenueEntriesByMonth(monthPrefix)
    }

    suspend fun insertRevenueEntry(entry: RevenueEntry) {
        dao.insertRevenueEntry(entry)
    }

    suspend fun updateRevenueEntry(entry: RevenueEntry) {
        dao.updateRevenueEntry(entry)
    }

    suspend fun deleteRevenueEntry(id: Int) {
        dao.deleteRevenueEntry(id)
    }

    // Expense Entries
    fun getExpenseEntriesByDate(dateString: String): Flow<List<ExpenseEntry>> {
        return dao.getExpenseEntriesByDate(dateString)
    }

    fun getExpenseEntriesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>> {
        return dao.getExpenseEntriesByMonth(monthPrefix)
    }

    suspend fun insertExpenseEntry(entry: ExpenseEntry) {
        dao.insertExpenseEntry(entry)
    }

    suspend fun updateExpenseEntry(entry: ExpenseEntry) {
        dao.updateExpenseEntry(entry)
    }

    suspend fun deleteExpenseEntry(id: Int) {
        dao.deleteExpenseEntry(id)
    }

    // Goals
    fun getGoalByMonth(monthString: String): Flow<Goal?> {
        return dao.getGoalByMonth(monthString)
    }

    suspend fun insertGoal(goal: Goal) {
        dao.insertGoal(goal)
    }

    // Statistics
    fun getTotalRevenueByDate(dateString: String): Flow<Long?> {
        return dao.getTotalRevenueByDate(dateString)
    }

    fun getTotalExpenseByDate(dateString: String): Flow<Long?> {
        return dao.getTotalExpenseByDate(dateString)
    }

    fun getTotalTripsByDate(dateString: String): Flow<Int?> {
        return dao.getTotalTripsByDate(dateString)
    }

    fun getTotalDurationByDate(dateString: String): Flow<Float?> {
        return dao.getTotalDurationByDate(dateString)
    }

    fun getTotalDistanceByDate(dateString: String): Flow<Float?> {
        return dao.getTotalDistanceByDate(dateString)
    }

    suspend fun getAllRevenueSources() = dao.getAllRevenueSources()
    suspend fun getAllExpenseCategories() = dao.getAllExpenseCategories()
    suspend fun getAllRevenueEntries() = dao.getAllRevenueEntries()
    suspend fun getAllExpenseEntries() = dao.getAllExpenseEntries()
    suspend fun getAllGoals() = dao.getAllGoals()

    suspend fun clearRevenueSources() = dao.clearRevenueSources()
    suspend fun clearExpenseCategories() = dao.clearExpenseCategories()
    suspend fun clearRevenueEntries() = dao.clearRevenueEntries()
    suspend fun clearExpenseEntries() = dao.clearExpenseEntries()
    suspend fun clearGoals() = dao.clearGoals()
}
