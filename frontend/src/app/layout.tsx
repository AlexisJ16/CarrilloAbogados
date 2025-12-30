import { Footer } from '@/components/layout/Footer';
import { Header } from '@/components/layout/Header';
import type { Metadata } from 'next';
import { Inter, Montserrat } from 'next/font/google';
import './globals.css';
import { Providers } from './providers';

const inter = Inter({ 
  subsets: ['latin'],
  variable: '--font-inter',
});

const montserrat = Montserrat({ 
  subsets: ['latin'],
  variable: '--font-montserrat',
});

export const metadata: Metadata = {
  title: 'Carrillo Abogados | Asesoría Legal Empresarial',
  description: 'Bufete de abogados con más de 23 años de experiencia. Especialistas en Derecho Administrativo, Corporativo, Telecomunicaciones, Marcas y Competencia.',
  keywords: [
    'abogados',
    'bufete',
    'derecho empresarial',
    'contratación estatal',
    'marcas',
    'propiedad industrial',
    'Cali',
    'Colombia',
  ],
  authors: [{ name: 'Carrillo ABGD SAS' }],
  openGraph: {
    type: 'website',
    locale: 'es_CO',
    url: 'https://carrilloabgd.com',
    siteName: 'Carrillo Abogados',
    title: 'Carrillo Abogados | Asesoría Legal Empresarial',
    description: 'Más de 23 años brindando soluciones legales efectivas a empresas y particulares.',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es" className={`${inter.variable} ${montserrat.variable}`}>
      <body className="min-h-screen flex flex-col">
        <Providers>
          <Header />
          <main className="flex-1">
            {children}
          </main>
          <Footer />
        </Providers>
      </body>
    </html>
  );
}
