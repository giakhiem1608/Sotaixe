sed -i '257,277c\
                // Smart Stats\
                item {\
                    Text("Nhận xét thông minh", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))\
                    Card(\
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),\
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)\
                    ) {\
                        Column(modifier = Modifier.padding(16.dp)) {\
                            Text("Dựa trên dữ liệu hiện có:", modifier = Modifier.padding(vertical = 4.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\
                            val breakdown = sources.map { source ->\
                                val sourceRevenue = revenueEntries.filter { it.sourceId == source.id }.sumOf { it.amount }\
                                Pair(source, sourceRevenue)\
                            }.filter { it.second > 0 }.sortedByDescending { it.second }' app/src/main/java/com/example/ui/screens/ReportScreen.kt
