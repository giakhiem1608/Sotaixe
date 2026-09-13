import re

with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "r") as f:
    content = f.read()

target = r"data class ThemePreset.*?\)\s*\)\s*val COLOR_PALETTE = listOf"

replace = """data class ThemePreset(
    val name: String,
    val bgHex: String,
    val incomeHex: String,
    val revHex: String,
    val expHex: String,
    val tipHex: String
)

val PRESETS = listOf(
    ThemePreset("MIDNIGHT", "#111827", "#FFFFFF", "#22C55E", "#F05D5E", "#F59E0B"),
    ThemePreset("DEEP NAVY", "#0F172A", "#FFFFFF", "#22C55E", "#F05D5E", "#F59E0B"),
    ThemePreset("GRAPHITE", "#20242C", "#FFFFFF", "#22C55E", "#F05D5E", "#F59E0B"),
    ThemePreset("DEEP TEAL", "#115E59", "#FFFFFF", "#86EFAC", "#FDA4AF", "#FDE047"),
    ThemePreset("ROYAL", "#312E81", "#FFFFFF", "#A7F3D0", "#FECDD3", "#FDE047"),
    ThemePreset("LIGHT", "#FFFFFF", "#172033", "#16A34A", "#DC2626", "#D97706")
)

val COLOR_PALETTE = listOf"""

content = re.sub(target, replace, content, flags=re.DOTALL)
with open("app/src/main/java/com/example/ui/screens/ThemeSettingsScreen.kt", "w") as f:
    f.write(content)
