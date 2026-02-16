#!/usr/bin/env python3
"""
Analiza distribución de keywords por seed
"""

import pandas as pd

# Leer CSV consolidado
df = pd.read_csv('reports/keywords_consolidated_top150.csv')

print("=" * 60)
print("ANÁLISIS DE DISTRIBUCIÓN - Top 150 Keywords")
print("=" * 60)

# Distribución por seed
print("\n1. DISTRIBUCIÓN POR SEED:")
dist = df.groupby('source_seed').size().sort_values(ascending=False)
for seed, count in dist.items():
    pct = (count / len(df)) * 100
    print(f"   {seed:30s}: {count:3d} ({pct:5.1f}%)")

# Keywords de PI
print("\n2. KEYWORDS PROPIEDAD INTELECTUAL (Top 10):")
pi_kw = df[df['source_seed'] == 'Propiedad-intelectual'].head(10)
for idx, row in pi_kw.iterrows():
    print(f"   - {row['Keyword']:50s} | Vol: {row['Volume']:5d} | Score: {row['relevance_score']:2d}")

# Keywords de Registro Marca
print("\n3. KEYWORDS REGISTRO DE MARCA (Todas):")
reg_kw = df[df['source_seed'] == 'registro-de-marca-colombia']
for idx, row in reg_kw.iterrows():
    print(f"   - {row['Keyword']:50s} | Vol: {row['Volume']:5d} | Score: {row['relevance_score']:2d}")

# Posiciones en el ranking
print("\n4. POSICIONES EN EL RANKING:")
for seed in ['fintech', 'Propiedad-intelectual', 'registro-de-marca-colombia', 'saas']:
    positions = df[df['source_seed'] == seed].index.tolist()
    first_pos = positions[0] + 1 if positions else 0
    last_pos = positions[-1] + 1 if positions else 0
    print(f"   {seed:30s}: Posición #{first_pos:3d} a #{last_pos:3d}")

print("\n" + "=" * 60)
