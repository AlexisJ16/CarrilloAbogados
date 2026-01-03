'use client';

import { Award, Briefcase, GraduationCap, Linkedin, Mail } from 'lucide-react';
import Link from 'next/link';

const lawyers = [
  {
    name: 'Dr. Omar Alberto Carrillo Martínez',
    role: 'Socio Director',
    specialty: 'Derecho de Competencias y Propiedad Industrial',
    experience: '23+ años de experiencia',
    highlight: '15 años en la Superintendencia de Industria y Comercio (SIC)',
    education: 'Abogado - Universidad del Cauca',
    image: '/team/omar-carrillo.jpg',
    bio: 'Fundador del bufete con amplia experiencia en derecho de competencias, propiedad industrial y derecho administrativo. Su trayectoria en la SIC le otorga un conocimiento único del sistema regulatorio colombiano.',
  },
  {
    name: 'Abogado Especialista 1',
    role: 'Abogado Senior',
    specialty: 'Derecho Corporativo',
    experience: '15+ años de experiencia',
    highlight: 'Especialista en fusiones y adquisiciones',
    education: 'Especialización en Derecho Comercial',
    image: null,
    bio: 'Experto en estructuración de negocios, gobierno corporativo y contratos comerciales complejos.',
  },
  {
    name: 'Abogado Especialista 2',
    role: 'Abogado Senior',
    specialty: 'Derecho Administrativo',
    experience: '12+ años de experiencia',
    highlight: 'Experto en contratación estatal',
    education: 'Maestría en Derecho Administrativo',
    image: null,
    bio: 'Especializado en licitaciones públicas, procesos administrativos y defensa ante la jurisdicción contencioso-administrativa.',
  },
  {
    name: 'Abogado Especialista 3',
    role: 'Abogado Asociado',
    specialty: 'Derecho de Telecomunicaciones',
    experience: '10+ años de experiencia',
    highlight: 'Asesor de operadores TIC',
    education: 'Especialización en Derecho de las Telecomunicaciones',
    image: null,
    bio: 'Experto en regulación de telecomunicaciones, habilitaciones ante MinTIC y transformación digital empresarial.',
  },
  {
    name: 'Abogado Especialista 4',
    role: 'Abogado Asociado',
    specialty: 'Propiedad Industrial',
    experience: '8+ años de experiencia',
    highlight: 'Especialista en registro de marcas',
    education: 'Especialización en Propiedad Intelectual',
    image: null,
    bio: 'Dedicado a la protección de marcas, patentes y secretos empresariales tanto a nivel nacional como internacional.',
  },
  {
    name: 'Abogado Especialista 5',
    role: 'Abogado Asociado',
    specialty: 'Derecho de Competencias',
    experience: '7+ años de experiencia',
    highlight: 'Defensa ante SIC',
    education: 'Maestría en Derecho Económico',
    image: null,
    bio: 'Especializado en competencia desleal, prácticas restrictivas y compliance en libre competencia.',
  },
  {
    name: 'Abogado Especialista 6',
    role: 'Abogado Junior',
    specialty: 'Derecho Corporativo',
    experience: '5+ años de experiencia',
    highlight: 'Constitución de sociedades',
    education: 'Abogado con estudios en curso',
    image: null,
    bio: 'Apoyo en constitución de empresas, contratos comerciales y due diligence corporativo.',
  },
];

const team = [
  {
    name: 'Asistente Administrativa',
    role: 'Coordinación Administrativa',
    description: 'Gestión de agenda, atención a clientes y coordinación interna del bufete.',
  },
  {
    name: 'Asistente Personal',
    role: 'Apoyo Directivo',
    description: 'Soporte directo a la dirección en gestión documental y comunicaciones.',
  },
];

