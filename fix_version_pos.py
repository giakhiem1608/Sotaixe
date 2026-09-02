import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

# Remove the incorrectly placed APP_VERSION
content = content.replace('const val APP_VERSION = "1.0"\n\n', '')

# Add it after the package declaration
content = content.replace(
    'package com.example.ui.screens\n',
    'package com.example.ui.screens\n\nconst val APP_VERSION = "1.0"\n'
)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
