import re

new_content = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesSheet(viewModel: LedgerViewModel, primaryColor: Color, onDismiss: () -> Unit) {
    val categories by viewModel.allExpenseCategories.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var nameStr by remember { mutableStateOf("") }
    var editingCategory by remember { mutableStateOf<com.example.data.ExpenseCategory?>(null) }
    var editNameStr by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = CardSurface) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp).imePadding()) {
            Text("Quản lý Danh mục", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState())) {
                categories.forEach { category ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        if (editingCategory?.id == category.id) {
                            OutlinedTextField(
                                value = editNameStr,
                                onValueChange = { editNameStr = it },
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                if (editNameStr.isNotBlank()) {
                                    viewModel.updateExpenseCategory(category.copy(name = editNameStr))
                                }
                                editingCategory = null
                            }) { Icon(Icons.Default.Check, contentDescription = "Lưu", tint = primaryColor) }
                            IconButton(onClick = { editingCategory = null }) { Icon(Icons.Default.Close, contentDescription = "Hủy", tint = Color.Gray) }
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
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Thêm danh mục mới", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(value = nameStr, onValueChange = { nameStr = it }, label = { Text("Tên danh mục") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { 
                if (nameStr.isNotBlank()) {
                    viewModel.addExpenseCategory(nameStr, "more_horiz")
                    nameStr = ""
                }
            }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = ButtonDefaults.buttonColors(containerColor = primaryColor)) { Text("THÊM") }
        }
    }
}"""

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# Replace ManageCategoriesSheet
pattern = r'@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun ManageCategoriesSheet.*?^}'
content = re.sub(pattern, new_content, content, flags=re.MULTILINE | re.DOTALL)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)

print("Replaced ManageCategoriesSheet")
