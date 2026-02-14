#!/usr/bin/env python3
"""
Genera comandos MCP para carga de keywords
"""

import pandas as pd

# Leer CSV
df = pd.read_csv('reports/keywords_for_sheets_upload.csv')

# Skip primeras 5 (ya cargadas)
remaining = df.iloc[5:]

# Crear chunks de 25 rows
chunk_size = 25
chunks = [remaining.iloc[i:i+chunk_size] for i in range(0, len(remaining), chunk_size)]

print(f"Total keywords restantes: {len(remaining)}")
print(f"Chunks de {chunk_size} rows: {len(chunks)}")
print(f"\nChunk sizes: {[len(c) for c in chunks]}")

# Generar resumen para informe final
print(f"\n[OK] Listos para cargar {len(chunks)} batches:")
for i, chunk in enumerate(chunks, 1):
    first_kw = chunk.iloc[0]['keyword_text']
    last_kw = chunk.iloc[-1]['keyword_text']
    print(f"  Batch {i}: {len(chunk)} keywords ({first_kw} ... {last_kw})")
