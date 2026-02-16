#!/usr/bin/env python3
"""
Genera batches finales para carga MCP desde CSV original
"""

import pandas as pd
import json

# Leer CSV
df = pd.read_csv('reports/keywords_for_sheets_upload.csv')

# Convertir booleanos
df['is_main_keyword'] = df['is_main_keyword'].map({True: True, False: False, 'True': True, 'False': False})
df['enabled'] = df['enabled'].map({True: True, False: False, 'True': True, 'False': False})

# Convertir a lista de listas
all_rows = df.values.tolist()

# Ya cargamos las primeras 5, obtener el resto
remaining = all_rows[5:]

# Dividir en batches de 45 rows (para evitar timeouts)
batch1 = remaining[:45]  # Rows 6-50
batch2 = remaining[45:95]  # Rows 51-95
batch3 = remaining[95:]  # Rows 96-150

print(f"Preparando para carga MCP:")
print(f"  Batch 1 (ya cargado): 5 rows")
print(f"  Batch 2: {len(batch1)} rows")
print(f"  Batch 3: {len(batch2)} rows")
print(f"  Batch 4: {len(batch3)} rows")
print(f"  Total: {5 + len(batch1) + len(batch2) + len(batch3)} keywords")

# Guardar count por batch
print(f"\n[INFO] Rows por batch para MCP:")
print(f"Batch 2: {len(batch1)}")
print(f"Batch 3: {len(batch2)}")
print(f"Batch 4: {len(batch3)}")
