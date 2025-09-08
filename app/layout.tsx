import './globals.css';
import type { ReactNode } from 'react';
import { Merriweather, Lato } from 'next/font/google';

const merriweather = Merriweather({
  subsets: ['latin'],
  weight: ['300', '400', '700', '900'],
});

const lato = Lato({
  subsets: ['latin'],
  weight: ['300', '400', '700', '900'],
});

export const metadata = {
  title: 'Carrillo Abogados',
  description: 'Asociación de abogados'
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="es">
      <body className={`${merriweather.className} ${lato.className}`}>{children}</body>
    </html>
  );
}
