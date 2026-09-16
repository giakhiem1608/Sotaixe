with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

import re

old_add_rev = """    fun addRevenue(sourceId: Int, amount: Long, tipAmount: Long?, trips: Int, duration: Float?, distance: Float?, note: String) {
        viewModelScope.launch {
            val dateStr = FormatUtils.formatDbDate(_currentDate.value)
            repository.insertRevenueEntry(
                RevenueEntry(
                    sourceId = sourceId,
                    amount = amount,
                    trips = trips,
                    durationHrs = duration,
                    distanceKm = distance,
                    note = note,
                    timestamp = _currentDate.value,
                    dateString = dateStr
                )
            )
        }
    }"""

new_add_rev = """    fun addRevenue(sourceId: Int, amount: Long, tipAmount: Long?, trips: Int, duration: Float?, distance: Float?, note: String) {
        viewModelScope.launch {
            val dateStr = FormatUtils.formatDbDate(_currentDate.value)
            repository.insertRevenueEntry(
                RevenueEntry(
                    sourceId = sourceId,
                    amount = amount,
                    tipAmount = tipAmount ?: 0L,
                    trips = trips,
                    durationHrs = duration,
                    distanceKm = distance,
                    note = note,
                    timestamp = _currentDate.value,
                    dateString = dateStr
                )
            )
        }
    }"""

content = content.replace(old_add_rev, new_add_rev)
with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)
