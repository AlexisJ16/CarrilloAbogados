# Carrillo Abogados Web

This repository contains the source code for the Carrillo Abogados website. The project is split into three parts to keep responsibilities separated and make development easier to manage.

## Structure

- **frontend/** – React + Vite client application.
- **backend/** – Express server providing API endpoints.
- **database/** – Supabase configuration and database migrations.

## Development

### Frontend

Install dependencies and start the development server:

```sh
npm --prefix frontend install
npm run frontend:dev
```

Other useful commands:

- `npm run frontend:build` – build the production bundle.
- `npm run frontend:lint` – run ESLint on the frontend code.

Copy `frontend/.env.example` to `frontend/.env` and fill in the required values before running the frontend locally.

### Backend

The backend is a minimal Express server. Install dependencies and start it with:

```sh
npm --prefix backend install
npm --prefix backend start
```

### Database

The `database/` folder contains Supabase configuration. Install the [Supabase CLI](https://supabase.com/docs/guides/cli) to manage migrations and run local instances.

## Deployment

- **Frontend** – Deploy using services like Vercel or Netlify.
- **Backend** – Deploy to a Node-friendly platform (e.g., Vercel functions, Render, Railway, or a traditional server).
- **Database** – Supabase hosts the PostgreSQL database and auth services.

## Contributing

1. Fork the repository and create your feature branch.
2. Make your changes and ensure `npm run frontend:lint` passes.
3. Commit your changes.
4. Submit a pull request.
