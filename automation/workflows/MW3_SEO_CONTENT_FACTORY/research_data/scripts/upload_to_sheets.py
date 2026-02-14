#!/usr/bin/env python3
"""
Upload Keywords to Google Sheets (Keywords_Master)
Transforma CSV consolidado → formato Keywords_Master
"""

import pandas as pd
from datetime import datetime
import re

# IDs de Google Sheets
KEYWORDS_MASTER_ID = "15RmVB34VnwxdJ9Ne-HX4WU54yv0kUn7WOvcjWdUL6to"
MAES_RESEARCH_ID = "155udZF7WtMsyDaSGCJZ3_7e4xZXpQm9p-oZGqve_VN8"

# Prefijos de IDs por seed
SEED_PREFIXES = {
    'fintech': 'FIN',
    'saas': 'SAA',
    'Propiedad-intelectual': 'PRO',
    'registro-de-marca-colombia': 'REG'
}

def load_consolidated_csv():
    """Carga CSV consolidado"""
    df = pd.read_csv('reports/keywords_consolidated_top150.csv')
    print(f"[*] Cargadas {len(df)} keywords del CSV consolidado")
    return df

def transform_to_keywords_master(df):
    """Transforma CSV → formato Keywords_Master"""
    print("\n[*] Transformando datos al formato Keywords_Master...")

    rows = []
    seed_counters = {prefix: 1 for prefix in SEED_PREFIXES.values()}

    for idx, row in df.iterrows():
        # Generar ID único
        prefix = SEED_PREFIXES.get(row['source_seed'], 'OTH')
        kw_id = f"KW-{prefix}-{seed_counters[prefix]:03d}"
        seed_counters[prefix] += 1

        # Determinar cluster_name desde Groups o source_seed
        cluster = row['source_seed']  # Fallback
        if pd.notna(row['Groups']):
            # Tomar primer grupo como cluster
            groups = str(row['Groups']).split(',')
            cluster = groups[0].strip() if groups else row['source_seed']

        # Determinar is_main_keyword (score alto + volumen alto)
        is_main = row['relevance_score'] >= 40 and row['Volume'] >= 500

        # Normalizar intent
        intent = str(row['Intent']) if pd.notna(row['Intent']) else 'Informational'

        # Timestamp Excel (days since 1900-01-01)
        excel_date = 46060  # Aproximado: 13 Feb 2026

        # Construir fila
        new_row = [
            kw_id,  # keyword_id
            row['Keyword'],  # keyword_text
            cluster,  # cluster_name
            is_main,  # is_main_keyword
            intent,  # intent
            int(row['Volume']),  # volume
            int(row['Keyword Difficulty']),  # kd
            float(row['CPC (USD)']),  # cpc
            0.0,  # competitive_density (calculado después)
            str(row['SERP Features']) if pd.notna(row['SERP Features']) else '',  # serp_features
            'estable',  # trend (default)
            int(row['relevance_score']),  # priority_score
            True,  # enabled
            'pendiente',  # status
            '',  # url_target (a definir)
            f"Keyword de investigacion SEMrush - seed: {row['source_seed']}",  # brief_notes
            '',  # content_id
            '',  # published_url
            'semrush_magic_tool',  # source
            excel_date,  # created_at
            excel_date  # updated_at
        ]

        rows.append(new_row)

    print(f"[OK] {len(rows)} keywords transformadas")
    return rows

def generate_batch_data(rows, batch_size=100):
    """Divide keywords en batches para carga a Sheets"""
    batches = []
    for i in range(0, len(rows), batch_size):
        batch = rows[i:i+batch_size]
        batches.append(batch)
    return batches

