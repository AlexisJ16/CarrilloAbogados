#!/usr/bin/env python3
"""
Keyword Magic Tool Data Processor
Consolida y filtra 17K+ keywords a top 100-150 relevantes para Keywords_Master
"""

import pandas as pd
import re
from pathlib import Path

# Configuración
DATA_DIR = Path("semrush/keyword_magic_tool")
OUTPUT_DIR = Path("reports")
OUTPUT_DIR.mkdir(exist_ok=True)

# Filtros Motor Futuro (70% del contenido objetivo)
MOTOR_FUTURO_KEYWORDS = [
    # Tech/Startup (prioridad alta)
    'startup', 'app', 'software', 'saas', 'fintech', 'plataforma', 'digital',
    'tecnología', 'tech', 'innovación', 'emprendimiento', 'emprendedor',

    # IA y ML (prioridad alta - diferenciador clave)
    'ia', 'inteligencia artificial', 'machine learning', 'algoritmo', 'chatgpt',
    'automatización', 'ai', 'código', 'datos', 'blockchain',

    # Propiedad Intelectual Tech
    'patente software', 'registro marca', 'derechos autor', 'copyright',
    'propiedad intelectual', 'protección', 'licencia',

    # Legal Tech
    'legal tech', 'legaltech', 'automatización legal', 'contrato inteligente'
]

# Filtros Genéricos (30% del contenido)
GENERIC_KEYWORDS = [
    'sic', 'superintendencia', 'marca', 'registro', 'patente',
    'derechos autor', 'diseño industrial', 'abogado', 'consultoría',
    'licitación', 'contratación', 'normativa', 'regulación'
]

# Filtros de Exclusión (servicios NO ofrecidos)
EXCLUDE_KEYWORDS = [
    'laboral', 'salario', 'pensiones', 'despido', 'liquidación',
    'divorcio', 'penal', 'criminal', 'familia', 'alimentos',
    'custodia', 'adopción', 'testamento', 'sucesión'
]

def load_all_csvs():
    """Carga y concatena todos los CSVs"""
    csv_files = list(DATA_DIR.glob("*_broad-match_co_2026-02-13.csv"))
    print(f"\n[*] Cargando {len(csv_files)} archivos CSV...")

    dfs = []
    for csv_file in csv_files:
        df = pd.read_csv(csv_file)
        df['source_seed'] = csv_file.stem.replace('_broad-match_co_2026-02-13', '')
        dfs.append(df)
        print(f"  [OK] {csv_file.name}: {len(df)} keywords")

    combined = pd.concat(dfs, ignore_index=True)
    print(f"\n[OK] Total keywords cargadas: {len(combined):,}")
    return combined

def clean_data(df):
    """Limpia y normaliza datos"""
    print("\n[*] Limpiando datos...")

    # Normalizar columnas PRIMERO (antes de eliminar)
    df['Volume'] = pd.to_numeric(df['Volume'], errors='coerce').fillna(0).astype(int)
    df['Keyword Difficulty'] = pd.to_numeric(df['Keyword Difficulty'], errors='coerce').fillna(50).astype(int)  # Asumir dificultad media si falta
    df['CPC (USD)'] = pd.to_numeric(df['CPC (USD)'], errors='coerce').fillna(0).astype(float)

    # Eliminar duplicados por keyword
    before = len(df)
    df = df.drop_duplicates(subset=['Keyword'], keep='first')
    print(f"  [OK] Duplicados eliminados: {before - len(df):,}")

    # Eliminar SOLO keywords sin volumen (KD puede faltar)
    df = df[df['Volume'] > 0]

    print(f"  [OK] Keywords validas (Volume > 0): {len(df):,}")
    return df

def score_keyword(row):
    """Calcula score de relevancia (0-100)"""
    score = 0
    keyword_lower = row['Keyword'].lower()

    # Motor Futuro (hasta +50 puntos)
    motor_futuro_matches = sum(1 for kw in MOTOR_FUTURO_KEYWORDS if kw in keyword_lower)
    score += min(motor_futuro_matches * 10, 50)

    # Genérico relevante (hasta +20 puntos)
    generic_matches = sum(1 for kw in GENERIC_KEYWORDS if kw in keyword_lower)
    score += min(generic_matches * 5, 20)

    # Volumen (hasta +20 puntos)
    if row['Volume'] >= 1000:
        score += 20
    elif row['Volume'] >= 500:
        score += 15
    elif row['Volume'] >= 100:
        score += 10
    elif row['Volume'] >= 50:
        score += 5

    # Dificultad baja = quick win (hasta +10 puntos)
    if row['Keyword Difficulty'] <= 20:
        score += 10
    elif row['Keyword Difficulty'] <= 30:
        score += 5

    # Intent comercial/transaccional (+5 puntos)
    if pd.notna(row['Intent']) and ('Commercial' in str(row['Intent']) or 'Transactional' in str(row['Intent'])):
        score += 5

    # Penalizar keywords excluidas (-100)
    if any(excl in keyword_lower for excl in EXCLUDE_KEYWORDS):
        score = -100

    return score

def filter_and_rank(df):
    """Filtra y rankea keywords"""
    print("\n[*] Calculando scores de relevancia...")

    # Calcular score
    df['relevance_score'] = df.apply(score_keyword, axis=1)

    # Eliminar keywords con score negativo (excluidas)
    df = df[df['relevance_score'] >= 0]

    # Ordenar por score, luego volumen
    df = df.sort_values(['relevance_score', 'Volume'], ascending=[False, False])

    print(f"  [OK] Keywords con score > 0: {len(df):,}")
    print(f"  [OK] Score promedio: {df['relevance_score'].mean():.1f}")
    print(f"  [OK] Score maximo: {df['relevance_score'].max()}")

    return df

