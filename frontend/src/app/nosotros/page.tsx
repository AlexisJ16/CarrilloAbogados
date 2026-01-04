'use client';

import { Award, Building, Scale, Users } from 'lucide-react';

const stats = [
  { label: 'Años de Experiencia', value: '23+' },
  { label: 'Casos Exitosos', value: '500+' },
  { label: 'Clientes Satisfechos', value: '300+' },
  { label: 'Abogados Especializados', value: '7' },
];

const values = [
  {
    icon: Scale,
    title: 'Integridad',
    description: 'Actuamos con honestidad y transparencia en cada caso que manejamos.',
  },
  {
    icon: Users,
    title: 'Compromiso',
    description: 'Nos dedicamos completamente a defender los intereses de nuestros clientes.',
  },
  {
    icon: Award,
    title: 'Excelencia',
    description: 'Buscamos los más altos estándares de calidad en nuestros servicios legales.',
  },
  {
    icon: Building,
    title: 'Profesionalismo',
    description: 'Mantenemos una conducta ética y profesional en todas nuestras actuaciones.',
  },
];

export default function NosotrosPage() {
  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-primary-600 py-24">
        <div className="container-section">
          <div className="text-center">
            <h1 className="font-heading text-4xl font-bold text-white sm:text-5xl">
              Quiénes Somos
            </h1>
            <p className="mx-auto mt-6 max-w-3xl text-lg text-primary-100">
              Somos un bufete de abogados con más de 23 años de trayectoria, comprometidos con la
              excelencia jurídica y la satisfacción de nuestros clientes.
            </p>
          </div>
        </div>
      </div>

      {/* Historia */}
      <section className="py-16 lg:py-24">
        <div className="container-section">
          <div className="grid grid-cols-1 items-center gap-12 lg:grid-cols-2">
            <div>
              <h2 className="font-heading text-3xl font-bold text-gray-900">Nuestra Historia</h2>
              <p className="mt-6 leading-relaxed text-gray-600">
                <strong>Carrillo ABGD SAS</strong> fue fundado en abril de 2001 por el Dr. Omar
                Alberto Carrillo Martínez, con la visión de ofrecer servicios jurídicos de alta
                calidad a empresas y particulares en Colombia.
              </p>
              <p className="mt-4 leading-relaxed text-gray-600">
                A lo largo de más de dos décadas, hemos construido una sólida reputación basada en
                el trato personalizado, el conocimiento especializado y los resultados exitosos para
                nuestros clientes.
              </p>
              <p className="mt-4 leading-relaxed text-gray-600">
                Hoy contamos con un equipo de <strong>7 abogados especializados</strong> en
                diferentes áreas del derecho, respaldados por personal administrativo comprometido
                con la excelencia en el servicio.
              </p>
            </div>
            <div className="rounded-2xl bg-gray-100 p-8">
              <div className="grid grid-cols-2 gap-6">
                {stats.map((stat) => (
                  <div key={stat.label} className="text-center">
                    <div className="text-3xl font-bold text-primary-600">{stat.value}</div>
                    <div className="mt-2 text-sm text-gray-600">{stat.label}</div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Misión y Visión */}
      <section className="bg-gray-50 py-16">
        <div className="container-section">
          <div className="grid grid-cols-1 gap-12 md:grid-cols-2">
            <div className="rounded-2xl bg-white p-8 shadow-sm">
              <h3 className="font-heading text-2xl font-bold text-gray-900">Misión</h3>
              <p className="mt-4 leading-relaxed text-gray-600">
                Proporcionar asesoría y representación legal de excelencia, combinando experiencia,
                conocimiento especializado y un trato personalizado para resolver eficazmente las
                necesidades jurídicas de nuestros clientes.
              </p>
            </div>
            <div className="rounded-2xl bg-white p-8 shadow-sm">
              <h3 className="font-heading text-2xl font-bold text-gray-900">Visión</h3>
              <p className="mt-4 leading-relaxed text-gray-600">
                Ser reconocidos como el bufete de referencia en Colombia para servicios jurídicos
                empresariales, destacándonos por nuestra innovación, especialización y compromiso
                con los resultados.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Valores */}
      <section className="py-16 lg:py-24">
        <div className="container-section">
          <div className="mb-12 text-center">
            <h2 className="font-heading text-3xl font-bold text-gray-900">Nuestros Valores</h2>
            <p className="mx-auto mt-4 max-w-2xl text-gray-600">
              Los principios que guían nuestra práctica profesional
            </p>
          </div>
          <div className="grid grid-cols-1 gap-8 md:grid-cols-2 lg:grid-cols-4">
            {values.map((value) => (
              <div key={value.title} className="text-center">
                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-primary-100">
                  <value.icon className="h-8 w-8 text-primary-600" />
                </div>
                <h3 className="mt-4 font-heading text-lg font-semibold text-gray-900">
                  {value.title}
                </h3>
                <p className="mt-2 text-sm text-gray-600">{value.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Ubicación */}
      <section className="bg-primary-600 py-16">
        <div className="container-section text-center">
          <h2 className="font-heading text-3xl font-bold text-white">Nuestra Ubicación</h2>
          <p className="mt-4 text-lg text-primary-100">Torre de Cali, Piso 21, Oficina 2102A</p>
          <p className="text-primary-100">Cra. 40 #27-26, El Piloto, Cali, Valle del Cauca</p>
        </div>
      </section>
    </div>
  );
}