export default function EquipoPage() {
  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-primary-600 py-24">
        <div className="container-section">
          <div className="text-center">
            <h1 className="font-heading text-4xl font-bold text-white sm:text-5xl">
              Nuestro Equipo
            </h1>
            <p className="mx-auto mt-6 max-w-3xl text-lg text-primary-100">
              Un equipo multidisciplinario de 7 abogados especializados, respaldados por personal
              administrativo comprometido con la excelencia en el servicio.
            </p>
          </div>
        </div>
      </div>

      {/* Socio Director */}
      <section className="py-16 lg:py-24">
        <div className="container-section">
          <div className="grid grid-cols-1 items-center gap-12 lg:grid-cols-2">
            <div className="flex aspect-square items-center justify-center rounded-2xl bg-gray-100">
              <div className="p-8 text-center">
                <div className="mx-auto flex h-32 w-32 items-center justify-center rounded-full bg-primary-200">
                  <span className="text-4xl font-bold text-primary-600">OC</span>
                </div>
                <p className="mt-4 text-sm text-gray-500">Foto profesional próximamente</p>
              </div>
            </div>
            <div>
              <div className="mb-4 inline-flex items-center gap-2 rounded-full bg-primary-100 px-4 py-2 text-sm font-medium text-primary-700">
                <Award className="h-4 w-4" />
                Socio Director
              </div>
              <h2 className="font-heading text-3xl font-bold text-gray-900">{lawyers[0].name}</h2>
              <p className="mt-2 font-medium text-primary-600">{lawyers[0].specialty}</p>
              <p className="mt-6 leading-relaxed text-gray-600">{lawyers[0].bio}</p>

              <div className="mt-8 space-y-4">
                <div className="flex items-center gap-3">
                  <Briefcase className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-600">{lawyers[0].experience}</span>
                </div>
                <div className="flex items-center gap-3">
                  <Award className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-600">{lawyers[0].highlight}</span>
                </div>
                <div className="flex items-center gap-3">
                  <GraduationCap className="h-5 w-5 text-gray-400" />
                  <span className="text-gray-600">{lawyers[0].education}</span>
                </div>
              </div>

              <div className="mt-8 flex gap-4">
                <a
                  href="mailto:gerenciacarrilloabgd@gmail.com"
                  className="inline-flex items-center gap-2 rounded-lg bg-primary-600 px-4 py-2 text-white transition-colors hover:bg-primary-700"
                >
                  <Mail className="h-4 w-4" />
                  Contactar
                </a>
                <a
                  href="https://linkedin.com"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center gap-2 rounded-lg border border-gray-300 px-4 py-2 text-gray-700 transition-colors hover:bg-gray-50"
                >
                  <Linkedin className="h-4 w-4" />
                  LinkedIn
                </a>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Otros Abogados */}
      <section className="bg-gray-50 py-16">
        <div className="container-section">
          <div className="mb-12 text-center">
            <h2 className="font-heading text-3xl font-bold text-gray-900">Equipo de Abogados</h2>
            <p className="mx-auto mt-4 max-w-2xl text-gray-600">
              Profesionales especializados en diferentes áreas del derecho
            </p>
          </div>

          <div className="grid grid-cols-1 gap-8 md:grid-cols-2 lg:grid-cols-3">
            {lawyers.slice(1).map((lawyer) => (
              <div key={lawyer.name} className="rounded-2xl bg-white p-6 shadow-sm">
                <div className="mx-auto mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-gray-100">
                  <span className="text-2xl font-bold text-gray-400">
                    {lawyer.name
                      .split(' ')
                      .map((n) => n[0])
                      .join('')
                      .slice(0, 2)}
                  </span>
                </div>
                <div className="text-center">
                  <h3 className="font-heading text-lg font-semibold text-gray-900">
                    {lawyer.name}
                  </h3>
                  <p className="text-sm font-medium text-primary-600">{lawyer.role}</p>
                  <p className="mt-1 text-sm text-gray-500">{lawyer.specialty}</p>
                </div>
                <div className="mt-4 border-t border-gray-100 pt-4">
                  <p className="text-center text-sm text-gray-600">{lawyer.bio}</p>
                </div>
                <div className="mt-4 flex items-center justify-center gap-2 text-sm text-gray-500">
                  <Briefcase className="h-4 w-4" />
                  {lawyer.experience}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Equipo Administrativo */}
      <section className="py-16">
        <div className="container-section">
          <div className="mb-12 text-center">
            <h2 className="font-heading text-3xl font-bold text-gray-900">Equipo Administrativo</h2>
            <p className="mt-4 text-gray-600">Profesionales que respaldan la gestión del bufete</p>
          </div>

          <div className="mx-auto grid max-w-2xl grid-cols-1 gap-8 md:grid-cols-2">
            {team.map((member) => (
              <div key={member.name} className="rounded-xl bg-gray-50 p-6 text-center">
                <div className="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-primary-100">
                  <Briefcase className="h-8 w-8 text-primary-600" />
                </div>
                <h3 className="font-semibold text-gray-900">{member.name}</h3>
                <p className="text-sm text-primary-600">{member.role}</p>
                <p className="mt-2 text-sm text-gray-600">{member.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="bg-primary-600 py-16">
        <div className="container-section text-center">
          <h2 className="font-heading text-3xl font-bold text-white">
            ¿Listo para trabajar con nosotros?
          </h2>
          <p className="mt-4 text-primary-100">
            Contáctenos para agendar una consulta con nuestro equipo de especialistas.
          </p>
          <Link
            href="/contacto"
            className="mt-8 inline-flex items-center rounded-lg bg-white px-6 py-3 font-semibold text-primary-600 transition-colors hover:bg-gray-100"
          >
            Agendar Consulta
          </Link>
        </div>
      </section>
    </div>
  );
}
