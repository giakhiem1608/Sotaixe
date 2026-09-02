sed -i 's/androidx.compose.ui.graphics.luminance(revenueThemeColor)/revenueThemeColor.luminance()/g' app/src/main/java/com/example/ui/screens/ReportScreen.kt
sed -i '12a\
import androidx.compose.ui.graphics.luminance
' app/src/main/java/com/example/ui/screens/ReportScreen.kt
