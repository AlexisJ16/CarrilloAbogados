#!/usr/bin/env python3
"""
Export keywords in simple TSV format for manual Google Sheets import
"""

import pandas as pd

# Leer CSV
df = pd.read_csv('reports/keywords_for_sheets_upload.csv')

# Skip primeras 5 (ya cargadas)
remaining = df.iloc[5:]

# Convertir booleanos a string para Google Sheets
remaining_copy = remaining.copy()
remaining_copy['is_main_keyword'] = remaining_copy['is_main_keyword'].map({True: 'TRUE', False: 'FALSE', 'True': 'TRUE', 'False': 'FALSE'})
remaining_copy['enabled'] = remaining_copy['enabled'].map({True: 'TRUE', False: 'FALSE', 'True': 'TRUE', 'False': 'FALSE'})

# Exportar como TSV (Tab Separated Values) - más fácil para Google Sheets
output_file = 'reports/keywords_remaining_for_manual_import.tsv'
remaining_copy.to_csv(output_file, sep='\t', index=False, encoding='utf-8')

print(f"[OK] Archivo TSV generado: {output_file}")
print(f"[*] {len(remaining)} keywords listas para importar")
print(f"\n[INSTRUCCIONES]:")
print(f"1. Abrir Keywords_Master en Google Sheets")
print(f"2. Ir a la tab 'All_Keywords'")
print(f"3. Click en celda A12 (después de las 5 keywords ya cargadas)")
print(f"4. File > Import > Upload > Seleccionar {output_file}")
print(f"5. Import location: 'Insert at current cell'")
print(f"6. Separator: Tab")
print(f"7. Import data")
print(f"\nAlternativamente, abrir el TSV y copiar/pegar directamente")
