#!/usr/bin/env python3
"""
Reorganiza research_data/ según plan v3.0
"""

import os
import shutil
from pathlib import Path

BASE_DIR = Path('.')

# Crear estructura según plan v3.0
print("=" * 60)
print("Reorganizando research_data/ según Plan v3.0")
print("=" * 60)

# 1. Crear carpetas si no existen
folders_to_create = [
    'scripts',           # Scripts Python de procesamiento
    'temp',              # Archivos temporales
    'semrush/keyword_overview',
    'semrush/gap_entre_competidores',
    'competidores_data'  # Data bruta manual (futuro)
]

for folder in folders_to_create:
    folder_path = BASE_DIR / folder
    folder_path.mkdir(parents=True, exist_ok=True)
    print(f"[OK] Carpeta creada/verificada: {folder}/")

# 2. Mover scripts Python a scripts/
python_files = list(BASE_DIR.glob('*.py'))
if python_files:
    print(f"\n[*] Moviendo {len(python_files)} scripts Python a scripts/...")
    for py_file in python_files:
        if py_file.name != 'organize_research_data.py':  # No mover este mismo script
            dest = BASE_DIR / 'scripts' / py_file.name
            if not dest.exists():
                shutil.move(str(py_file), str(dest))
                print(f"    - {py_file.name}")

# 3. Mover archivos temporales a temp/
temp_patterns = ['*.json', 'batch*.csv', 'batch*.txt', 'mcp_*.txt']
temp_files = []
for pattern in temp_patterns:
    temp_files.extend(BASE_DIR.glob(pattern))
    temp_files.extend((BASE_DIR / 'reports').glob(pattern))

if temp_files:
    print(f"\n[*] Moviendo {len(temp_files)} archivos temporales a temp/...")
    for temp_file in temp_files:
        dest = BASE_DIR / 'temp' / temp_file.name
        if not dest.exists() and temp_file.exists():
            shutil.move(str(temp_file), str(dest))
            print(f"    - {temp_file.name}")

# 4. Limpiar reports/ - mantener solo informes finales
reports_to_keep = [
    'keywords_magic_tool_analysis.md',
    'keywords_upload_report.md',
    'RESUMEN_INVESTIGACION_KEYWORDS.md',
    'keywords_consolidated_top150.csv',
    'keywords_for_sheets_upload.csv',
    'keywords_remaining_for_manual_import.tsv',
    'keywords_remaining_for_manual_import.csv'
]

reports_dir = BASE_DIR / 'reports'
if reports_dir.exists():
    all_reports = list(reports_dir.glob('*'))
    to_move = [f for f in all_reports if f.name not in reports_to_keep and f.is_file()]

    if to_move:
        print(f"\n[*] Moviendo {len(to_move)} archivos no esenciales de reports/ a temp/...")
        for f in to_move:
            dest = BASE_DIR / 'temp' / f.name
            if not dest.exists():
                shutil.move(str(f), str(dest))
                print(f"    - {f.name}")

