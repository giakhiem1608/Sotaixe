import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

new_header = """    var timeFilter by remember { mutableStateOf("month") } // today, 7days, month, all
    val allRev by viewModel.allRevenueEntries.collectAsState()
    val allExp by viewModel.allExpenseEntries.collectAsState()

    val displayMonth = FormatUtils.formatDisplayMonth(currentMonth)

    val (activeRev, activeExp) = when (timeFilter) {
        "today" -> {
            val today = FormatUtils.formatDbDate(System.currentTimeMillis())
            allRev.filter { it.dateString == today } to allExp.filter { it.dateString == today }
        }
        "7days" -> {
            val cutoff = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
            allRev.filter { it.timestamp >= cutoff } to allExp.filter { it.timestamp >= cutoff }
        }
        "all" -> {
            allRev to allExp
        }
        else -> {
            revenueEntries to expenseEntries
        }
    }

    val combinedItems = mutableListOf<Any>()

    // Filtering
    val filteredRev = when (selectedFilter) {
        "all" -> activeRev
        "has_tip" -> activeRev.filter { (it.tipAmount ?: 0L) > 0L }
        "missing_km" -> activeRev.filter { it.distanceKm == null }
        "expense" -> emptyList()
        else -> {
            val sourceId = selectedFilter.toIntOrNull()
            if (sourceId != null) activeRev.filter { it.sourceId == sourceId } else activeRev
        }
    }

    val filteredExp = when (selectedFilter) {
        "all", "expense" -> activeExp
        else -> emptyList()
    }

    combinedItems.addAll(filteredRev)
    combinedItems.addAll(filteredExp)
    combinedItems.sortByDescending { 
        if (it is RevenueEntry) it.timestamp else if (it is ExpenseEntry) it.timestamp else 0L 
    }

    Column(modifier = Modifier.fillMaxSize().background(BgColor)) {
        // TIME FILTER ROW
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { CustomFilterChip("today", "Hôm nay", timeFilter, primaryColor) { timeFilter = "today" } }
            item { CustomFilterChip("7days", "7 ngày", timeFilter, primaryColor) { timeFilter = "7days" } }
            item { CustomFilterChip("month", "Tháng", timeFilter, primaryColor) { timeFilter = "month" } }
            item { CustomFilterChip("all", "Tất cả", timeFilter, primaryColor) { timeFilter = "all" } }
        }

        if (timeFilter == "month") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color(0xFF475569)) }
                Text(displayMonth, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                IconButton(onClick = { viewModel.nextMonth() }) { Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF475569)) }
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // TYPE FILTER CHIPS
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CustomFilterChip("all", "Tất cả", selectedFilter, primaryColor) { selectedFilter = "all" }
            }
            sources.forEach { source ->
                item {
                    val count = activeRev.count { it.sourceId == source.id }
                    CustomFilterChip(source.id.toString(), "${source.name} ($count)", selectedFilter, primaryColor) { selectedFilter = source.id.toString() }
                }
            }
            item {
                val mkCount = activeRev.count { it.distanceKm == null }
                CustomFilterChip("missing_km", "Thiếu KM ($mkCount)", selectedFilter, primaryColor) { selectedFilter = "missing_km" }
            }
            item {
                val tipCount = activeRev.count { (it.tipAmount ?: 0L) > 0L }
                CustomFilterChip("has_tip", "Có Tip ($tipCount)", selectedFilter, primaryColor) { selectedFilter = "has_tip" }
            }
            item {
                CustomFilterChip("expense", "Chi phí (${activeExp.size})", selectedFilter, primaryColor) { selectedFilter = "expense" }
            }
        }"""

start_str = "    val displayMonth = FormatUtils.formatDisplayMonth(currentMonth)"
end_str = 'CustomFilterChip("expense", "Chi phí (${expenseEntries.size})", selectedFilter, primaryColor) { selectedFilter = "expense" }\n            }\n        }'

if start_str in content and end_str in content:
    start_idx = content.find(start_str)
    end_idx = content.find(end_str) + len(end_str)
    content = content[:start_idx] + new_header + content[end_idx:]
    with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
        f.write(content)
    print("Successfully replaced.")
else:
    print("Could not find start or end strings.")
