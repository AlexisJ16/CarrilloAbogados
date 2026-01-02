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
  metadataBase: new URL('https://carrilloabgd.com'),
  title: {
    default: 'Carrillo Abogados | Asesoría Legal Empresarial en Cali',
    template: '%s | Carrillo Abogados',
  },
  description:
    'Bufete de abogados con más de 23 años de experiencia en Cali, Colombia. Especialistas en Derecho Administrativo, Corporativo, Telecomunicaciones, Marcas y Competencia. Consulta inicial gratuita.',
  keywords: [
    'abogados Cali',
    'bufete de abogados',
    'derecho empresarial',
    'contratación estatal Colombia',
    'registro de marcas',
    'propiedad industrial SIC',
    'abogados corporativos',
    'derecho administrativo',
    'telecomunicaciones',
    'competencia desleal',
    'asesoría legal empresas',
  ],
  authors: [{ name: 'Carrillo ABGD SAS' }],
  creator: 'Carrillo ABGD SAS',
  publisher: 'Carrillo ABGD SAS',
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  openGraph: {
    type: 'website',
    locale: 'es_CO',
    url: 'https://carrilloabgd.com',
    siteName: 'Carrillo Abogados',
    title: 'Carrillo Abogados | Asesoría Legal Empresarial en Cali',
    description:
      'Más de 23 años brindando soluciones legales efectivas a empresas y particulares. Especialistas en marcas, contratación estatal y derecho corporativo.',
    images: [
      {
        url: '/og-image.jpg',
        width: 1200,
        height: 630,
        alt: 'Carrillo Abogados - Asesoría Legal Empresarial',
      },
    ],
  },
  twitter: {
    card: 'summary_large_image',
    title: 'Carrillo Abogados | Asesoría Legal Empresarial',
    description: 'Bufete de abogados en Cali con más de 23 años de experiencia.',
    images: ['/og-image.jpg'],
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      'max-video-preview': -1,
      'max-image-preview': 'large',
      'max-snippet': -1,
    },
  },
  verification: {
    // Agregar después de configurar en Google Search Console
    // google: 'tu-codigo-verificacion',
  },
  alternates: {
    canonical: 'https://carrilloabgd.com',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es" className={`${inter.variable} ${montserrat.variable}`}>
      <body className="flex min-h-screen flex-col">
        <Providers>
          <Header />
          <main className="flex-1">{children}</main>
          <Footer />
        </Providers>
      </body>
    </html>
  );
}
