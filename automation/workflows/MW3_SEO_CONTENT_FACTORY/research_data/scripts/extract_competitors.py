#!/usr/bin/env python3
"""
Extractor de competidores de datos SEMrush
Usa pandas para manejo robusto de CSV
"""

import pandas as pd
import re
from collections import Counter

def extract_domains_from_json(text):
    """Extrae dominios de campo JSON"""
    if pd.isna(text) or text == '':
        return []

    # Patrón para extraer dominios
    pattern = r'"([a-zA-Z0-9.-]+\.[a-z]{2,})":'
    domains = re.findall(pattern, str(text))

    return domains

def main():
    print("[*] Leyendo CSV con pandas...")

    # Leer CSV
    filepath = 'C:\\CarrilloAbogados\\automation\\workflows\\MW3_SEO_CONTENT_FACTORY\\research_data\\semrush_kw_strategy_130kw.csv'

    # Intentar leer con diferentes configuraciones
    try:
        # Primero intentar lectura estándar
        df = pd.read_csv(filepath, encoding='utf-8')
        print(f"[OK] {len(df)} filas leídas")
        print(f"[INFO] Columnas: {list(df.columns)}")

        # Verificar si existen las columnas esperadas
        if 'Content references' not in df.columns and 'Competitors' not in df.columns:
            print("[WARN] No se encontraron columnas de competidores con nombres esperados")
            print(f"[INFO] Columnas disponibles: {list(df.columns)}")

            # Buscar columnas que puedan contener URLs
            url_columns = [col for col in df.columns if 'ref' in col.lower() or 'compet' in col.lower()]
            print(f"[INFO] Columnas potenciales: {url_columns}")

        # Extraer dominios
        all_domains = []

        # Enfocarse en las dos columnas específicas
        target_cols = ['Content references', 'Competitors']

        for col in target_cols:
            if col in df.columns:
                print(f"[*] Procesando columna: {col}")
                non_null = df[col].dropna()
                print(f"[INFO] {len(non_null)} valores no nulos en '{col}'")

                for idx, value in enumerate(non_null):
                    if idx < 2:  # Mostrar primeros 2 para debug
                        print(f"[DEBUG] Valor {idx}: {str(value)[:100]}...")

                    domains = extract_domains_from_json(value)
                    if domains:
                        all_domains.extend(domains)

                print(f"[OK] {len([d for v in non_null for d in extract_domains_from_json(v)])} dominios extraídos de '{col}'")

        if not all_domains:
            print("[ERROR] No se encontraron dominios en ninguna columna")
            print("[INFO] Mostrando primeras 5 filas:")
            print(df.head())
            return

        # Contar y rankear
        counter = Counter(all_domains)
        top_competitors = counter.most_common(30)

        print(f"\n[OK] {len(all_domains)} apariciones de dominios encontradas")
        print(f"[OK] {len(counter)} dominios únicos")

        print("\n[TOP] Top 30 Competidores:")
        print("-" * 70)
        print(f"{'Rank':<5} {'Domain':<45} {'Count':>8} {'Share %':>10}")
        print("-" * 70)

        total = sum(c[1] for c in top_competitors)
        for i, (domain, count) in enumerate(top_competitors, 1):
            share = (count / total * 100) if total > 0 else 0
            print(f"{i:<5} {domain:<45} {count:>8} {share:>9.2f}%")

        # Guardar a CSV
        output_file = 'C:\\CarrilloAbogados\\automation\\workflows\\MW3_SEO_CONTENT_FACTORY\\research_data\\competitors_ranking.csv'
        with open(output_file, 'w', newline='', encoding='utf-8') as f:
            f.write('Rank,Domain,Keyword_Count,Market_Share_%\n')
            for i, (domain, count) in enumerate(top_competitors, 1):
                share = (count / total * 100) if total > 0 else 0
                f.write(f'{i},{domain},{count},{share:.2f}\n')

        print(f"\n[OK] Guardado: {output_file}")

    except Exception as e:
        print(f"[ERROR] {type(e).__name__}: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
