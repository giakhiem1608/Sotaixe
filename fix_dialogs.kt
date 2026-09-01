@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ManageSourcesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val sources by viewModel.activeRevenueSources.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSource by remember { mutableStateOf<RevenueSource?>(null) }
    
    val colorOptions = listOf("#2979FF", "#00C853", "#FF3D00", "#FFC107", "#9C27B0", "#E91E63", "#00BCD4", "#607D8B")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nguồn thu") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(sources) { source ->
                        ListItem(
                            headlineContent = { Text(source.name, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable { editingSource = source },
                            trailingContent = {
                                IconButton(onClick = { viewModel.hideRevenueSource(source) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Ẩn", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showAddDialog = true }) {
                Text("THÊM MỚI")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ĐÓNG") }
        }
    )

    if (showAddDialog || editingSource != null) {
        var name by remember { mutableStateOf(editingSource?.name ?: "") }
        var selectedColor by remember { mutableStateOf(editingSource?.colorHex ?: colorOptions[0]) }
        
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingSource = null
            },
            title = { Text(if (editingSource != null) "Sửa nguồn thu" else "Thêm nguồn thu") },
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
                    Text("Chọn màu:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorOptions.forEach { hex ->
                            val color = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex))
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .androidx.compose.foundation.shape.CircleShape.let { shape ->
                                        if (selectedColor == hex) {
                                            androidx.compose.foundation.border(2.dp, MaterialTheme.colorScheme.onSurface, shape)
                                        } else {
                                            this
                                        }
                                    }
                                    .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.CircleShape)
                                    .androidx.compose.foundation.background(color)
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingSource != null) {
                            viewModel.updateRevenueSource(editingSource!!.copy(name = name, colorHex = selectedColor))
                        } else {
                            viewModel.addRevenueSource(name, selectedColor)
                        }
                        showAddDialog = false
                        editingSource = null
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("LƯU")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingSource = null
                }) { Text("HỦY") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun ManageCategoriesDialog(viewModel: LedgerViewModel, onDismiss: () -> Unit) {
    val categories by viewModel.activeExpenseCategories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    
    val iconOptions = listOf(
        "battery_charging_full" to Icons.Filled.BatteryChargingFull,
        "restaurant" to Icons.Filled.Restaurant,
        "local_parking" to Icons.Filled.LocalParking,
        "add_road" to Icons.Filled.AddRoad,
        "local_car_wash" to Icons.Filled.LocalCarWash,
        "build" to Icons.Filled.Build,
        "phone_android" to Icons.Filled.PhoneAndroid,
        "more_horiz" to Icons.Filled.MoreHoriz
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Danh mục chi phí") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(categories) { category ->
                        ListItem(
                            headlineContent = { Text(category.name, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.clickable { editingCategory = category },
                            trailingContent = {
                                IconButton(onClick = { viewModel.hideExpenseCategory(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Ẩn", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showAddDialog = true }) {
                Text("THÊM MỚI")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("ĐÓNG") }
        }
    )

    if (showAddDialog || editingCategory != null) {
        var name by remember { mutableStateOf(editingCategory?.name ?: "") }
        var selectedIcon by remember { mutableStateOf(editingCategory?.iconName ?: iconOptions[0].first) }
        
        AlertDialog(
            onDismissRequest = { 
                showAddDialog = false
                editingCategory = null
            },
            title = { Text(if (editingCategory != null) "Sửa danh mục" else "Thêm danh mục") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên danh mục") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chọn icon:", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        iconOptions.forEach { (iconName, vector) ->
                            IconButton(
                                onClick = { selectedIcon = iconName },
                                modifier = Modifier
                                    .size(48.dp)
                                    .androidx.compose.foundation.background(
                                        if (selectedIcon == iconName) MaterialTheme.colorScheme.primaryContainer else androidx.compose.ui.graphics.Color.Transparent,
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = vector,
                                    contentDescription = null,
                                    tint = if (selectedIcon == iconName) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingCategory != null) {
                            viewModel.updateExpenseCategory(editingCategory!!.copy(name = name, iconName = selectedIcon))
                        } else {
                            viewModel.addExpenseCategory(name, selectedIcon)
                        }
                        showAddDialog = false
                        editingCategory = null
                    },
                    enabled = name.isNotBlank()
                ) {
                    Text("LƯU")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddDialog = false
                    editingCategory = null
                }) { Text("HỦY") }
            }
        )
    }
}
