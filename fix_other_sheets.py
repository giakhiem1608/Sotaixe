sheets = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageSourcesSheet(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sources by viewModel.allRevenueSources.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Quản lý nguồn thu", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Thêm") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (sources.isEmpty()) {
                Text("Chưa có nguồn thu", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sources) { source ->
                        val color = try { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(source.colorHex)) } catch(e: Exception) { androidx.compose.ui.graphics.Color.Gray }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(source.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = source.isActive,
                                    onCheckedChange = { isActive ->
                                        if (isActive) {
                                            viewModel.updateRevenueSource(source.copy(isActive = true))
                                        } else {
                                            viewModel.hideRevenueSource(source)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var colorHex by remember { mutableStateOf("#3B82F6") } // Default blue
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm nguồn thu") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên nguồn thu") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Màu sắc")
                    Spacer(modifier = Modifier.height(8.dp))
                    val colors = listOf("#E78300", "#3B82F6", "#14B8A6", "#8B5CF6", "#EF4444", "#10B981")
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        colors.forEach { hex ->
                            val c = try { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex)) } catch(e: Exception) { androidx.compose.ui.graphics.Color.Gray }
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .clickable { colorHex = hex }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (colorHex == hex) {
                                    Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(androidx.compose.ui.graphics.Color.White))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addRevenueSource(name, colorHex)
                        showAddDialog = false
                    }
                }) { Text("LƯU") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("HỦY") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesSheet(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val categories by viewModel.allExpenseCategories.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Danh mục chi phí", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, contentDescription = "Thêm") }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (categories.isEmpty()) {
                Text("Chưa có danh mục", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(category.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = category.isActive,
                                    onCheckedChange = { isActive ->
                                        if (isActive) {
                                            viewModel.updateExpenseCategory(category.copy(isActive = true))
                                        } else {
                                            viewModel.hideExpenseCategory(category)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm danh mục chi phí") },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên danh mục") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (name.isNotBlank()) {
                        viewModel.addExpenseCategory(name, "attach_money")
                        showAddDialog = false
                    }
                }) { Text("LƯU") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("HỦY") }
            }
        )
    }
}
"""
with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "a") as f:
    f.write(sheets)