# 5. Generar README.md
readme_content = """# Research Data - MW3 SEO Content Factory

**Última actualización:** 13 Feb 2026
**Estado:** MAES Phase 4 (Competition Analysis) - 60% Completado

---

## 📁 ESTRUCTURA DE CARPETAS

```
research_data/
├── semrush/                          # Datos de SEMrush Pro
│   ├── keyword_magic_tool/           # ✅ Keywords expandidas desde seeds
│   │   ├── fintech_broad-match_co_2026-02-13.csv (3,621 kw)
│   │   ├── Propiedad-intelectual_broad-match_co_2026-02-13.csv (7,848 kw)
│   │   ├── registro-de-marca-colombia_broad-match_co_2026-02-13.csv (587 kw)
│   │   └── saas_broad-match_co_2026-02-13.csv (5,253 kw)
│   ├── keyword_overview/             # ⏳ PENDIENTE - Bulk metrics de 150 kw
│   │   └── bulk_keywords_metrics.xlsx
│   └── gap_entre_competidores/       # ⏳ PENDIENTE - Keyword Gap 4 competidores
│       └── gap_competidores.csv
├── reports/                          # Informes finales
│   ├── RESUMEN_INVESTIGACION_KEYWORDS.md  # 📊 Informe ejecutivo
│   ├── keywords_magic_tool_analysis.md     # Análisis estadístico
│   ├── keywords_upload_report.md           # Reporte de transformación
│   ├── keywords_consolidated_top150.csv    # Top 150 keywords rankeadas
│   ├── keywords_for_sheets_upload.csv      # Formato Keywords_Master
│   └── keywords_remaining_for_manual_import.tsv/csv  # Para importar manual
├── scripts/                          # Scripts Python de procesamiento
│   ├── process_keywords.py           # Consolidación y filtrado
│   ├── upload_to_sheets.py           # Transformación a formato Keywords_Master
│   └── analyze_distribution.py       # Análisis de distribución
├── temp/                             # Archivos temporales (no críticos)
│   ├── *.json                        # Batches JSON
│   └── batch*.csv                    # Archivos de prueba
└── competidores_data/                # ⏳ PENDIENTE - Data bruta manual
    ├── olarte_semrush_notes.md
    ├── lois_semrush_notes.md
    ├── cardenasvega_semrush_notes.md
    └── casasantofimio_semrush_notes.md
```

---

## ✅ ARCHIVOS COMPLETADOS

### SEMrush Magic Tool (4 seeds)
- **fintech**: 3,621 keywords
- **Propiedad-intelectual**: 7,848 keywords
- **registro-de-marca-colombia**: 587 keywords
- **saas**: 5,253 keywords
- **TOTAL RAW**: 17,309 keywords

### Keywords Procesadas
- **Válidas**: 2,069 keywords (volumen > 0)
- **Top 150**: Seleccionadas por score + relevancia
- **Cargadas a Sheets**: 150 keywords en Keywords_Master

### Informes Generados
1. `RESUMEN_INVESTIGACION_KEYWORDS.md` - Informe ejecutivo completo
2. `keywords_magic_tool_analysis.md` - Análisis estadístico
3. `keywords_upload_report.md` - Reporte de transformación

---

## ⏳ ARCHIVOS PENDIENTES (MAES Phase 4 - 40%)

### 1. Keyword Overview (Bulk Metrics)
**Archivo:** `semrush/keyword_overview/bulk_keywords_metrics.xlsx`
**Herramienta:** SEMrush → Keyword Research → Keyword Overview → Analyze multiple keywords
**Contenido:** Métricas completas de las 150 keywords (Volume, KD%, CPC, Trend, SERP Features)

### 2. Keyword Gap (4 Competidores)
**Archivo:** `semrush/gap_entre_competidores/gap_competidores.csv`
**Herramienta:** SEMrush → Competitive Research → Keyword Gap
**Dominios:** olartemoure.com, lois.com.co, cardenasvega.com, casasantofimio.co
**Contenido:** Keywords compartidas/perdidas entre competidores

---

## 🎯 PRÓXIMOS PASOS

1. ✅ Keywords Magic Tool procesadas
2. ✅ Top 150 keywords cargadas a Keywords_Master
3. ⏳ Extraer Bulk Keyword Overview (150 kw con métricas completas)
4. ⏳ Extraer Keyword Gap (4 competidores)
5. ⏳ Actualizar MAES_Research Google Sheet
6. ⏳ Generar informe consolidado final

---

## 📊 MÉTRICAS ACTUALES

| Métrica | Target | Actual | Estado |
|---------|--------|--------|--------|
| Seeds procesados | 4 | 4 | ✅ |
| Keywords en Keywords_Master | 100-150 | 150 | ✅ |
| Quick wins identificados | 15-20 | 36 | ✅ |
| Keywords Motor Futuro | 50-70 | 28 | ⚠️ |
| Competidores analizados | 4 | 0 | ⏳ |
| Informes completos | 6 | 3 | 🔄 |

---

**Versión:** 1.0
**Plan de referencia:** PLAN_INVESTIGACION_COMPETENCIA_SEO_v3.md
"""

with open(BASE_DIR / 'README.md', 'w', encoding='utf-8') as f:
    f.write(readme_content)

print("\n[OK] README.md generado")

# 6. Resumen final
print("\n" + "=" * 60)
print("REORGANIZACIÓN COMPLETADA")
print("=" * 60)
print("\nEstructura final:")
print("  semrush/")
print("    ├── keyword_magic_tool/ (4 CSVs)")
print("    ├── keyword_overview/ (vacío - pendiente)")
print("    └── gap_entre_competidores/ (vacío - pendiente)")
print("  reports/ (7 archivos esenciales)")
print("  scripts/ (scripts Python)")
print("  temp/ (archivos temporales)")
print("  competidores_data/ (vacío - pendiente)")
print("  README.md")
print("\n[OK] Carpeta organizada según Plan v3.0")
