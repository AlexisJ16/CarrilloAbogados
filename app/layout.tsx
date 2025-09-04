import './globals.css';
import type { ReactNode } from 'react';

export const metadata = {
  title: 'Carrillo Abogados',
  description: 'Asociación de abogados'
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="es">
      <body>{children}</body>
    </html>
  );
}
