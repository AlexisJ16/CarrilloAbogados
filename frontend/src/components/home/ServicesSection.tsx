import {
    ArrowRight,
    Award,
    Briefcase,
    Building2,
    Scale,
    Wifi
} from 'lucide-react';
import Link from 'next/link';

const services = [
  {
    name: 'Derecho Administrativo',
    description: 'Acompañamiento en contratación estatal, procedimientos administrativos y recursos ante entidades públicas.',
    icon: Building2,
    href: '/servicios/derecho-administrativo',
    color: 'bg-blue-50 text-blue-600',
  },
  {
    name: 'Derecho de Competencias',
    description: 'Asesoría en libre competencia, compliance antimonopolio y defensa ante la SIC.',
    icon: Scale,
    href: '/servicios/derecho-competencias',
    color: 'bg-purple-50 text-purple-600',
  },
  {
    name: 'Derecho Corporativo',
    description: 'Constitución de sociedades, gobierno corporativo, fusiones, adquisiciones y contratos comerciales.',
    icon: Briefcase,
    href: '/servicios/derecho-corporativo',
    color: 'bg-green-50 text-green-600',
  },
  {
    name: 'Derecho de Telecomunicaciones',
    description: 'Trámites ante MinTIC, CRC y ANE. Habilitaciones, concesiones y cumplimiento normativo.',
    icon: Wifi,
    href: '/servicios/derecho-telecomunicaciones',
    color: 'bg-orange-50 text-orange-600',
  },
  {
    name: 'Derecho de Marcas',
    description: 'Registro y protección de marcas ante la SIC, Sistema de Madrid y defensa de propiedad industrial.',
    icon: Award,
    href: '/servicios/derecho-marcas',
    color: 'bg-red-50 text-red-600',
  },
];

export function ServicesSection() {
  return (
    <section className="py-20 lg:py-28 bg-gray-50">
      <div className="container-section">
        {/* Header */}
        <div className="text-center max-w-3xl mx-auto mb-16">
          <span className="text-accent-500 font-semibold text-sm uppercase tracking-wider">
            Nuestros Servicios
          </span>
          <h2 className="text-3xl sm:text-4xl font-heading font-bold text-primary-500 mt-2 mb-4">
            Áreas de Práctica Legal
          </h2>
          <p className="text-gray-600 text-lg">
            Ofrecemos asesoría jurídica especializada en cinco áreas clave para proteger 
            los intereses de su empresa y garantizar su seguridad legal.
          </p>
        </div>
        
        {/* Services Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {services.map((service, index) => (
            <Link
              key={service.name}
              href={service.href}
              className={`card group hover:shadow-xl transition-all duration-300 hover:-translate-y-1 ${
                index === 4 ? 'md:col-span-2 lg:col-span-1' : ''
              }`}
            >
              <div className={`inline-flex p-3 rounded-lg ${service.color} mb-4`}>
                <service.icon className="h-6 w-6" />
              </div>
              <h3 className="text-xl font-heading font-bold text-primary-500 mb-2 group-hover:text-secondary-500 transition-colors">
                {service.name}
              </h3>
              <p className="text-gray-600 mb-4">
                {service.description}
              </p>
              <span className="inline-flex items-center text-secondary-500 font-medium text-sm group-hover:gap-2 transition-all">
                Conocer más
                <ArrowRight className="h-4 w-4 ml-1 group-hover:translate-x-1 transition-transform" />
              </span>
            </Link>
          ))}
        </div>
        
        {/* CTA */}
        <div className="text-center mt-12">
          <Link href="/servicios" className="btn-primary inline-flex items-center gap-2">
            Ver todos los servicios
            <ArrowRight className="h-5 w-5" />
          </Link>
        </div>
      </div>
    </section>
  );
}
