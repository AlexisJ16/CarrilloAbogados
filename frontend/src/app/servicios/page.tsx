'use client';

import { ArrowRight, Briefcase, Building2, FileText, Gavel, Network, Shield } from 'lucide-react';
import Link from 'next/link';

const services = [
  {
    id: 'derecho-administrativo',
    icon: Gavel,
    title: 'Derecho Administrativo',
    subtitle: 'Con énfasis en Contratación Estatal',
    description:
      'Conjunto de normas que definen cómo actúan las entidades públicas y cómo los ciudadanos y empresas pueden interactuar con ellas o defenderse de sus decisiones.',
    services: [
      'Acompañamiento en procedimientos administrativos y sancionatorios',
      'Asesoría en contratación estatal (licitaciones, selección abreviada)',
      'Elaboración y revisión de pliegos, propuestas y contratos con el Estado',
      'Presentación de recursos administrativos (reposición, apelación, queja)',
      'Acciones ante jurisdicción contencioso administrativa',
    ],
    regulation: 'Marco Regulatorio: Ley 80 de 1993, Ley 1150 de 2007, Decreto 1082 de 2015',
  },
  {
    id: 'derecho-competencias',
    icon: Shield,
    title: 'Derecho de Competencias',
    subtitle: 'Libre Competencia Económica',
    description:
      'Rama del derecho que regula, protege y promueve la competencia leal en los mercados, evitando prácticas que limiten, distorsionen o eliminen la competencia.',
    services: [
      'Asesoría preventiva en cumplimiento del régimen de libre competencia',
      'Representación en investigaciones ante la SIC',
      'Revisión de integraciones empresariales y fusiones',
      'Defensa en procesos sancionatorios',
      'Demandas y defensas por actos de competencia desleal',
    ],
    regulation: 'Dr. Omar Carrillo: 15 años de experiencia en la SIC',
  },
  {
    id: 'derecho-corporativo',
    icon: Building2,
    title: 'Derecho Corporativo',
    subtitle: 'Gestión Empresarial Integral',
    description:
      'Regula la creación, organización, funcionamiento, transformación, fusión, escisión, disolución y liquidación de empresas.',
    services: [
      'Constitución y formalización de sociedades (S.A.S., Ltda., S.A.)',
      'Reformas estatutarias, fusiones, escisiones y transformaciones',
      'Gobierno corporativo y cumplimiento normativo (compliance)',
      'Elaboración y revisión de contratos comerciales',
      'Asesoría en procesos de insolvencia y reorganización',
    ],
    regulation: 'Due Diligence en adquisiciones e inversiones',
  },
  {
    id: 'derecho-telecomunicaciones',
    icon: Network,
    title: 'Derecho de Telecomunicaciones',
    subtitle: 'Sector TIC y Tecnología',
    description:
      'Rama del Derecho Público que regula la organización, prestación y control de los servicios de telecomunicaciones.',
    services: [
      'Trámites ante MinTIC, CRC y ANE (habilitaciones, concesiones)',
      'Cumplimiento normativo y regulatorio para operadores',
      'Contratos entre operadores y proveedores de servicios',
      'Representación en investigaciones administrativas',
      'Asesoría en transformación digital, IoT, OTT',
    ],
    regulation: 'Ámbito: Telefonía, internet, radio, televisión, satélite y nuevas tecnologías',
  },
  {
    id: 'derecho-marcas',
    icon: FileText,
    title: 'Derecho de Marcas',
    subtitle: 'Propiedad Industrial',
    description:
      'Regula la protección, registro, uso y defensa de las marcas y signos distintivos que identifican productos o servicios en el mercado.',
    services: [
      'Búsquedas previas de antecedentes marcarios',
      'Solicitud y obtención de registro de marcas ante la SIC',
      'Renovaciones, modificaciones y oposiciones',
      'Protección internacional mediante Sistema de Madrid',
      'Acciones de nulidad, cancelación o infracción',
    ],
    regulation: 'Autoridad Competente: Superintendencia de Industria y Comercio (SIC)',
  },
];

export default function ServiciosPage() {
  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-primary-600 py-24">
        <div className="container-section">
          <div className="text-center">
            <h1 className="font-heading text-4xl font-bold text-white sm:text-5xl">
              Áreas de Práctica
            </h1>
            <p className="mx-auto mt-6 max-w-3xl text-lg text-primary-100">
              Ofrecemos servicios jurídicos especializados en 5 áreas principales, todas orientadas
              al sector empresarial con el más alto nivel de profesionalismo.
            </p>
          </div>
        </div>
      </div>

      {/* Services List */}
      <section className="py-16 lg:py-24">
        <div className="container-section">
          <div className="space-y-16">
            {services.map((service, index) => (
              <div
                key={service.id}
                id={service.id}
                className={`grid grid-cols-1 items-start gap-12 lg:grid-cols-2 ${
                  index % 2 === 1 ? 'lg:flex-row-reverse' : ''
                }`}
              >
                <div className={index % 2 === 1 ? 'lg:order-2' : ''}>
                  <div className="mb-4 flex items-center gap-4">
                    <div className="flex h-14 w-14 items-center justify-center rounded-xl bg-primary-100">
                      <service.icon className="h-7 w-7 text-primary-600" />
                    </div>
                    <div>
                      <h2 className="font-heading text-2xl font-bold text-gray-900">
                        {service.title}
                      </h2>
                      <p className="font-medium text-primary-600">{service.subtitle}</p>
                    </div>
                  </div>
                  <p className="mb-6 leading-relaxed text-gray-600">{service.description}</p>
                  <p className="mb-4 text-sm font-medium text-primary-600">{service.regulation}</p>
                </div>
                <div
                  className={`rounded-2xl bg-gray-50 p-6 ${index % 2 === 1 ? 'lg:order-1' : ''}`}
                >
                  <h3 className="mb-4 font-semibold text-gray-900">Servicios que ofrecemos:</h3>
                  <ul className="space-y-3">
                    {service.services.map((item, i) => (
                      <li key={i} className="flex items-start gap-3">
                        <ArrowRight className="mt-0.5 h-5 w-5 flex-shrink-0 text-primary-600" />
                        <span className="text-sm text-gray-600">{item}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-gray-900 py-16">
        <div className="container-section text-center">
          <Briefcase className="mx-auto mb-6 h-12 w-12 text-primary-400" />
          <h2 className="font-heading text-3xl font-bold text-white">¿Necesita asesoría legal?</h2>
          <p className="mx-auto mt-4 max-w-2xl text-gray-400">
            Contáctenos para una consulta inicial gratuita. Nuestro equipo de abogados
            especializados está listo para ayudarle.
          </p>
          <div className="mt-8 flex flex-col justify-center gap-4 sm:flex-row">
            <Link
              href="/contacto"
              className="inline-flex items-center justify-center rounded-lg bg-primary-600 px-6 py-3 font-semibold text-white transition-colors hover:bg-primary-700"
            >
              Solicitar Consulta
              <ArrowRight className="ml-2 h-5 w-5" />
            </Link>
            <Link
              href="/equipo"
              className="inline-flex items-center justify-center rounded-lg border border-gray-600 px-6 py-3 font-semibold text-gray-300 transition-colors hover:bg-gray-800"
            >
              Conocer al Equipo
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
