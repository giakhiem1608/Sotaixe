import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("fun addRevenue(sourceId: Int, amount: Long, trips: Int, duration: Float?, distance: Float?, note: String)", "fun addRevenue(sourceId: Int, amount: Long, tipAmount: Long?, trips: Int, duration: Float?, distance: Float?, note: String)")

target = """                amount = amount,
                trips = trips,
                durationHrs = duration,
                distanceKm = distance,"""
replace = """                amount = amount,
                tipAmount = tipAmount,
                trips = trips,
                durationHrs = duration,
                distanceKm = distance,"""
content = content.replace(target, replace)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)
