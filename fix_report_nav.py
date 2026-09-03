import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun ReportScreen(viewModel: LedgerViewModel)", "fun ReportScreen(viewModel: LedgerViewModel, onNavigateToHistory: () -> Unit = {})")
content = content.replace("onClick = { /* TODO: Navigation to history missing KM */ }", "onClick = { viewModel.activateMissingKmFilter(); onNavigateToHistory() }")

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)