def generate_report(df_top):
    """Genera informe de análisis"""
    report = f"""# Keywords Magic Tool - Análisis Consolidado

**Fecha:** 13 Feb 2026
**Seeds procesados:** registro-de-marca-colombia, Propiedad-intelectual, saas, fintech
**Keywords totales:** {len(df_top):,}

## 📊 Top 20 Keywords por Score

| Keyword | Volume | KD% | CPC | Intent | Score | Seed |
|---------|--------|-----|-----|--------|-------|------|
"""

    for idx, row in df_top.head(20).iterrows():
        report += f"| {row['Keyword']} | {row['Volume']:,} | {row['Keyword Difficulty']} | ${row['CPC (USD)']:.2f} | {row['Intent']} | {row['relevance_score']} | {row['source_seed']} |\n"

    report += f"""

## 🎯 Distribución por Score

| Rango Score | Cantidad |
|-------------|----------|
"""

    for threshold in [80, 60, 40, 20, 0]:
        count = len(df_top[df_top['relevance_score'] >= threshold])
        report += f"| ≥{threshold} | {count} |\n"

    report += f"""

## 📈 Distribución por Volumen

| Rango Volumen | Cantidad |
|---------------|----------|
| ≥1000 | {len(df_top[df_top['Volume'] >= 1000])} |
| 500-999 | {len(df_top[(df_top['Volume'] >= 500) & (df_top['Volume'] < 1000)])} |
| 100-499 | {len(df_top[(df_top['Volume'] >= 100) & (df_top['Volume'] < 500)])} |
| 50-99 | {len(df_top[(df_top['Volume'] >= 50) & (df_top['Volume'] < 100)])} |
| <50 | {len(df_top[df_top['Volume'] < 50])} |

## 🎯 Distribución por Dificultad (Quick Wins)

| Rango KD% | Cantidad |
|-----------|----------|
| 0-20 (Easy) | {len(df_top[df_top['Keyword Difficulty'] <= 20])} |
| 21-30 (Medium) | {len(df_top[(df_top['Keyword Difficulty'] > 20) & (df_top['Keyword Difficulty'] <= 30)])} |
| 31-40 (Hard) | {len(df_top[(df_top['Keyword Difficulty'] > 30) & (df_top['Keyword Difficulty'] <= 40)])} |
| 41+ (Very Hard) | {len(df_top[df_top['Keyword Difficulty'] > 40])} |

## 🚀 Quick Wins Identificados

**Criterio:** Score ≥60, Volume ≥100, KD% ≤30

"""

    quick_wins = df_top[
        (df_top['relevance_score'] >= 60) &
        (df_top['Volume'] >= 100) &
        (df_top['Keyword Difficulty'] <= 30)
    ]

    report += f"**Total Quick Wins:** {len(quick_wins)}\n\n"

    if len(quick_wins) > 0:
        report += "| Keyword | Volume | KD% | Score |\n"
        report += "|---------|--------|-----|-------|\n"
        for idx, row in quick_wins.head(15).iterrows():
            report += f"| {row['Keyword']} | {row['Volume']:,} | {row['Keyword Difficulty']} | {row['relevance_score']} |\n"

    report += f"""

## 🎯 Keywords Motor Futuro

**Criterio:** Contiene keywords tech (startup, app, saas, fintech, ia, software, etc.)

**Total:** {len(df_top[df_top['relevance_score'] >= 40])}

"""

    motor_futuro = df_top[df_top['relevance_score'] >= 40].head(20)
    report += "| Keyword | Volume | KD% | Score |\n"
    report += "|---------|--------|-----|-------|\n"
    for idx, row in motor_futuro.iterrows():
        report += f"| {row['Keyword']} | {row['Volume']:,} | {row['Keyword Difficulty']} | {row['relevance_score']} |\n"

    return report

def main():
    """Proceso principal"""
    print("=" * 60)
    print("  Keyword Magic Tool Processor v1.0")
    print("  Carrillo Abogados - MW#3 SEO Content Factory")
    print("=" * 60)

    # Cargar datos
    df = load_all_csvs()

    # Limpiar
    df = clean_data(df)

    # Filtrar y rankear
    df = filter_and_rank(df)

    # Top 150 keywords
    df_top = df.head(150)

    # Exportar CSV consolidado
    output_csv = OUTPUT_DIR / "keywords_consolidated_top150.csv"
    df_top.to_csv(output_csv, index=False, encoding='utf-8-sig')
    print(f"\n[OK] CSV consolidado guardado: {output_csv}")

    # Generar informe
    report = generate_report(df_top)
    report_file = OUTPUT_DIR / "keywords_magic_tool_analysis.md"
    with open(report_file, 'w', encoding='utf-8') as f:
        f.write(report)
    print(f"[OK] Informe generado: {report_file}")

    print("\n" + "=" * 60)
    print("[OK] Procesamiento completado")
    print("=" * 60)
    print(f"\n[*] Resumen:")
    print(f"  - Keywords procesadas: {len(df):,}")
    print(f"  - Top keywords seleccionadas: {len(df_top)}")
    print(f"  - Quick wins identificados: {len(df_top[(df_top['relevance_score'] >= 60) & (df_top['Volume'] >= 100) & (df_top['Keyword Difficulty'] <= 30)])}")
    print(f"  - Motor Futuro keywords: {len(df_top[df_top['relevance_score'] >= 40])}")
    print(f"\n[*] Archivos generados:")
    print(f"  - {output_csv}")
    print(f"  - {report_file}")

if __name__ == "__main__":
    main()
