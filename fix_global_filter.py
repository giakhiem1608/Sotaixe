import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "r") as f:
    content = f.read()

target = r"""    val filteredRevenues = remember\(revenueEntries, selectedFilter, sources\) \{\n        if \(selectedFilter == "Tất cả"\) \{\n            revenueEntries\n        \} else if \(selectedFilter == "Thiếu KM"\) \{\n            revenueEntries\.filter \{ it\.distanceKm == null \|\| it\.distanceKm <= 0f \}\n        \} else \{\n            val sourceId = sources\.find \{ it\.name == selectedFilter \}\?\.id\n            if \(sourceId != null\) revenueEntries\.filter \{ it\.sourceId == sourceId \} else revenueEntries\n        \}\n    \}"""

replace = """    val filteredRevenues = remember(revenueEntries, selectedFilter, sources) {
        if (selectedFilter == "Tất cả") {
            revenueEntries
        } else if (selectedFilter == "Thiếu KM") {
            revenueEntries.filter { it.distanceKm == null || it.distanceKm <= 0f }
        } else if (selectedFilter == "Có Tip") {
            revenueEntries.filter { (it.tipAmount ?: 0L) > 0L }
        } else {
            val sourceId = sources.find { it.name == selectedFilter }?.id
            if (sourceId != null) revenueEntries.filter { it.sourceId == sourceId } else revenueEntries
        }
    }"""
content = re.sub(target, replace, content)

target_scroll = r"""        ScrollableTabRow\(\n            selectedTabIndex = filterKeys\.indexOf\(selectedFilter\)\.takeIf \{ it >= 0 \} \?: 0,\n            modifier = Modifier\.fillMaxWidth\(\)\.padding\(vertical = 8\.dp\),\n            edgePadding = 0\.dp,\n            indicator = \{\},\n            divider = \{\},\n            containerColor = androidx\.compose\.ui\.graphics\.Color\.Transparent\n        \) \{\n            filterKeys\.forEach \{ filterKey ->"""

replace_scroll = """        val displayFilterKeys = filterKeys.toMutableList()
        val hasTip = revenueEntries.any { (it.tipAmount ?: 0L) > 0L }
        if (hasTip && "Có Tip" !in displayFilterKeys) displayFilterKeys.add("Có Tip")

        ScrollableTabRow(
            selectedTabIndex = displayFilterKeys.indexOf(selectedFilter).takeIf { it >= 0 } ?: 0,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            edgePadding = 0.dp,
            indicator = {},
            divider = {},
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) {
            displayFilterKeys.forEach { filterKey ->"""
content = re.sub(target_scroll, replace_scroll, content)

with open("app/src/main/java/com/example/ui/screens/HistoryScreen.kt", "w") as f:
    f.write(content)

