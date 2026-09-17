import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

new_func = """@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesSheet(viewModel: LedgerViewModel, primaryColor: Color, onDismiss: () -> Unit) {
    val categories by viewModel.allExpenseCategories.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var nameStr by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<com.example.data.ExpenseCategory?>(null) }
    var editNameStr by remember { mutableStateOf("") }

    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    val isImeVisible = androidx.compose.foundation.layout.WindowInsets.ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0

    androidx.activity.compose.BackHandler(enabled = isImeVisible) {
        keyboardController?.hide()
        focusManager.clearFocus()
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = CardSurface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp, top = 8.dp).imePadding()) {
            Text("Quản lý Danh mục", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState())) {
                categories.forEach { category ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        if (editingCategory?.id == category.id) {
                            OutlinedTextField(
                                value = editNameStr,
                                onValueChange = { editNameStr = it },
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = {
                                    if (editNameStr.isNotBlank()) {
                                        viewModel.updateExpenseCategory(category.copy(name = editNameStr))
                                    }
                                    editingCategory = null
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                })
                            )
                            IconButton(onClick = {
                                if (editNameStr.isNotBlank()) {
                                    viewModel.updateExpenseCategory(category.copy(name = editNameStr))
                                }
                                editingCategory = null
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }) { Icon(Icons.Default.Check, contentDescription = "Lưu", tint = primaryColor) }
                            IconButton(onClick = { 
                                editingCategory = null 
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }) { Icon(Icons.Default.Close, contentDescription = "Hủy", tint = Color.Gray) }
                        } else {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(category.name, fontWeight = FontWeight.Medium, color = if (category.isActive) Color(0xFF0F172A) else Color(0xFF94A3B8))
                                if (!category.isActive) {
                                    Text("Đã ẩn", style = MaterialTheme.typography.bodySmall, color = Color(0xFFEF4444))
                                }
                            }
                            Row {
                                if (!category.isDefault) {
                                    IconButton(onClick = {
                                        editingCategory = category
                                        editNameStr = category.name
                                    }) { Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color(0xFF64748B)) }
                                }
                                IconButton(onClick = { 
                                    if (category.isActive) {
                                        viewModel.updateExpenseCategory(category.copy(isActive = false))
                                    } else {
                                        viewModel.updateExpenseCategory(category.copy(isActive = true))
                                    }
                                }) { 
                                    Icon(if (category.isActive) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = "Ẩn/Hiện", tint = Color(0xFF64748B)) 
                                }
                                if (!category.isDefault) {
                                    IconButton(onClick = { viewModel.deleteExpenseCategory(category) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = CardBorder)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("Thêm danh mục mới", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value = nameStr, 
                onValueChange = { nameStr = it }, 
                label = { Text("Tên danh mục") }, 
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = {
                    if (nameStr.isNotBlank()) {
                        viewModel.addExpenseCategory(nameStr, "more_horiz")
                        nameStr = ""
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                })
            )
            Button(
                onClick = { 
                    if (nameStr.isNotBlank()) {
                        viewModel.addExpenseCategory(nameStr, "more_horiz")
                        nameStr = ""
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                }, 
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp), 
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) { Text("THÊM DANH MỤC") }
        }
    }
}"""

old_func_match = re.search(r'fun ManageCategoriesSheet.*?^}', content, re.MULTILINE | re.DOTALL)
if old_func_match:
    content = content[:old_func_match.start()] + new_func + content[old_func_match.end():]
    # In case there's another closing brace (which there is for ModalBottomSheet), we need to replace carefully.
else:
    print("Not found regex.")

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
