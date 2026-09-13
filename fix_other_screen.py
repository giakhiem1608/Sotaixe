import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

content = content.replace("ThemeSettingsSheet(viewModel = viewModel, onDismiss = { showThemeSettings = false })", "ThemeSettingsSheet(themeManager = viewModel.themeManager, onDismiss = { showThemeSettings = false })")

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)

