import re

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("fun updateCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String) {", "fun updateCardColors(bgHex: String, incomeHex: String, revHex: String, expHex: String, tipHex: String = \"\") {")

content = content.replace("prefs[EXP_COLOR_KEY] = expHex\n            }", "prefs[EXP_COLOR_KEY] = expHex\n                if (tipHex.isNotEmpty()) prefs[TIP_COLOR_KEY] = tipHex else prefs.remove(TIP_COLOR_KEY)\n            }")

with open("app/src/main/java/com/example/ui/viewmodels/LedgerViewModel.kt", "w") as f:
    f.write(content)

