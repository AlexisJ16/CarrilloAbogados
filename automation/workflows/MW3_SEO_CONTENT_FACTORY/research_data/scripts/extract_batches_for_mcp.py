#!/usr/bin/env python3
"""
Extrae batches en formato Python list para MCP tool
"""

import json

# Leer batch1 remaining (keywords 6-50)
with open('reports/upload_batch1_remaining.json', 'r', encoding='utf-8') as f:
    batch1_rem = json.load(f)

# Leer batch2 (keywords 51-100)
with open('reports/upload_batch2.json', 'r', encoding='utf-8') as f:
    batch2 = json.load(f)

# Leer batch3 (keywords 101-150)
with open('reports/upload_batch3.json', 'r', encoding='utf-8') as f:
    batch3 = json.load(f)

# Guardar en formato Python para MCP
with open('reports/mcp_batch1.txt', 'w', encoding='utf-8') as f:
    f.write(str(batch1_rem))

with open('reports/mcp_batch2.txt', 'w', encoding='utf-8') as f:
    f.write(str(batch2))

with open('reports/mcp_batch3.txt', 'w', encoding='utf-8') as f:
    f.write(str(batch3))

print("Batches en formato MCP generados:")
print(f"  - mcp_batch1.txt: {len(batch1_rem)} rows")
print(f"  - mcp_batch2.txt: {len(batch2)} rows")
print(f"  - mcp_batch3.txt: {len(batch3)} rows")
