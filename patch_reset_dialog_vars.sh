sed -i '/var showRestoreConfirmDialog/a \
    var showResetConfirmDialog1 by remember { mutableStateOf(false) }\n    var showResetConfirmDialog2 by remember { mutableStateOf(false) }' app/src/main/java/com/example/ui/screens/OtherScreen.kt
