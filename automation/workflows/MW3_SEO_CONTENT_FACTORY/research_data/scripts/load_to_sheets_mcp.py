#!/usr/bin/env python3
"""
Carga keywords a Google Sheets usando formato JSON para MCP tool
"""

import pandas as pd
import json

# Cargar CSV
df = pd.read_csv('reports/keywords_for_sheets_upload.csv')

# Convertir a lista de listas (formato para sheets_append_rows)
rows = df.values.tolist()

# Guardar como JSON para copiar y pegar
with open('reports/keywords_for_mcp.json', 'w', encoding='utf-8') as f:
    json.dump(rows[:50], f, indent=2, ensure_ascii=False)  # Solo primeras 50 para test

print(f"[OK] JSON generado con {len(rows[:50])} keywords para MCP tool")
print(f"[*] Total keywords disponibles: {len(rows)}")
print(f"\n[*] Para cargar todas las keywords, usar sheets_batch_update con batches de 50")