def generate_output_for_manual_upload(rows):
    """Genera CSV para carga manual"""
    columns = [
        'keyword_id', 'keyword_text', 'cluster_name', 'is_main_keyword', 'intent',
        'volume', 'kd', 'cpc', 'competitive_density', 'serp_features', 'trend',
        'priority_score', 'enabled', 'status', 'url_target', 'brief_notes',
        'content_id', 'published_url', 'source', 'created_at', 'updated_at'
    ]

    df_output = pd.DataFrame(rows, columns=columns)
    output_file = 'reports/keywords_for_sheets_upload.csv'
    df_output.to_csv(output_file, index=False, encoding='utf-8-sig')
    print(f"\n[OK] CSV generado: {output_file}")
    print(f"[*] Subir manualmente a Google Sheets o usar API")

    return output_file

def generate_report(rows):
    """Genera resumen de carga"""
    df = pd.DataFrame(rows)

    report = f"""# Carga de Keywords a Google Sheets

**Fecha:** {datetime.now().strftime('%d %b %Y %H:%M')}
**Total keywords:** {len(rows)}

## Distribución por Seed

| Seed | Cantidad |
|------|----------|
"""

    seed_counts = {}
    for row in rows:
        # keyword_text está en índice 1
        seed_id = row[0].split('-')[1]  # KW-FIN-001 → FIN
        seed_counts[seed_id] = seed_counts.get(seed_id, 0) + 1

    for seed, count in sorted(seed_counts.items()):
        report += f"| {seed} | {count} |\n"

    report += f"""

## Distribución por Cluster

| Cluster | Cantidad |
|---------|----------|
"""

    cluster_counts = {}
    for row in rows:
        cluster = row[2]  # cluster_name
        cluster_counts[cluster] = cluster_counts.get(cluster, 0) + 1

    # Top 10 clusters
    for cluster, count in sorted(cluster_counts.items(), key=lambda x: x[1], reverse=True)[:10]:
        report += f"| {cluster} | {count} |\n"

    report += f"""

## Main Keywords (is_main_keyword = TRUE)

**Total:** {sum(1 for row in rows if row[3])}  # is_main_keyword es índice 3

| Keyword | Volume | KD | Score |
|---------|--------|-----|-------|
"""

    main_keywords = [row for row in rows if row[3]][:20]
    for row in main_keywords:
        report += f"| {row[1]} | {row[5]:,} | {row[6]} | {row[11]} |\n"

    report += f"""

## Próximos Pasos

1. Revisar archivo `keywords_for_sheets_upload.csv`
2. **Opción A:** Importar manualmente a Keywords_Master (All_Keywords tab)
3. **Opción B:** Usar MCP google-sheets tool para carga automática (sheets_append_rows)
4. Verificar que no hay duplicados con keywords existentes
5. Ajustar cluster_names y url_target según arquitectura de contenido

## Notas

- IDs generados: KW-FIN-XXX (fintech), KW-SAA-XXX (saas), KW-PRO-XXX (propiedad intelectual), KW-REG-XXX (registro marca)
- is_main_keyword = TRUE si score ≥40 AND volume ≥500
- competitive_density = 0.0 (pendiente cálculo)
- url_target vacío (pendiente definir arquitectura URLs)
"""

    report_file = 'reports/keywords_upload_report.md'
    with open(report_file, 'w', encoding='utf-8') as f:
        f.write(report)

    print(f"[OK] Reporte generado: {report_file}")

def main():
    print("=" * 60)
    print("  Keywords Upload to Google Sheets")
    print("  Keywords_Master: All_Keywords tab")
    print("=" * 60)

    # Cargar CSV
    df = load_consolidated_csv()

    # Transformar
    rows = transform_to_keywords_master(df)

    # Generar CSV para carga
    output_file = generate_output_for_manual_upload(rows)

    # Generar reporte
    generate_report(rows)

    print("\n" + "=" * 60)
    print("[OK] Proceso completado")
    print("=" * 60)
    print(f"\n[*] Próximo paso:")
    print(f"  1. Revisar: {output_file}")
    print(f"  2. Subir a Keywords_Master (All_Keywords) manualmente o con MCP tool")
    print(f"  3. Revisar reporte: reports/keywords_upload_report.md")

if __name__ == "__main__":
    main()
