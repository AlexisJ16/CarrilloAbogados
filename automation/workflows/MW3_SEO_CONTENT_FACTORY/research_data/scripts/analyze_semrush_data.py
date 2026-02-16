#!/usr/bin/env python3
"""
Análisis de datos SEMrush para Carrillo Abogados
Procesa keywords y competidores de estrategia SEO
"""

import csv
import json
import re
from collections import Counter, defaultdict
from typing import Dict, List, Tuple

def clean_json_field(field: str) -> List[str]:
    """Extrae dominios de campos JSON con URLs"""
    domains = []
    if not field or field.strip() == '':
        return domains

    # Buscar patrones de dominio en el JSON
    domain_pattern = r'"([a-zA-Z0-9.-]+\.[a-z]{2,})":'
    matches = re.findall(domain_pattern, field)
    domains.extend(matches)

    return domains

def parse_csv_with_multiline(filepath: str) -> List[Dict]:
    """Parse CSV con manejo de campos multilinea"""
    keywords = []
    current_row = None

    with open(filepath, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    header = lines[0].strip().split(',')

    for line in lines[1:]:
        # Si la línea empieza con "co," es una nueva keyword
        if line.startswith('co,'):
            # Guardar la keyword anterior si existe
            if current_row:
                keywords.append(current_row)

            # Parsear nueva keyword
            parts = line.strip().split(',')
            current_row = {
                'database': parts[0] if len(parts) > 0 else '',
                'keyword': parts[1] if len(parts) > 1 else '',
                'volume': int(parts[7]) if len(parts) > 7 and parts[7].isdigit() else 0,
                'difficulty': int(parts[8]) if len(parts) > 8 and parts[8].replace('.','').isdigit() else 0,
                'cpc': float(parts[9]) if len(parts) > 9 and parts[9].replace('.','').isdigit() else 0.0,
                'intent': parts[12] if len(parts) > 12 else '',
                'trend': parts[14] if len(parts) > 14 else '',
                'click_potential': parts[15] if len(parts) > 15 else '',
                'content_refs': '',
                'competitors': ''
            }

            # Capturar content_refs y competitors si están en la misma línea
            if len(parts) > 16:
                current_row['content_refs'] = ','.join(parts[16:])
        else:
            # Líneas de continuación (JSON multilinea)
            if current_row:
                current_row['competitors'] += line.strip()

    # Agregar la última keyword
    if current_row:
        keywords.append(current_row)

    return keywords

def analyze_competitors(keywords: List[Dict]) -> List[Tuple[str, int]]:
    """Extrae y rankea competidores por frecuencia"""
    all_domains = []

    for kw in keywords:
        # Extraer de content_refs
        domains = clean_json_field(kw.get('content_refs', ''))
        all_domains.extend(domains)

        # Extraer de competitors
        domains = clean_json_field(kw.get('competitors', ''))
        all_domains.extend(domains)

    # Contar y rankear
    counter = Counter(all_domains)
    return counter.most_common(30)

def segment_by_intent(keywords: List[Dict]) -> Dict[str, List[Dict]]:
    """Segmenta keywords por intent"""
    segments = defaultdict(list)

    for kw in keywords:
        intent = kw.get('intent', '').strip()
        # Limpiar intents con comillas o múltiples valores
        if ',' in intent:
            intent = intent.split(',')[0].strip().strip('"')
        else:
            intent = intent.strip('"')

        if not intent:
            intent = 'Unknown'

        segments[intent].append(kw)

    return dict(segments)

def calculate_opportunity_score(kw: Dict) -> float:
    """
    Score de oportunidad: volumen alto + dificultad baja = mejor
    Formula: (Volume / 100) * (100 - Difficulty) / 100
    """
    volume = kw.get('volume', 0)
    difficulty = kw.get('difficulty', 100)

    if volume == 0:
        return 0

    # Normalizar volumen (logarítmico para evitar sesgo extremo)
    volume_score = min(volume / 100, 100)

    # Invertir dificultad (menor es mejor)
    difficulty_score = (100 - difficulty) / 100

    return volume_score * difficulty_score

def identify_opportunities(keywords: List[Dict], top_n: int = 30) -> List[Dict]:
    """Identifica top keywords por oportunidad"""
    # Calcular score para cada keyword
    for kw in keywords:
        kw['opportunity_score'] = calculate_opportunity_score(kw)

    # Ordenar por score descendente
    sorted_kws = sorted(keywords, key=lambda x: x['opportunity_score'], reverse=True)

    return sorted_kws[:top_n]

def main():
    print("[*] Analizando datos SEMrush...")

    # Leer CSV
    filepath = 'C:\\CarrilloAbogados\\automation\\workflows\\MW3_SEO_CONTENT_FACTORY\\research_data\\semrush_kw_strategy_130kw.csv'
    keywords = parse_csv_with_multiline(filepath)
    print(f"[OK] {len(keywords)} keywords encontradas")

    # 1. Análisis de competidores
    print("\n[*] Analizando competidores...")
    competitors = analyze_competitors(keywords)

    print("\n[TOP] Top 20 Competidores:")
    print("-" * 60)
    for i, (domain, count) in enumerate(competitors[:20], 1):
        print(f"{i:2d}. {domain:40s} - {count:3d} keywords")

    # Guardar a CSV
    with open('C:\\CarrilloAbogados\\automation\\workflows\\MW3_SEO_CONTENT_FACTORY\\research_data\\competitors_ranking.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['Rank', 'Domain', 'Keyword_Count', 'Market_Share_%'])
        total_appearances = sum(c[1] for c in competitors)
        for i, (domain, count) in enumerate(competitors, 1):
            share = (count / total_appearances * 100) if total_appearances > 0 else 0
            writer.writerow([i, domain, count, f"{share:.2f}"])

    print("\n[OK] Guardado: competitors_ranking.csv")

    # 2. Segmentación por intent
    print("\n[*] Segmentando por intent...")
    segments = segment_by_intent(keywords)

    print("\n[INFO] Keywords por Intent:")
    print("-" * 60)
    for intent, kws in sorted(segments.items(), key=lambda x: len(x[1]), reverse=True):
        print(f"{intent:20s}: {len(kws):3d} keywords")

    # Guardar segmentos
    for intent, kws in segments.items():
        safe_intent = intent.replace('/', '-').replace(' ', '_')
        filename = f'C:\\CarrilloAbogados\\automation\\workflows\\MW3_SEO_CONTENT_FACTORY\\research_data\\keywords_{safe_intent}.csv'

        with open(filename, 'w', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow(['Keyword', 'Volume', 'Difficulty', 'CPC', 'Trend', 'Click_Potential'])
            for kw in kws:
                writer.writerow([
                    kw['keyword'],
                    kw['volume'],
                    kw['difficulty'],
                    kw['cpc'],
                    kw.get('trend', ''),
                    kw.get('click_potential', '')
                ])

    print(f"[OK] Guardados {len(segments)} archivos de segmentos")

    # 3. Top oportunidades
    print("\n[*] Identificando oportunidades...")
    opportunities = identify_opportunities(keywords, top_n=30)

    print("\n[TOP] Top 30 Keywords por Oportunidad:")
    print("-" * 80)
    print(f"{'#':<3} {'Keyword':<45} {'Vol':>6} {'KD':>4} {'Score':>6}")
    print("-" * 80)
    for i, kw in enumerate(opportunities, 1):
        print(f"{i:<3} {kw['keyword'][:45]:<45} {kw['volume']:>6} {kw['difficulty']:>4} {kw['opportunity_score']:>6.2f}")

    # Guardar oportunidades
    with open('C:\\CarrilloAbogados\\automation\\workflows\\MW3_SEO_CONTENT_FACTORY\\research_data\\top_opportunities.csv', 'w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['Rank', 'Keyword', 'Volume', 'Difficulty', 'CPC', 'Intent', 'Opportunity_Score', 'Click_Potential'])
        for i, kw in enumerate(opportunities, 1):
            writer.writerow([
                i,
                kw['keyword'],
                kw['volume'],
                kw['difficulty'],
                kw['cpc'],
                kw['intent'],
                f"{kw['opportunity_score']:.2f}",
                kw.get('click_potential', '')
            ])

    print("\n[OK] Guardado: top_opportunities.csv")

    # Resumen final
    print("\n" + "="*80)
    print("RESUMEN DE ARCHIVOS GENERADOS:")
    print("="*80)
    print("1. competitors_ranking.csv - Top 30 competidores rankeados")
    print(f"2. keywords_{{intent}}.csv - {len(segments)} archivos segmentados por intent")
    print("3. top_opportunities.csv - Top 30 keywords por oportunidad")
    print("\n[OK] Analisis completado!")

if __name__ == "__main__":
    main()
