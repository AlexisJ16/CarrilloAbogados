import { ArrowRight, Award, Shield, Users } from 'lucide-react';
import Link from 'next/link';

export function HeroSection() {
  return (
    <section className="relative bg-gradient-to-br from-primary-500 via-primary-600 to-primary-700 overflow-hidden">
      {/* Background Pattern */}
      <div className="absolute inset-0 opacity-10">
        <div className="absolute top-0 left-0 w-96 h-96 bg-accent-500 rounded-full -translate-x-1/2 -translate-y-1/2 blur-3xl" />
        <div className="absolute bottom-0 right-0 w-96 h-96 bg-secondary-500 rounded-full translate-x-1/2 translate-y-1/2 blur-3xl" />
      </div>
      
      <div className="container-section relative">
        <div className="py-24 lg:py-32">
          <div className="max-w-3xl">
            {/* Badge */}
            <div className="inline-flex items-center gap-2 bg-white/10 backdrop-blur-sm text-white px-4 py-2 rounded-full text-sm mb-8">
              <Award className="h-4 w-4 text-accent-400" />
              <span>Más de 23 años de experiencia legal</span>
            </div>
            
            {/* Headline */}
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-heading font-bold text-white leading-tight mb-6">
              Soluciones legales{' '}
              <span className="text-accent-400">efectivas</span> para su empresa
            </h1>
            
            {/* Subheadline */}
            <p className="text-lg sm:text-xl text-gray-200 mb-8 max-w-2xl">
              Bufete de abogados especializado en Derecho Administrativo, Corporativo, 
              Telecomunicaciones, Marcas y Libre Competencia. Protegemos los intereses 
              de su empresa con experiencia y compromiso.
            </p>
            
            {/* CTAs */}
            <div className="flex flex-col sm:flex-row gap-4 mb-12">
              <Link
                href="/contacto"
                className="btn-primary bg-accent-500 hover:bg-accent-600 inline-flex items-center justify-center gap-2"
              >
                Agendar Consulta Gratuita
                <ArrowRight className="h-5 w-5" />
              </Link>
              <Link
                href="/servicios"
                className="btn-outline border-white text-white hover:bg-white hover:text-primary-500 inline-flex items-center justify-center"
              >
                Ver Áreas de Práctica
              </Link>
            </div>
            
            {/* Stats */}
            <div className="grid grid-cols-3 gap-8 pt-8 border-t border-white/20">
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <Shield className="h-5 w-5 text-accent-400" />
                  <span className="text-3xl font-bold text-white">23+</span>
                </div>
                <p className="text-sm text-gray-300">Años de experiencia</p>
              </div>
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <Users className="h-5 w-5 text-accent-400" />
                  <span className="text-3xl font-bold text-white">500+</span>
                </div>
                <p className="text-sm text-gray-300">Clientes satisfechos</p>
              </div>
              <div>
                <div className="flex items-center gap-2 mb-1">
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
