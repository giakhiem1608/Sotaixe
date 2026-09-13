import re

with open("app/src/main/java/com/example/data/RevenueEntry.kt", "r") as f:
    content = f.read()

content = content.replace("val amount: Long,", "val amount: Long,\n    val tipAmount: Long? = 0L,")

with open("app/src/main/java/com/example/data/RevenueEntry.kt", "w") as f:
    f.write(content)
