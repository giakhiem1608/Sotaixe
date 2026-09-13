import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

target = r"""                            if \(totalHours > 0\) \{\n                                StatRow\("Tổng giờ chạy", "\$\{String\.format\("%\.1f", totalHours\)\.replace\("\.", ","\)\} giờ"\)\n                            \}"""

replace = """                            if (totalHours > 0) {
                                StatRow("Tổng giờ chạy", "${String.format("%.1f", totalHours).replace(".", ",")} giờ")
                            }

                            if (totalTip > 0) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("TIỀN TIP", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(8.dp))
                                StatRow("Tổng Tip", FormatUtils.formatCurrency(totalTip))
                                val tipTransactions = revenueEntries.count { (it.tipAmount ?: 0L) > 0L }
                                StatRow("Giao dịch có Tip", "$tipTransactions")
                                val avgTip = if (tipTransactions > 0) totalTip / tipTransactions else 0L
                                StatRow("TB Tip/giao dịch có Tip", FormatUtils.formatCurrency(avgTip))
                            }"""
content = re.sub(target, replace, content)

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
    f.write(content)
