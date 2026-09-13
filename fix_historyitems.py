import re

with open("app/src/main/java/com/example/ui/screens/HistoryItems.kt", "r") as f:
    content = f.read()

target = r"""Text\("\$\{entry\.trips\} cuốc • \$\{FormatUtils\.formatTime\(entry\.timestamp\)\}", style = MaterialTheme\.typography\.bodySmall, color = MaterialTheme\.colorScheme\.onSurfaceVariant\)"""
replace = """var subtext = "${entry.trips} cuốc"
                if (entry.distanceKm != null) subtext += " • ${String.format("%.1f", entry.distanceKm).replace(".0", "").replace(".", ",")} km"
                if ((entry.tipAmount ?: 0L) > 0L) subtext += " • Tip +${FormatUtils.formatCurrency(entry.tipAmount ?: 0L)}"
                subtext += " • ${FormatUtils.formatTime(entry.timestamp)}"
                Text(subtext, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)"""
content = re.sub(target, replace, content)

with open("app/src/main/java/com/example/ui/screens/HistoryItems.kt", "w") as f:
    f.write(content)

