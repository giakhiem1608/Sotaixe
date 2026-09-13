import re

with open("app/src/main/java/com/example/data/LedgerDatabase.kt", "r") as f:
    content = f.read()

content = content.replace("version = 1,", "version = 2,")
content = content.replace("import androidx.sqlite.db.SupportSQLiteDatabase", "import androidx.room.migration.Migration\nimport androidx.sqlite.db.SupportSQLiteDatabase")

migration = """
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE revenue_entries ADD COLUMN tipAmount INTEGER DEFAULT 0")
            }
        }
        
        fun getDatabase"""
content = content.replace("fun getDatabase", migration)

builder = """                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "ledger_database"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(LedgerDatabaseCallback())"""
content = content.replace("""                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LedgerDatabase::class.java,
                    "ledger_database"
                )
                .addCallback(LedgerDatabaseCallback())""", builder)

with open("app/src/main/java/com/example/data/LedgerDatabase.kt", "w") as f:
    f.write(content)

