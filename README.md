# Carrillo Abogados Web

<<<<<<< HEAD
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

=======
This repository contains the source code for the Carrillo Abogados website. The project is split into three parts to keep responsibilities separated and make development easier to manage. The main web interface is built with **Next.js**, allowing us to serve both pages and API routes from a single application.

## Structure

- **frontend/** – Next.js application handling UI and API routes.
- **backend/** – Express server providing API endpoints.
- **database/** – Supabase configuration and database migrations.

## Development

### Next.js app

Install dependencies and start the development server:

```sh
npm --prefix frontend install
npm run frontend:dev
```

Other useful commands:

- `npm run frontend:build` – build the production bundle.
- `npm run frontend:lint` – run ESLint on the frontend code.

Copy `frontend/.env.example` to `frontend/.env` and fill in the required values before running the Next.js app locally.

### Backend

The backend is a minimal Express server. Install dependencies and start it with:

```sh
npm --prefix backend install
npm --prefix backend start
```

### Database

The `database/` folder contains Supabase configuration. Install the [Supabase CLI](https://supabase.com/docs/guides/cli) to manage migrations and run local instances.

## Deployment

- **Next.js app** – Deploy on platforms like Vercel or Netlify. Pushing to `main` triggers an automatic build and production deployment.
- **Backend** – Deploy to a Node-friendly platform (e.g., Vercel functions, Render, Railway, or a traditional server).
- **Database** – Supabase hosts the PostgreSQL database and auth services.

## Contributing

1. Fork the repository and create your feature branch.
2. Make your changes and ensure `npm run frontend:lint` passes.
3. Commit your changes.
4. Submit a pull request.
>>>>>>> main
