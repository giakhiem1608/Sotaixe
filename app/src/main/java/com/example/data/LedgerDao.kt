package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LedgerDao {
    // Revenue Sources
    @Query("SELECT * FROM revenue_sources")
    fun getAllRevenueSourcesFlow(): Flow<List<RevenueSource>>
    @Query("SELECT * FROM revenue_sources WHERE isActive = 1 ORDER BY isDefault DESC, name ASC")
    fun getActiveRevenueSources(): Flow<List<RevenueSource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevenueSource(source: RevenueSource)

    @Update
    suspend fun updateRevenueSource(source: RevenueSource)

    // Expense Categories
    @Query("SELECT * FROM expense_categories")
    fun getAllExpenseCategoriesFlow(): Flow<List<ExpenseCategory>>

    @Query("SELECT * FROM expense_categories WHERE isActive = 1 ORDER BY isDefault DESC, name ASC")
    fun getActiveExpenseCategories(): Flow<List<ExpenseCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseCategory(category: ExpenseCategory)

    @Update
    suspend fun updateExpenseCategory(category: ExpenseCategory)

    // Revenue Entries
    @Query("SELECT * FROM revenue_entries WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getRevenueEntriesByDate(dateString: String): Flow<List<RevenueEntry>>

    @Query("SELECT * FROM revenue_entries WHERE dateString LIKE :monthPrefix || '%'")
    fun getRevenueEntriesByMonth(monthPrefix: String): Flow<List<RevenueEntry>>

    @Insert
    suspend fun insertRevenueEntry(entry: RevenueEntry)

    @Update
    suspend fun updateRevenueEntry(entry: RevenueEntry)

    @Query("SELECT COUNT(*) FROM revenue_entries WHERE sourceId = :sourceId")
    suspend fun countRevenueEntries(sourceId: Int): Int

    @Delete
    suspend fun deleteRevenueSource(source: RevenueSource)

    @Query("DELETE FROM revenue_entries WHERE id = :id")
    suspend fun deleteRevenueEntry(id: Int)
    // Expense Entries
    @Query("SELECT * FROM expense_entries WHERE dateString = :dateString ORDER BY timestamp DESC")
    fun getExpenseEntriesByDate(dateString: String): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries WHERE dateString LIKE :monthPrefix || '%'")
    fun getExpenseEntriesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>>

    @Insert
    suspend fun insertExpenseEntry(entry: ExpenseEntry)

    @Update
    suspend fun updateExpenseEntry(entry: ExpenseEntry)

    @Query("SELECT COUNT(*) FROM expense_entries WHERE categoryId = :categoryId")
    suspend fun countExpenseEntries(categoryId: Int): Int

    @Delete
    suspend fun deleteExpenseCategory(category: ExpenseCategory)

    @Query("DELETE FROM expense_entries WHERE id = :id")
    suspend fun deleteExpenseEntry(id: Int)
    @Query("SELECT * FROM goals WHERE monthString = :monthString")
    fun getGoalByMonth(monthString: String): Flow<Goal?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)
    @Delete
    suspend fun deleteGoal(goal: Goal)

    // Statistics for today
    @Query("SELECT SUM(amount) FROM revenue_entries WHERE dateString = :dateString")
    fun getTotalRevenueByDate(dateString: String): Flow<Long?>

    @Query("SELECT SUM(amount) FROM expense_entries WHERE dateString = :dateString")
    fun getTotalExpenseByDate(dateString: String): Flow<Long?>

    @Query("SELECT SUM(trips) FROM revenue_entries WHERE dateString = :dateString")
    fun getTotalTripsByDate(dateString: String): Flow<Int?>

    @Query("SELECT SUM(durationHrs) FROM revenue_entries WHERE dateString = :dateString")
    fun getTotalDurationByDate(dateString: String): Flow<Float?>

    @Query("SELECT SUM(distanceKm) FROM revenue_entries WHERE dateString = :dateString")
    fun getTotalDistanceByDate(dateString: String): Flow<Float?>

    // Get ALL data for Backup
    @Query("SELECT * FROM revenue_sources")
    suspend fun getAllRevenueSources(): List<RevenueSource>

    @Query("SELECT * FROM expense_categories")
    suspend fun getAllExpenseCategories(): List<ExpenseCategory>

    @Query("SELECT * FROM revenue_entries")
    suspend fun getAllRevenueEntries(): List<RevenueEntry>

    @Query("SELECT * FROM expense_entries")
    suspend fun getAllExpenseEntries(): List<ExpenseEntry>

    @Query("SELECT * FROM goals")
    suspend fun getAllGoals(): List<Goal>

    // Delete ALL data for Restore
    @Query("DELETE FROM revenue_sources")
    suspend fun clearRevenueSources()

    @Query("DELETE FROM expense_categories")
    suspend fun clearExpenseCategories()

    @Query("DELETE FROM revenue_entries")
    suspend fun clearRevenueEntries()

    @Query("DELETE FROM expense_entries")
    suspend fun clearExpenseEntries()

    @Query("DELETE FROM goals")
    suspend fun clearGoals()
}
