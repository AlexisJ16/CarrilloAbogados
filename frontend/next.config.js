/** @type {import('next').NextConfig} */

// Detectar si estamos en modo producción/export
const isExport = process.env.BUILD_MODE === 'export';

const nextConfig = {
  // Output mode:
  // - 'export' para HostGator (static HTML/CSS/JS)
  // - 'standalone' para Docker/Cloud Run
  output: isExport ? 'export' : 'standalone',

  // Trailing slashes para mejor compatibilidad con servidores estáticos
  trailingSlash: isExport,

  // Reescribir llamadas API al backend (solo en modo server)
  ...(isExport
    ? {}
    : {
        async rewrites() {
          return [
            {
              source: '/api/:path*',
              destination: `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/:path*`,
            },
          ];
        },
      }),

  // Headers de seguridad (solo en modo server, en export usar .htaccess)
  ...(isExport
    ? {}
    : {
        async headers() {
          return [
            {
              source: '/:path*',
              headers: [
                { key: 'X-Frame-Options', value: 'DENY' },
                { key: 'X-Content-Type-Options', value: 'nosniff' },
                { key: 'Referrer-Policy', value: 'strict-origin-when-cross-origin' },
                { key: 'X-XSS-Protection', value: '1; mode=block' },
              ],
            },
          ];
        },
      }),

  // Configuración de imágenes
  images: {
    // En modo export, las imágenes no se optimizan server-side
    unoptimized: isExport,
    remotePatterns: [
      {
        protocol: 'http',
        hostname: 'localhost',
        port: '8080',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: '**.carrilloabgd.com',
        pathname: '/**',
      },
      {
        protocol: 'https',
        hostname: 'api.carrilloabgd.com',
        pathname: '/**',
      },
    ],
  },

  // Variables de entorno públicas
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
    NEXT_PUBLIC_SITE_URL: process.env.NEXT_PUBLIC_SITE_URL || 'http://localhost:3000',
  },

  // Compilador optimizations
  compiler: {
    // Remover console.log en producción
    removeConsole: process.env.NODE_ENV === 'production' ? { exclude: ['error', 'warn'] } : false,
  },
};

module.exports = nextConfig;
