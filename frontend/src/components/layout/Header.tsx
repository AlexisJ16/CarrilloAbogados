'use client';

import { Menu, Phone, X } from 'lucide-react';
import Link from 'next/link';
import { useState } from 'react';

const navigation = [
  { name: 'Inicio', href: '/' },
  { name: 'Quiénes Somos', href: '/nosotros' },
  { name: 'Áreas de Práctica', href: '/servicios' },
  { name: 'Equipo', href: '/equipo' },
  { name: 'Blog', href: '/blog' },
  { name: 'Contacto', href: '/contacto' },
];

export function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <header className="bg-white shadow-sm sticky top-0 z-50">
      <nav className="container-section" aria-label="Global">
        <div className="flex items-center justify-between py-4">
          {/* Logo */}
          <div className="flex lg:flex-1">
            <Link href="/" className="-m-1.5 p-1.5">
              <span className="font-heading text-2xl font-bold text-primary-500">
                Carrillo<span className="text-accent-500">Abogados</span>
              </span>
            </Link>
          </div>
          
          {/* Mobile menu button */}
          <div className="flex lg:hidden">
            <button
              type="button"
              className="-m-2.5 inline-flex items-center justify-center rounded-md p-2.5 text-gray-700"
              onClick={() => setMobileMenuOpen(true)}
            >
              <span className="sr-only">Abrir menú</span>
              <Menu className="h-6 w-6" aria-hidden="true" />
            </button>
          </div>
          
          {/* Desktop navigation */}
          <div className="hidden lg:flex lg:gap-x-8">
            {navigation.map((item) => (
              <Link
                key={item.name}
                href={item.href}
                className="text-sm font-medium text-gray-700 hover:text-primary-500 transition-colors"
              >
                {item.name}
              </Link>
            ))}
          </div>
          
          {/* CTA Button */}
          <div className="hidden lg:flex lg:flex-1 lg:justify-end lg:gap-x-4">
            <a
              href="tel:+573001234567"
              className="flex items-center gap-2 text-sm text-gray-600 hover:text-primary-500"
            >
              <Phone className="h-4 w-4" />
              <span>+57 300 123 4567</span>
            </a>
            <Link href="/contacto" className="btn-primary text-sm">
              Agendar Consulta
            </Link>
          </div>
        </div>
      </nav>
      
      {/* Mobile menu */}
      {mobileMenuOpen && (
        <div className="lg:hidden">
          <div className="fixed inset-0 z-50" />
          <div className="fixed inset-y-0 right-0 z-50 w-full overflow-y-auto bg-white px-6 py-6 sm:max-w-sm sm:ring-1 sm:ring-gray-900/10">
            <div className="flex items-center justify-between">
              <Link href="/" className="-m-1.5 p-1.5">
                <span className="font-heading text-2xl font-bold text-primary-500">
                  Carrillo<span className="text-accent-500">Abogados</span>
                </span>
              </Link>
              <button
                type="button"
                className="-m-2.5 rounded-md p-2.5 text-gray-700"
                onClick={() => setMobileMenuOpen(false)}
              >
                <span className="sr-only">Cerrar menú</span>
                <X className="h-6 w-6" aria-hidden="true" />
              </button>
            </div>
            <div className="mt-6 flow-root">
              <div className="-my-6 divide-y divide-gray-500/10">
                <div className="space-y-2 py-6">
                  {navigation.map((item) => (
                    <Link
                      key={item.name}
                      href={item.href}
                      className="-mx-3 block rounded-lg px-3 py-2 text-base font-medium text-gray-700 hover:bg-gray-50"
                      onClick={() => setMobileMenuOpen(false)}
                    >
                      {item.name}
                    </Link>
                  ))}
                </div>
                <div className="py-6">
                  <Link
                    href="/contacto"
                    className="btn-primary block text-center"
                    onClick={() => setMobileMenuOpen(false)}
                  >
                    Agendar Consulta
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
