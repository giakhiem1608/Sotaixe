sed -i '244,261c\
                item {\
                    Text("Cơ cấu nguồn thu", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))\
                    val breakdownList = sources.map { source ->\
                        val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }\
                        val sourceTrips = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.trips }\
                        Triple(source, sourceRevenue, sourceTrips)\
                    }.filter { it.second > 0 }.sortedByDescending { it.second }\
                    com.example.ui.components.RevenueBreakdown(\
                        totalRevenue = totalRev,\
                        breakdown = breakdownList\
                    )\
                }' app/src/main/java/com/example/ui/screens/ReportScreen.kt
