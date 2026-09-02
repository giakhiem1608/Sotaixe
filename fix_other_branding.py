import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# Add APP_VERSION constant
if "const val APP_VERSION" not in content:
    content = content.replace(
        "import androidx.compose.ui.unit.dp\n",
        "import androidx.compose.ui.unit.dp\n\nconst val APP_VERSION = \"1.0\"\n\n"
    )

# Add branding at the end of the Column
branding_ui = """            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚗 Made for drivers • ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("LHN", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("  ·  v$APP_VERSION", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
"""

content = content.replace(
    """            }
        }
    }""", branding_ui
)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
