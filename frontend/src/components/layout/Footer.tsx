import { Facebook, Linkedin, Mail, MapPin, Phone } from 'lucide-react';
import Link from 'next/link';

const footerNavigation = {
  servicios: [
    { name: 'Derecho Administrativo', href: '/servicios/derecho-administrativo' },
    { name: 'Derecho de Competencias', href: '/servicios/derecho-competencias' },
    { name: 'Derecho Corporativo', href: '/servicios/derecho-corporativo' },
    { name: 'Derecho de Telecomunicaciones', href: '/servicios/derecho-telecomunicaciones' },
    { name: 'Derecho de Marcas', href: '/servicios/derecho-marcas' },
  ],
  empresa: [
    { name: 'Quiénes Somos', href: '/nosotros' },
    { name: 'Nuestro Equipo', href: '/equipo' },
    { name: 'Blog', href: '/blog' },
    { name: 'Contacto', href: '/contacto' },
  ],
  legal: [
    { name: 'Política de Privacidad', href: '/privacidad' },
    { name: 'Términos y Condiciones', href: '/terminos' },
    { name: 'Política de Cookies', href: '/cookies' },
  ],
};

export function Footer() {
  return (
    <footer className="bg-primary-500 text-white" aria-labelledby="footer-heading">
      <h2 id="footer-heading" className="sr-only">
        Footer
      </h2>
      <div className="container-section py-12 lg:py-16">
        <div className="xl:grid xl:grid-cols-3 xl:gap-8">
          {/* Brand & Contact */}
          <div className="space-y-6">
            <span className="font-heading text-2xl font-bold">
              Carrillo<span className="text-accent-400">Abogados</span>
            </span>
            <p className="text-sm text-gray-300 max-w-xs">
              Más de 23 años brindando soluciones legales efectivas a empresas y particulares en Colombia.
            </p>
            <div className="space-y-3">
              <a
                href="tel:+573001234567"
                className="flex items-center gap-2 text-sm text-gray-300 hover:text-white transition-colors"
              >
                <Phone className="h-4 w-4" />
                +57 300 123 4567
              </a>
              <a
                href="mailto:contacto@carrilloabgd.com"
                className="flex items-center gap-2 text-sm text-gray-300 hover:text-white transition-colors"
              >
                <Mail className="h-4 w-4" />
                contacto@carrilloabgd.com
              </a>
              <div className="flex items-start gap-2 text-sm text-gray-300">
                <MapPin className="h-4 w-4 mt-0.5 flex-shrink-0" />
                <span>
                  Cra. 40 #27-26, El Piloto<br />
                  Torre de Cali, Piso 21, Of. 2102A<br />
                  Cali, Colombia
                </span>
              </div>
            </div>
            {/* Social */}
            <div className="flex gap-4">
              <a
                href="https://linkedin.com/company/carrilloabgd"
                target="_blank"
                rel="noopener noreferrer"
                className="text-gray-300 hover:text-white transition-colors"
              >
                <span className="sr-only">LinkedIn</span>
                <Linkedin className="h-6 w-6" />
              </a>
              <a
                href="https://facebook.com/carrilloabgd"
                target="_blank"
                rel="noopener noreferrer"
                className="text-gray-300 hover:text-white transition-colors"
              >
                <span className="sr-only">Facebook</span>
                <Facebook className="h-6 w-6" />
              </a>
            </div>
          </div>
          
          {/* Navigation */}
          <div className="mt-16 grid grid-cols-2 gap-8 xl:col-span-2 xl:mt-0">
            <div className="md:grid md:grid-cols-2 md:gap-8">
              <div>
                <h3 className="text-sm font-semibold">Áreas de Práctica</h3>
                <ul role="list" className="mt-4 space-y-3">
                  {footerNavigation.servicios.map((item) => (
                    <li key={item.name}>
                      <Link
                        href={item.href}
                        className="text-sm text-gray-300 hover:text-white transition-colors"
                      >
                        {item.name}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
              <div className="mt-10 md:mt-0">
                <h3 className="text-sm font-semibold">Empresa</h3>
                <ul role="list" className="mt-4 space-y-3">
                  {footerNavigation.empresa.map((item) => (
                    <li key={item.name}>
                      <Link
                        href={item.href}
                        className="text-sm text-gray-300 hover:text-white transition-colors"
                      >
                        {item.name}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
            <div className="md:grid md:grid-cols-2 md:gap-8">
              <div>
                <h3 className="text-sm font-semibold">Legal</h3>
                <ul role="list" className="mt-4 space-y-3">
                  {footerNavigation.legal.map((item) => (
                    <li key={item.name}>
                      <Link
                        href={item.href}
                        className="text-sm text-gray-300 hover:text-white transition-colors"
                      >
                        {item.name}
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        </div>
        
        {/* Bottom */}
        <div className="mt-12 border-t border-white/10 pt-8">
          <p className="text-xs text-gray-400 text-center">
            &copy; {new Date().getFullYear()} Carrillo ABGD SAS. Todos los derechos reservados.
          </p>
        </div>
      </div>
    </footer>
  );
}
