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
                // Default revenue sources
                dao.insertRevenueSource(RevenueSource(name = "Xanh SM", colorHex = "#00BFA5", isDefault = true))
                dao.insertRevenueSource(RevenueSource(name = "Grab", colorHex = "#00C853", isDefault = true))
                dao.insertRevenueSource(RevenueSource(name = "Khách ngoài", colorHex = "#2979FF", isDefault = true))
                
                // Default expense categories
                dao.insertExpenseCategory(ExpenseCategory(name = "Sạc xe", iconName = "ev_station", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Ăn uống", iconName = "restaurant", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Gửi xe", iconName = "local_parking", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Cầu đường", iconName = "add_road", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Rửa xe", iconName = "local_car_wash", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Bảo dưỡng", iconName = "build", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Điện thoại / 4G", iconName = "phone_android", isDefault = true))
                dao.insertExpenseCategory(ExpenseCategory(name = "Khác", iconName = "more_horiz", isDefault = true))
            }
        }
    }
}
