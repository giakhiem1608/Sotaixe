import re

with open("app/src/main/java/com/example/ui/screens/HistoryItems.kt", "r") as f:
    content = f.read()

# Replace revenue string to include Tip
target_rev = r"""val revStr = if \(rev\.distanceKm != null\) "\$\{rev\.trips\} cuốc • \$\{String\.format\("%\.1f", rev\.distanceKm\)\.replace\("\.0", ""\)\.replace\("\.", ","\)\} km" else "\$\{rev\.trips\} cuốc""""
replace_rev = """var revStr = if (rev.distanceKm != null) "${rev.trips} cuốc • ${String.format("%.1f", rev.distanceKm).replace(".0", "").replace(".", ",")} km" else "${rev.trips} cuốc"
    if ((rev.tipAmount ?: 0L) > 0L) {
        revStr += " • Tip +${FormatUtils.formatCurrency(rev.tipAmount ?: 0L)}"
    }"""
content = re.sub(target_rev, replace_rev, content)

with open("app/src/main/java/com/example/ui/screens/HistoryItems.kt", "w") as f:
    f.write(content)

