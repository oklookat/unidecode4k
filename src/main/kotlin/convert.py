import os
from typing import List

# Converts unidecode Python tables to Kotlin files.
# 1. Download: https://github.com/avian2/unidecode/tree/master/unidecode
# 2. Delete any .py files that not starts with "x".
# 3. Set source and target folder below.
# 4. Run.

package_name = "table"
source_folder = "./py"
target_folder = "./table"

class Entry:
    none: bool
    content: str
    comment: str
    def __init__(self):
        self.content = ""
        self.comment = ""

def find_start_end_entries(line: str) -> List[Entry]:
    current_entry = Entry()
    entries = []
    in_entry = False
    def is_string(char: str)->bool:
        return char == "'" or char == '"'
    i = 0
    while(i < len(line)):  
        char = line[i]
        if not in_entry:
            if is_string(char):
                in_entry = True
                i += 1
                continue
            if char.startswith("#") and i+1 < len(line):
                current_entry.comment = line[i+1:]
                break
            if i+1 < len(line) and line[i] == "N" and line[i+1] == "o":
                current_entry.none = True
                entries.append(current_entry)
            i += 1
            continue
        if is_string(char) and i+1 < len(line) and line[i+1] == ",":
            in_entry = False
            entries.append(current_entry)
            i += 1
            continue
        # escape " in " "
        if char == '"':
            current_entry.content += "\\" + char
            i += 1
            continue
        # \x shit (example: 0x020.py -> # 0x28)
        if char == '\\' and i+1 < len(line) and line[i+1] == 'x':
            hex_value = line[i+2:i+4]
            unicode_char = chr(int(hex_value, 16))
            current_entry.content += unicode_char.encode("unicode_escape").decode("utf-8")
            i += 4
            continue
        current_entry.content += char
        i += 1
    return entries


filenames_without_ext: List[str] = []

def gen_table(filenames_without_ext: List[str]):
    kotlin_lines = []
    kotlin_lines.append(f"package {package_name}\n")
    kotlin_lines.append("internal val tables = mutableMapOf<Char, List<String>>().apply {")
    for name in filenames_without_ext:
        kotlin_lines.append(f"\tthis[0{name}.toChar()] = {name}")
    kotlin_lines.append("}")

    kotlin_file_path = os.path.join(target_folder, f"tables.kt")
    with open(kotlin_file_path, 'w', encoding='utf-8') as f:
        f.write("\n".join(kotlin_lines))

def convert_file(file_path, target_folder):
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    filename_without_ext = os.path.splitext(os.path.basename(file_path))[0]
    filenames_without_ext.append(filename_without_ext)
    
    kotlin_lines = []
    kotlin_lines.append(f"package {package_name}\n\ninternal val {filename_without_ext} = listOf(")
    
    inside_data = False
    
    for line in lines:
        line = line.strip()
        
        if line.startswith("data = ("):
            inside_data = True
            continue
        elif line == ")":
            inside_data = False
            continue

        if not inside_data:
            continue

        if len(line) == 0:
            kotlin_lines.append("")
            continue

        if line.startswith('#'):
            kotlin_lines.append("//" + line.removeprefix("#"))
            continue

        entries = find_start_end_entries(line)

        kotlin_line = ""

        for i, entry in enumerate(entries):
            kotlin_line+= f'"{entry.content}",'
            if i+1 == len(entries) and len(entry.comment) > 0:
                comment = " //" + entry.comment
                if comment == " // 0x80":
                    e = 1
                kotlin_line+=comment
                
        kotlin_lines.append(kotlin_line)

    kotlin_lines.append(")")

    kotlin_file_path = os.path.join(target_folder, f"{filename_without_ext}.kt")
    with open(kotlin_file_path, 'w', encoding='utf-8') as f:
        f.write("\n".join(kotlin_lines))

def convert_folder(source_folder, target_folder):
    if not os.path.exists(target_folder):
        os.makedirs(target_folder)

    e = '\x0a'
    for filename in os.listdir(source_folder):
        if filename.endswith(".py"):
            file_path = os.path.join(source_folder, filename)
            convert_file(file_path, target_folder)

convert_folder(source_folder, target_folder)
gen_table(filenames_without_ext)
