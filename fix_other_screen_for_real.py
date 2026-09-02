import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# Let's remove the extra braces between backupLauncher and restoreLauncher
# Currently we have:
# 68             }
# 69         }
# 70 
# 71             }
# 72         }
# 73     }
# 74 
# 75 
# 76     val restoreLauncher = rememberLauncherForActivityResult(

content = re.sub(r'            \}\n        \}\n\n            \}\n        \}\n    \}', r'            }\n        }\n    }', content)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
