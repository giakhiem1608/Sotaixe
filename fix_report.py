import re

with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "r") as f:
    content = f.read()

new_section = """@Composable
fun FinancialSummarySection(netIncome: Long, rev: Long, tip: Long, exp: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text("TỔNG THU NHẬP", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(FormatUtils.formatCurrency(netIncome), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = CardBorder)
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Doanh thu", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF475569))
                Text(FormatUtils.formatCurrency(rev), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF0F172A))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tip", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF475569))
                Text(if (tip > 0) "+${FormatUtils.formatCurrency(tip)}" else FormatUtils.formatCurrency(0), fontWeight = FontWeight.Bold, color = Color(0xFF059669), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Chi phí", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF475569))
                Text("-${FormatUtils.formatCurrency(exp)}", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}"""

old_section = re.search(r'@Composable\nfun FinancialSummarySection.*?^}', content, re.MULTILINE | re.DOTALL)
if old_section:
    content = content[:old_section.start()] + new_section + content[old_section.end():]
    with open("app/src/main/java/com/example/ui/screens/ReportScreen.kt", "w") as f:
        f.write(content)
    print("Success")
else:
    print("Fail")
