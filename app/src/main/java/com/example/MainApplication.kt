package com.example

import android.app.Application
import com.example.data.LedgerDatabase
import com.example.data.LedgerRepository

class MainApplication : Application() {
    val database by lazy { LedgerDatabase.getDatabase(this) }
    val repository by lazy { LedgerRepository(database.ledgerDao()) }
}
