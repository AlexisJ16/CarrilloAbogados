# Carrillo Abogados Web

Este repositorio contiene el código fuente del sitio web de la Asociación Carrillo Abogados, construido con [Next.js](https://nextjs.org/).

## Estructura

- `app/` – Rutas de la aplicación y API utilizando el directorio `app` de Next.js.
- `lib/` – Utilidades compartidas como el cliente de Supabase.
- `database/` – Configuración y migraciones de Supabase.

## Configuración

1. Copia `.env.example` a `.env` y completa las variables de entorno de Supabase.
2. Instala dependencias (requiere acceso a internet):
   ```sh
   npm install
   ```

## Scripts

- `npm run dev` – Ejecuta el servidor de desarrollo.
- `npm run build` – Crea el build de producción.
- `npm start` – Inicia el servidor en modo producción.
- `npm run lint` – Ejecuta ESLint.

## Blog y SEO

El directorio `app/blog` contiene una base para artículos. Cada entrada puede renderizarse de forma estática para mejorar el SEO. Se recomienda añadir metadatos por página y un sitemap automático.

## Despliegue

El proyecto puede desplegarse en Vercel u otra plataforma compatible con Node.js. Para entornos con cPanel, genera un build estático (`npm run build`) y sirve los archivos de `.next` a través de Node o un adaptador como `next start`.

