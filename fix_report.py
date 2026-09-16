with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

old_dialog = """    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (currentGoal == null) "Thêm mục tiêu" else "Cập nhật mục tiêu") },
        text = {
            var goalType by remember { mutableStateOf(currentGoal?.type ?: "REVENUE") }
            var amountStr by remember { mutableStateOf(if (currentGoal != null && currentGoal.amount > 0) currentGoal.amount.toString() else "") }
            
            Column {"""

new_dialog = """    var goalType by remember { mutableStateOf(currentGoal?.type ?: "REVENUE") }
    var amountStr by remember { mutableStateOf(if (currentGoal != null && currentGoal.amount > 0) currentGoal.amount.toString() else "") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (currentGoal == null) "Thêm mục tiêu" else "Cập nhật mục tiêu") },
        text = {
            Column {"""

content = content.replace(old_dialog, new_dialog)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
