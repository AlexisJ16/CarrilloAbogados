import { ArrowRight, Award, Shield, Users } from 'lucide-react';
import Link from 'next/link';

export function HeroSection() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-primary-500 via-primary-600 to-primary-700">
      {/* Background Pattern */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute left-0 top-0 h-96 w-96 -translate-x-1/2 -translate-y-1/2 rounded-full bg-accent-500 blur-3xl" />
        <div className="absolute bottom-0 right-0 h-96 w-96 translate-x-1/2 translate-y-1/2 rounded-full bg-secondary-500 blur-3xl" />
      </div>

      <div className="container-section relative">
        <div className="py-24 lg:py-32">
          <div className="max-w-3xl">
            {/* Badge */}
            <div className="mb-8 inline-flex items-center gap-2 rounded-full bg-white/10 px-4 py-2 text-sm text-white backdrop-blur-sm">
              <Award className="h-4 w-4 text-accent-400" />
              <span>Más de 23 años de experiencia legal</span>
            </div>

            {/* Headline */}
            <h1 className="mb-6 font-heading text-4xl font-bold leading-tight text-white sm:text-5xl lg:text-6xl">
              Soluciones legales <span className="text-accent-400">efectivas</span> para su empresa
            </h1>

            {/* Subheadline */}
            <p className="mb-8 max-w-2xl text-lg text-gray-200 sm:text-xl">
              Bufete de abogados especializado en Derecho Administrativo, Corporativo,
              Telecomunicaciones, Marcas y Libre Competencia. Protegemos los intereses de su empresa
              con experiencia y compromiso.
            </p>

            {/* CTAs */}
            <div className="mb-12 flex flex-col gap-4 sm:flex-row">
              <Link
                href="/contact"
                className="btn-primary inline-flex items-center justify-center gap-2 bg-accent-500 hover:bg-accent-600"
              >
                Agendar Consulta Gratuita
                <ArrowRight className="h-5 w-5" />
              </Link>
              <Link
                href="/servicios"
                className="btn-outline inline-flex items-center justify-center border-white text-white hover:bg-white hover:text-primary-500"
              >
                Ver Áreas de Práctica
              </Link>
            </div>

            {/* Stats */}
            <div className="grid grid-cols-3 gap-8 border-t border-white/20 pt-8">
              <div>
                <div className="mb-1 flex items-center gap-2">
                  <Shield className="h-5 w-5 text-accent-400" />
                  <span className="text-3xl font-bold text-white">23+</span>
                </div>
                <p className="text-sm text-gray-300">Años de experiencia</p>
              </div>
              <div>
                <div className="mb-1 flex items-center gap-2">
                  <Users className="h-5 w-5 text-accent-400" />
                  <span className="text-3xl font-bold text-white">500+</span>
                </div>
                <p className="text-sm text-gray-300">Clientes satisfechos</p>
              </div>
              <div>
                <div className="mb-1 flex items-center gap-2">
                  <Award className="h-5 w-5 text-accent-400" />
                  <span className="text-3xl font-bold text-white">5</span>
                </div>
                <p className="text-sm text-gray-300">Áreas de práctica</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
