#!/usr/bin/env python3
"""
Prepara keywords para carga completa a Sheets
"""

import json

# Leer batches
with open('reports/batch1.json', 'r', encoding='utf-8') as f:
    batch1 = json.load(f)

with open('reports/batch2.json', 'r', encoding='utf-8') as f:
    batch2 = json.load(f)

with open('reports/batch3.json', 'r', encoding='utf-8') as f:
    batch3 = json.load(f)

# Ya cargamos las primeras 5, tomar el resto del batch1 (45 rows)
remaining_batch1 = batch1[5:]  # Keywords 6-50

# Guardar en formato compacto para copiar/pegar
with open('reports/upload_batch1_remaining.json', 'w') as f:
    json.dump(remaining_batch1, f, separators=(',', ':'), ensure_ascii=False)

with open('reports/upload_batch2.json', 'w') as f:
    json.dump(batch2, f, separators=(',', ':'), ensure_ascii=False)

with open('reports/upload_batch3.json', 'w') as f:
    json.dump(batch3, f, separators=(',', ':'), ensure_ascii=False)

print(f"Batch 1 remaining: {len(remaining_batch1)} rows (keywords 6-50)")
print(f"Batch 2: {len(batch2)} rows (keywords 51-100)")
print(f"Batch 3: {len(batch3)} rows (keywords 101-150)")
print(f"\nTotal pendiente: {len(remaining_batch1) + len(batch2) + len(batch3)} keywords")
