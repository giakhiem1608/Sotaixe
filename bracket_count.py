with open("app/src/main/java/com/example/ui/screens/OtherScreen.kt", "r") as f:
    lines = f.readlines()

count = 0
for i, line in enumerate(lines):
    count += line.count('{') - line.count('}')
    print(f"{i+1:3d} [{count:2d}]: {line.rstrip()}")
