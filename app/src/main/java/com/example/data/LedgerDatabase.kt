package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [RevenueSource::class, ExpenseCategory::class, RevenueEntry::class, ExpenseEntry::class, Goal::class],
    version = 1,
    exportSchema = false
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun ledgerDao(): LedgerDao

    companion object {
        @Volatile
        private var INSTANCE: LedgerDatabase? = null

        fun getDatabase(context: Context): LedgerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "ledger_database"
                )
                .addCallback(LedgerDatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private class LedgerDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.ledgerDao())
                    }
                }
            }
            
            suspend fun populateDatabase(dao: LedgerDao) {
                val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                val ts1 = format.parse("2026-09-02 12:00")?.time ?: 0L
                val ts2 = format.parse("2026-09-02 18:00")?.time ?: 0L
                
                // Default revenue sources
                dao.insertRevenueSource(RevenueSource(name = "Xanh SM", colorHex = "#8B5CF6", isDefault = true))
                dao.insertRevenueSource(RevenueSource(name = "Grab", colorHex = "#16A34A", isDefault = true))
                dao.insertRevenueSource(RevenueSource(name = "Khách ngoài", colorHex = "#3B82F6", isDefault = true))
                
                // Default expense categories
                dao.insertExpenseCategory(ExpenseCategory(name = "Sạc xe", iconName = "ev_station", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Ăn uống", iconName = "restaurant", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Gửi xe", iconName = "local_parking", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Cầu đường", iconName = "add_road", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Rửa xe", iconName = "local_car_wash", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Bảo dưỡng", iconName = "build", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Điện thoại / 4G", iconName = "phone_android", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Khác", iconName = "more_horiz", isDefault = true))

                // Mock data for 2026-09-02
                dao.insertRevenueEntry(RevenueEntry(sourceId = 2, amount = 550000, trips = 12, distanceKm = 85f, durationHrs = 4.5f, note = "", dateString = "2026-09-02", timestamp = ts1))
                dao.insertRevenueEntry(RevenueEntry(sourceId = 3, amount = 200000, trips = 1, distanceKm = 15f, durationHrs = 0.5f, note = "", dateString = "2026-09-02", timestamp = ts2))
                
                dao.insertExpenseEntry(ExpenseEntry(categoryId = 1, amount = 150000, note = "Xăng", dateString = "2026-09-02", timestamp = ts1))
                dao.insertExpenseEntry(ExpenseEntry(categoryId = 2, amount = 450000, note = "Ăn uống", dateString = "2026-09-02", timestamp = ts2))
            }
        }
    }
}
