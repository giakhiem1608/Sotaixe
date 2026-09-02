sed -i 's/class LedgerRepository(private val dao: LedgerDao) {/import android.content.SharedPreferences\n\nclass LedgerRepository(private val dao: LedgerDao, val sharedPreferences: SharedPreferences? = null) {/g' app/src/main/java/com/example/data/LedgerRepository.kt

sed -i 's/val repository by lazy { LedgerRepository(database.ledgerDao()) }/val repository by lazy { LedgerRepository(database.ledgerDao(), getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)) }/g' app/src/main/java/com/example/MainApplication.kt

