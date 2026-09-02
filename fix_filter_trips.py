import re

with open("app/src/main/java/com/example/ui/screens/HistoryScreenFilter.kt", "r") as f:
    content = f.read()

content = content.replace(
    '"Tất cả (${revenues.size})"',
    '"Tất cả (${revenues.sumOf { it.trips }})"'
)

content = content.replace(
    'val count = revenues.count { r -> sources.find { it.id == r.sourceId }?.name == filterKey }',
    'val count = revenues.filter { r -> sources.find { it.id == r.sourceId }?.name == filterKey }.sumOf { it.trips }'
)

with open("app/src/main/java/com/example/ui/screens/HistoryScreenFilter.kt", "w") as f:
    f.write(content)
