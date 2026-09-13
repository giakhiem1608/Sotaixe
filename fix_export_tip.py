import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

target = r"""                val totalRev = revEntries\.sumOf \{ it\.amount \}\n                val totalExp = expEntries\.sumOf \{ it\.amount \}\n                val totalTrips = revEntries\.sumOf \{ it\.trips \}\n                \n                wsOverview\.value\(2, 0, "Tong doanh thu"\)\n                wsOverview\.value\(2, 1, totalRev\)\n                wsOverview\.value\(3, 0, "Tong chi phi"\)\n                wsOverview\.value\(3, 1, totalExp\)\n                wsOverview\.value\(4, 0, "Thu nhap rong"\)\n                wsOverview\.value\(4, 1, totalRev - totalExp\)\n                wsOverview\.value\(5, 0, "Tong so cuoc"\)\n                wsOverview\.value\(5, 1, totalTrips\)"""
replace = """                val totalRev = revEntries.sumOf { it.amount }
                val totalExp = expEntries.sumOf { it.amount }
                val totalTip = revEntries.sumOf { it.tipAmount ?: 0L }
                val totalTrips = revEntries.sumOf { it.trips }
                
                wsOverview.value(2, 0, "Tong doanh thu")
                wsOverview.value(2, 1, totalRev)
                wsOverview.value(3, 0, "Tong Tip")
                wsOverview.value(3, 1, totalTip)
                wsOverview.value(4, 0, "Tong tien nhan")
                wsOverview.value(4, 1, totalRev + totalTip)
                wsOverview.value(5, 0, "Tong chi phi")
                wsOverview.value(5, 1, totalExp)
                wsOverview.value(6, 0, "Thu nhap rong")
                wsOverview.value(6, 1, totalRev + totalTip - totalExp)
                wsOverview.value(7, 0, "Tong so cuoc")
                wsOverview.value(7, 1, totalTrips)"""
content = re.sub(target, replace, content)

target_header = r"""                val wsRev = wb\.newWorksheet\("Doanh thu"\)\n                val revHeaders = listOf\("Ngay", "Nguon", "So tien", "So cuoc", "Km", "Gio chay", "Ghi chu"\)"""
replace_header = """                val wsRev = wb.newWorksheet("Doanh thu")
                val revHeaders = listOf("Ngay", "Nguon", "So tien", "Tien Tip", "So cuoc", "Km", "Gio chay", "Ghi chu")"""
content = re.sub(target_header, replace_header, content)

target_row = r"""                    wsRev\.value\(r, 2, rev\.amount\)\n                    wsRev\.value\(r, 3, rev\.trips\)\n                    if \(rev\.distanceKm != null\) wsRev\.value\(r, 4, rev\.distanceKm\)\n                    if \(rev\.durationHrs != null\) wsRev\.value\(r, 5, rev\.durationHrs\)\n                    wsRev\.value\(r, 6, rev\.note\)"""
replace_row = """                    wsRev.value(r, 2, rev.amount)
                    wsRev.value(r, 3, rev.tipAmount ?: 0L)
                    wsRev.value(r, 4, rev.trips)
                    if (rev.distanceKm != null) wsRev.value(r, 5, rev.distanceKm)
                    if (rev.durationHrs != null) wsRev.value(r, 6, rev.durationHrs)
                    wsRev.value(r, 7, rev.note)"""
content = re.sub(target_row, replace_row, content)

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)
