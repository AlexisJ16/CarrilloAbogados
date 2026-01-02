import { ArrowRight, CheckCircle } from 'lucide-react';
import Link from 'next/link';

const features = [
  'Más de 23 años de experiencia en derecho empresarial',
  'Equipo de 7 abogados especializados',
  'Atención personalizada a cada cliente',
  '15 años de experiencia en la SIC',
  'Representación ante entidades públicas',
  'Soluciones legales a la medida',
];

export function AboutSection() {
  return (
    <section className="py-20 lg:py-28 bg-white">
      <div className="container-section">
        <div className="grid lg:grid-cols-2 gap-12 items-center">
          {/* Content */}
          <div>
            <span className="text-accent-500 font-semibold text-sm uppercase tracking-wider">
              Sobre Nosotros
            </span>
            <h2 className="text-3xl sm:text-4xl font-heading font-bold text-primary-500 mt-2 mb-6">
              El trato personalizado que su empresa merece
            </h2>
            <p className="text-gray-600 text-lg mb-6">
              <strong>Carrillo ABGD SAS</strong> es un bufete de abogados fundado en abril de 2001, 
              con más de dos décadas de trayectoria brindando soluciones jurídicas efectivas 
              a empresas y particulares en Colombia.
            </p>
            <p className="text-gray-600 mb-8">
              Nuestro equipo multidisciplinario combina experiencia, compromiso y conocimiento 
              profundo en cada área de práctica. El Dr. Omar Carrillo, socio fundador, cuenta 
              con más de 15 años de experiencia directa en la Superintendencia de Industria y 
              Comercio, lo que nos permite ofrecer una perspectiva única en temas de marcas, 
              competencia y propiedad industrial.
            </p>
            
            {/* Features */}
            <ul className="grid sm:grid-cols-2 gap-3 mb-8">
              {features.map((feature) => (
                <li key={feature} className="flex items-start gap-2">
                  <CheckCircle className="h-5 w-5 text-accent-500 flex-shrink-0 mt-0.5" />
                  <span className="text-gray-700 text-sm">{feature}</span>
                </li>
              ))}
            </ul>
            
            <Link href="/nosotros" className="btn-primary inline-flex items-center gap-2">
              Conocer nuestra historia
              <ArrowRight className="h-5 w-5" />
            </Link>
          </div>
          
          {/* Image/Visual */}
          <div className="relative">
            <div className="aspect-square bg-gradient-to-br from-primary-500 to-secondary-500 rounded-2xl overflow-hidden">
              {/* Placeholder for actual image */}
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="text-center text-white p-8">
                  <p className="font-heading text-4xl font-bold mb-2">23+</p>
                  <p className="text-lg opacity-90">Años protegiendo empresas</p>
                </div>
              </div>
              {/* Decorative elements */}
              <div className="absolute -bottom-4 -right-4 w-32 h-32 bg-accent-500 rounded-2xl -z-10" />
              <div className="absolute -top-4 -left-4 w-24 h-24 bg-secondary-200 rounded-2xl -z-10" />
            </div>
            
            {/* Floating card */}
            <div className="absolute -bottom-8 -left-8 bg-white rounded-xl shadow-xl p-6 max-w-xs">
              <p className="text-gray-600 italic mb-3">
                &ldquo;Cada cliente recibe atención directa y especializada, no es un número más.&rdquo;
              </p>
              <p className="text-sm font-semibold text-primary-500">
                — Dr. Omar Carrillo, Socio Director
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
