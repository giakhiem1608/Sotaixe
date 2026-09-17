import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

# Update elevation in GoalSection
content = re.sub(
    r'fun GoalSection(.*?)\n(.*?)elevation = CardDefaults\.cardElevation\(defaultElevation = 1\.dp\)',
    r'fun GoalSection\1\n\2elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)',
    content,
    flags=re.DOTALL | re.MULTILINE
)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
