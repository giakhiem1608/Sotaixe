import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

target = r"""    val allDates = \(revenueEntries\.map \{ it\.dateString \} \+ expenseEntries\.map \{ it\.dateString \}\)\.distinct\(\)\.sortedDescending\(\)\n\n    Column\("""
replacement = """    val missingKmCount = revenueEntries.count { it.distanceKm == null || it.distanceKm <= 0f }
    val activeMissingKmFilter = viewModel.activeMissingKmFilter.collectAsState().value
    
    var selectedFilter by remember { mutableStateOf<String>("Tất cả") }
    
    LaunchedEffect(activeMissingKmFilter) {
        if (activeMissingKmFilter) {
            selectedFilter = "Thiếu KM"
            viewModel.clearMissingKmFilter()
        }
    }

    val filteredRevenues = remember(revenueEntries, selectedFilter, sources) {
        if (selectedFilter == "Tất cả") {
            revenueEntries
        } else if (selectedFilter == "Thiếu KM") {
            revenueEntries.filter { it.distanceKm == null || it.distanceKm <= 0f }
        } else {
            val sourceId = sources.find { it.name == selectedFilter }?.id
            if (sourceId != null) revenueEntries.filter { it.sourceId == sourceId } else revenueEntries
        }
    }
    
    val filteredExpenses = remember(expenseEntries, selectedFilter) {
        if (selectedFilter == "Tất cả") expenseEntries else emptyList()
    }
    
    val allDates = (filteredRevenues.map { it.dateString } + filteredExpenses.map { it.dateString }).distinct().sortedDescending()

    Column("""

content = re.sub(target, replacement, content)

filter_ui_insert = """        Text(
            text = "Lịch sử giao dịch",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
        )
        
        // Filters
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedFilter == "Tất cả",
                    onClick = { selectedFilter = "Tất cả" },
                    label = { Text("Tất cả") }
                )
            }
            if (missingKmCount > 0 || selectedFilter == "Thiếu KM") {
                item {
                    val missingTrips = revenueEntries.filter { it.distanceKm == null || it.distanceKm <= 0f }.sumOf { it.trips }
                    FilterChip(
                        selected = selectedFilter == "Thiếu KM",
                        onClick = { selectedFilter = "Thiếu KM" },
                        label = { Text(if (missingTrips > 0) "Thiếu KM ($missingTrips)" else "Thiếu KM") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
            items(sources) { source ->
                val hasTransactions = revenueEntries.any { it.sourceId == source.id }
                if (hasTransactions || selectedFilter == source.name) {
                    FilterChip(
                        selected = selectedFilter == source.name,
                        onClick = { selectedFilter = source.name },
                        label = { Text(source.name) }
                    )
                }
            }
        }
        
        if (selectedFilter == "Thiếu KM" && filteredRevenues.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "✓ Đã cập nhật đầy đủ KM",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Không còn cuốc nào thiếu dữ liệu quãng đường.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (allDates.isEmpty()) {"""

content = content.replace("""        Text(
            text = "Lịch sử giao dịch",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        )
        if (allDates.isEmpty()) {""", filter_ui_insert)


content = content.replace("""                    val dayRevenues = revenueEntries.filter { it.dateString == dateStr }
                    val dayExpenses = expenseEntries.filter { it.dateString == dateStr }""",
"""                    val dayRevenues = filteredRevenues.filter { it.dateString == dateStr }
                    val dayExpenses = filteredExpenses.filter { it.dateString == dateStr }""")

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)

