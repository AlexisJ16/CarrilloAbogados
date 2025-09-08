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

El proyecto puede desplegarse en Vercel u otra plataforma compatible con Node.js.

### Despliegue en cPanel

1. En cPanel crea una aplicación de Node.js (versión 18 o superior).
2. Sube el contenido del repositorio o utiliza la integración de Git.
3. Dentro del directorio de la aplicación ejecuta:
   ```sh
   npm install
   npm run build
   ```
4. Configura el comando de inicio como `npm start` (que ejecuta `node server.js`).
5. Define las variables de entorno `NEXT_PUBLIC_SUPABASE_URL` y `NEXT_PUBLIC_SUPABASE_ANON_KEY` en la interfaz de cPanel.
6. Reinicia la aplicación para aplicar los cambios.

### Nota sobre Vercel

Si Vercel está conectado al repositorio intentará desplegar cada push. Revisa los logs de construcción para verificar dependencias y variables de entorno. Puedes desactivar el despliegue automático desde el panel de Vercel si prefieres manejarlo manualmente.

### Desarrollo en Codespaces

GitHub Codespaces permite ejecutar `npm run dev` o cualquier script de `package.json` directamente en el navegador, lo cual es suficiente para probar la aplicación sin un entorno local. También puedes usar editores locales si lo prefieres; ambos son válidos.

