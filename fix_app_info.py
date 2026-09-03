import re
with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

target = "    if (showManageCategories) {\n        ManageCategoriesSheet(viewModel = viewModel, onDismiss = { showManageCategories = false })\n    }"
replacement = "    if (showManageCategories) {\n        ManageCategoriesSheet(viewModel = viewModel, onDismiss = { showManageCategories = false })\n    }\n    \n    if (showAppInfoSheet) {\n        AppInfoSheet(onDismiss = { showAppInfoSheet = false })\n    }"
content = content.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
