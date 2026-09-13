import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

target = r"""    val totalRev = revenues\.sumOf \{ it\.amount \}\n    val totalExp = expenses\.sumOf \{ it\.amount \}\n    val netIncome = totalRev - totalExp\n    val totalTrips = revenues\.sumOf \{ it\.trips \}"""
replace = """    val totalRev = revenues.sumOf { it.amount }
    val totalTip = revenues.sumOf { it.tipAmount ?: 0L }
    val totalExp = expenses.sumOf { it.amount }
    val netIncome = totalRev + totalTip - totalExp
    val totalTrips = revenues.sumOf { it.trips }"""
content = re.sub(target, replace, content)

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)

