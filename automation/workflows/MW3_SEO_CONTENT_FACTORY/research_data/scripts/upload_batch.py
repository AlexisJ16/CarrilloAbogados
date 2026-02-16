#!/usr/bin/env python3
"""
Prepara datos para carga a Google Sheets via MCP
"""

import pandas as pd
import json

# Leer CSV completo
df = pd.read_csv('reports/keywords_for_sheets_upload.csv')

# Convertir booleanos de string a bool
df['is_main_keyword'] = df['is_main_keyword'].map({'True': True, 'False': False})
df['enabled'] = df['enabled'].map({'True': True, 'False': False})

# Convertir a lista de listas
all_rows = df.values.tolist()

# Dividir en 3 batches
batch1 = all_rows[:50]
batch2 = all_rows[50:100]
batch3 = all_rows[100:]

print(f"Batch 1: {len(batch1)} rows")
print(f"Batch 2: {len(batch2)} rows")
print(f"Batch 3: {len(batch3)} rows")
print(f"Total: {len(all_rows)} rows")

# Guardar como JSON para inspección
with open('reports/batch1.json', 'w', encoding='utf-8') as f:
    json.dump(batch1, f, indent=2, ensure_ascii=False)

with open('reports/batch2.json', 'w', encoding='utf-8') as f:
    json.dump(batch2, f, indent=2, ensure_ascii=False)

with open('reports/batch3.json', 'w', encoding='utf-8') as f:
    json.dump(batch3, f, indent=2, ensure_ascii=False)

print("\n[OK] Batches JSON generados")
print("Listo para cargar con MCP sheets_append_rows")
