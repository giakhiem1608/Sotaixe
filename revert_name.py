import re

def replace_in_file(filepath, old, new):
    with open(filepath, 'r') as f:
        content = f.read()
    content = content.replace(old, new)
    with open(filepath, 'w') as f:
        f.write(content)

replace_in_file("metadata.json", '"name": "BA BON"', '"name": "Sổ Tài Xế"')
replace_in_file("settings.gradle.kts", 'rootProject.name = "BA BON"', 'rootProject.name = "Sổ Tài Xế"')
replace_in_file("app/src/main/res/values/strings.xml", '<string name="app_name">BA BON</string>', '<string name="app_name">Sổ Tài Xế</string>')
print("Done")
