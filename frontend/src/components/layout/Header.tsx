'use client';

import { useAuth } from '@/lib/auth';
import { LogIn, LogOut, Menu, Phone, User, X } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
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
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const { isAuthenticated, user, logout, isLoading } = useAuth();
  const router = useRouter();

  const handleLogout = async () => {
    await logout();
    setUserMenuOpen(false);
    router.push('/');
  };

  const getDashboardLink = () => {
    if (!user) return '/dashboard';
    switch (user.role) {
      case 'ROLE_ADMIN':
        return '/dashboard';
      case 'ROLE_LAWYER':
        return '/dashboard';
      case 'ROLE_CLIENT':
        return '/dashboard';
      default:
        return '/dashboard';
    }
  };

  return (
    <header className="sticky top-0 z-50 bg-white shadow-sm">
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
                className="text-sm font-medium text-gray-700 transition-colors hover:text-primary-500"
              >
                {item.name}
              </Link>
            ))}
          </div>

          {/* Right side - Auth buttons & CTA */}
          <div className="hidden lg:flex lg:flex-1 lg:items-center lg:justify-end lg:gap-x-4">
            <a
              href="tel:+573001234567"
              className="flex items-center gap-2 text-sm text-gray-600 hover:text-primary-500"
            >
              <Phone className="h-4 w-4" />
              <span>+57 300 123 4567</span>
            </a>

            {/* Auth Section */}
            {!isLoading && (
              <>
                {isAuthenticated && user ? (
                  // Logged in - show user menu
                  <div className="relative">
                    <button
                      type="button"
                      className="flex items-center gap-2 rounded-full bg-gray-100 px-3 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200"
                      onClick={() => setUserMenuOpen(!userMenuOpen)}
                    >
                      <div className="flex h-7 w-7 items-center justify-center rounded-full bg-primary-500 text-white">
                        {user.firstName?.[0]?.toUpperCase() || user.email[0].toUpperCase()}
                      </div>
                      <span className="max-w-[120px] truncate">
                        {user.firstName || user.email.split('@')[0]}
                      </span>
                    </button>

                    {/* Dropdown menu */}
                    {userMenuOpen && (
                      <div className="absolute right-0 z-50 mt-2 w-56 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                        <div className="border-b border-gray-100 px-4 py-2">
                          <p className="text-sm font-medium text-gray-900">{user.fullName}</p>
                          <p className="text-xs text-gray-500">{user.email}</p>
                          <span className="mt-1 inline-flex items-center rounded-full bg-primary-100 px-2 py-0.5 text-xs font-medium text-primary-800">
                            {user.role.replace('ROLE_', '')}
                          </span>
                        </div>
                        <Link
                          href={getDashboardLink()}
                          className="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
                          onClick={() => setUserMenuOpen(false)}
                        >
                          <User className="h-4 w-4" />
                          Mi Panel
                        </Link>
                        <button
                          type="button"
                          className="flex w-full items-center gap-2 px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                          onClick={handleLogout}
                        >
                          <LogOut className="h-4 w-4" />
                          Cerrar Sesión
                        </button>
                      </div>
                    )}
                  </div>
                ) : (
                  // Not logged in - show login button
                  <Link
                    href="/login"
                    className="flex items-center gap-2 rounded-md bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200"
                  >
                    <LogIn className="h-4 w-4" />
                    Iniciar Sesión
                  </Link>
                )}
              </>
            )}

            <Link href="/contacto" className="btn-primary text-sm">
              Agendar Consulta
            </Link>
          </div>
        </div>
      </nav>

      {/* Mobile menu */}
      {mobileMenuOpen && (
        <div className="lg:hidden">
          <div
            className="fixed inset-0 z-50 bg-black/20"
            onClick={() => setMobileMenuOpen(false)}
          />
          <div className="fixed inset-y-0 right-0 z-50 w-full overflow-y-auto bg-white px-6 py-6 sm:max-w-sm sm:ring-1 sm:ring-gray-900/10">
            <div className="flex items-center justify-between">
              <Link href="/" className="-m-1.5 p-1.5" onClick={() => setMobileMenuOpen(false)}>
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

                {/* Mobile auth section */}
                <div className="space-y-3 py-6">
                  {!isLoading && (
                    <>
                      {isAuthenticated && user ? (
                        <>
                          <div className="rounded-lg bg-gray-50 px-3 py-2">
                            <p className="text-sm font-medium text-gray-900">{user.fullName}</p>
                            <p className="text-xs text-gray-500">{user.email}</p>
                          </div>
                          <Link
                            href={getDashboardLink()}
                            className="-mx-3 flex items-center gap-2 rounded-lg px-3 py-2 text-base font-medium text-gray-700 hover:bg-gray-50"
                            onClick={() => setMobileMenuOpen(false)}
                          >
                            <User className="h-5 w-5" />
                            Mi Panel
                          </Link>
                          <button
                            type="button"
                            className="-mx-3 flex w-full items-center gap-2 rounded-lg px-3 py-2 text-base font-medium text-red-600 hover:bg-red-50"
                            onClick={() => {
                              handleLogout();
                              setMobileMenuOpen(false);
                            }}
                          >
                            <LogOut className="h-5 w-5" />
                            Cerrar Sesión
                          </button>
                        </>
                      ) : (
                        <Link
                          href="/login"
                          className="flex items-center justify-center gap-2 rounded-lg bg-gray-100 px-3 py-2.5 text-base font-medium text-gray-700 hover:bg-gray-200"
                          onClick={() => setMobileMenuOpen(false)}
                        >
                          <LogIn className="h-5 w-5" />
                          Iniciar Sesión
                        </Link>
                      )}
                    </>
                  )}
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

      {/* Click outside to close user menu */}
      {userMenuOpen && (
        <div className="fixed inset-0 z-40" onClick={() => setUserMenuOpen(false)} />
      )}
    </header>
  );
}
