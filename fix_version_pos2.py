import re

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    content = f.read()

content = content.replace("const val APP_VERSION = \"1.0\"\n\n", "")

content = content.replace(
    "import java.util.Locale\n",
    "import java.util.Locale\n\nconst val APP_VERSION = \"1.0\"\n"
)

with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "w") as f:
    f.write(content)
