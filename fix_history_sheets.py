with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

sheets = """
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditRevenueSheet(
    entry: RevenueEntry,
    sources: List<RevenueSource>,
    onDismiss: () -> Unit,
    onSave: (RevenueEntry) -> Unit,
    onDelete: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedSourceId by remember { mutableStateOf(entry.sourceId) }
        var amountStr by remember { mutableStateOf(entry.amount.toString()) }
        var tipStr by remember { mutableStateOf((entry.tipAmount ?: 0L).let { if (it > 0) it.toString() else "" }) }
        var tripsStr by remember { mutableStateOf(entry.trips.toString()) }
        var distanceStr by remember { mutableStateOf(entry.distanceKm?.toString()?.replace(".0", "") ?: "") }
        var note by remember { mutableStateOf(entry.note) }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("SỬA DOANH THU", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sources.forEach { source ->
                    val color = try { Color(android.graphics.Color.parseColor(source.colorHex)) } catch (e: Exception) { Color.Gray }
                    FilterChip(
                        selected = selectedSourceId == source.id,
                        onClick = { selectedSourceId = source.id },
                        label = { Text(source.name) },
                        leadingIcon = { Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color)) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                label = { Text("Số tiền thực nhận (đ)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = tripsStr,
                onValueChange = { tripsStr = it },
                label = { Text("Số cuốc") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = distanceStr,
                onValueChange = { distanceStr = it.replace(",", ".") },
                label = { Text("Số KM") },
                placeholder = { Text("Không bắt buộc") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = tipStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) tipStr = newValue },
                label = { Text("Tiền tip (Tùy chọn)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { onDelete(entry.id) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("XÓA")
                }
                Button(
                    onClick = {
                        val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                        val tip = tipStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L
                        val trips = tripsStr.toIntOrNull()
                        val dist = distanceStr.toFloatOrNull()
                        if (amount != null && trips != null && trips >= 1 && selectedSourceId != 0) {
                            onSave(entry.copy(
                                sourceId = selectedSourceId,
                                amount = amount,
                                tipAmount = tip,
                                trips = trips,
                                distanceKm = dist,
                                note = note
                            ))
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L && (tripsStr.toIntOrNull() ?: 0) >= 1
                ) {
                    Text("LƯU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditExpenseSheet(
    entry: ExpenseEntry,
    categories: List<ExpenseCategory>,
    onDismiss: () -> Unit,
    onSave: (ExpenseEntry) -> Unit,
    onDelete: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        var selectedCategoryId by remember { mutableStateOf(entry.categoryId) }
        var amountStr by remember { mutableStateOf(entry.amount.toString()) }
        var note by remember { mutableStateOf(entry.note) }

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp).padding(bottom = 32.dp).imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("SỬA CHI PHÍ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategoryId == category.id,
                        onClick = { selectedCategoryId = category.id },
                        label = { Text(category.name) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = amountStr,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amountStr = newValue },
                label = { Text("Số tiền (đ)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                visualTransformation = com.example.utils.CurrencyVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi chú (Tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { onDelete(entry.id) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("XÓA")
                }
                Button(
                    onClick = {
                        val amount = amountStr.replace(Regex("[^0-9]"), "").toLongOrNull()
                        if (amount != null && selectedCategoryId != 0) {
                            onSave(entry.copy(categoryId = selectedCategoryId, amount = amount, note = note))
                        }
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    enabled = (amountStr.replace(Regex("[^0-9]"), "").toLongOrNull() ?: 0L) > 0L
                ) {
                    Text("LƯU", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
"""

content = content.replace("    if (selectedRevenue != null) {\n        // Implement EditRevenueSheet\n    }", """    if (selectedRevenue != null) {
        EditRevenueSheet(
            entry = selectedRevenue!!,
            sources = sources,
            onDismiss = { selectedRevenue = null },
            onSave = { updated ->
                viewModel.updateRevenueEntry(updated)
                selectedRevenue = null
            },
            onDelete = { id ->
                viewModel.deleteRevenue(id)
                selectedRevenue = null
            }
        )
    }""")

content = content.replace("    if (selectedExpense != null) {\n        // Implement EditExpenseSheet\n    }", """    if (selectedExpense != null) {
        EditExpenseSheet(
            entry = selectedExpense!!,
            categories = categories,
            onDismiss = { selectedExpense = null },
            onSave = { updated ->
                viewModel.updateExpenseEntry(updated)
                selectedExpense = null
            },
            onDelete = { id ->
                viewModel.deleteExpense(id)
                selectedExpense = null
            }
        )
    }""")

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content + "\n" + sheets)
