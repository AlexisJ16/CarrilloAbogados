'use client';

import { ArrowRight, BookOpen, Calendar, Clock, Search, Tag } from 'lucide-react';
import Link from 'next/link';

// Artículos de ejemplo - En producción vendrían del CMS/API
const featuredPost = {
  slug: 'proteger-marca-colombia-2026',
  title: '¿Cómo proteger tu marca en Colombia en 2026?',
  excerpt:
    'Guía completa sobre el proceso de registro de marcas ante la SIC, requisitos, costos y tiempos. Todo lo que necesitas saber para proteger tu propiedad industrial.',
  category: 'Propiedad Industrial',
  author: 'Dr. Omar Carrillo',
  date: '15 Enero, 2026',
  readTime: '8 min',
  image: null,
};

const posts = [
  {
    slug: 'contratacion-estatal-guia-basica',
    title: 'Contratación Estatal: Guía Básica para Empresas',
    excerpt:
      'Aprenda cómo participar en procesos de contratación pública y evitar los errores más comunes.',
    category: 'Derecho Administrativo',
    author: 'Equipo Legal',
    date: '10 Enero, 2026',
    readTime: '6 min',
  },
  {
    slug: 'competencia-desleal-que-hacer',
    title: '¿Víctima de Competencia Desleal? Esto es lo que debes hacer',
    excerpt:
      'Identifica las señales de competencia desleal y conoce los recursos legales disponibles.',
    category: 'Derecho de Competencias',
    author: 'Equipo Legal',
    date: '5 Enero, 2026',
    readTime: '5 min',
  },
  {
    slug: 'constituir-sas-colombia',
    title: 'Cómo Constituir una S.A.S. en Colombia',
    excerpt: 'Paso a paso para crear tu Sociedad por Acciones Simplificada de manera correcta.',
    category: 'Derecho Corporativo',
    author: 'Equipo Legal',
    date: '28 Diciembre, 2025',
    readTime: '7 min',
  },
  {
    slug: 'habilitacion-mintic-operadores',
    title: 'Habilitación ante MinTIC: Requisitos para Operadores',
    excerpt:
      'Todo sobre las habilitaciones y registros TIC que necesita tu empresa de telecomunicaciones.',
    category: 'Telecomunicaciones',
    author: 'Equipo Legal',
    date: '20 Diciembre, 2025',
    readTime: '10 min',
  },
];

const categories = [
  'Todos',
  'Derecho Administrativo',
  'Derecho de Competencias',
  'Derecho Corporativo',
  'Telecomunicaciones',
  'Propiedad Industrial',
];

export default function BlogPage() {
  return (
    <div className="bg-white">
      {/* Hero Section */}
      <div className="relative bg-primary-600 py-24">
        <div className="container-section">
          <div className="text-center">
            <h1 className="font-heading text-4xl font-bold text-white sm:text-5xl">
              Blog Jurídico
            </h1>
            <p className="mx-auto mt-6 max-w-3xl text-lg text-primary-100">
              Artículos, guías y noticias sobre actualidad legal en Colombia. Manténgase informado
              con contenido de nuestros expertos.
            </p>
          </div>
        </div>
      </div>

      {/* Search and Filter */}
      <section className="border-b border-gray-200 py-8">
        <div className="container-section">
          <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
            <div className="relative w-full md:w-96">
              <Search className="absolute left-3 top-1/2 h-5 w-5 -translate-y-1/2 transform text-gray-400" />
              <input
                type="text"
                placeholder="Buscar artículos..."
                className="w-full rounded-lg border border-gray-300 py-2 pl-10 pr-4 focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div className="flex flex-wrap justify-center gap-2">
              {categories.map((category) => (
                <button
                  key={category}
                  className={`rounded-full px-4 py-2 text-sm font-medium transition-colors ${
                    category === 'Todos'
                      ? 'bg-primary-600 text-white'
                      : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  {category}
                </button>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Featured Post */}
      <section className="py-16">
        <div className="container-section">
          <div className="grid grid-cols-1 items-center gap-12 lg:grid-cols-2">
            <div className="flex aspect-video items-center justify-center rounded-2xl bg-gray-100">
              <BookOpen className="h-24 w-24 text-gray-300" />
            </div>
            <div>
              <div className="mb-4 flex items-center gap-4">
                <span className="inline-flex items-center gap-1 rounded-full bg-primary-100 px-3 py-1 text-sm font-medium text-primary-700">
                  <Tag className="h-3 w-3" />
                  {featuredPost.category}
                </span>
                <span className="text-sm text-gray-500">Destacado</span>
              </div>
              <h2 className="font-heading text-3xl font-bold text-gray-900">
                <Link
                  href={`/blog/${featuredPost.slug}`}
                  className="transition-colors hover:text-primary-600"
                >
                  {featuredPost.title}
                </Link>
              </h2>
              <p className="mt-4 leading-relaxed text-gray-600">{featuredPost.excerpt}</p>
              <div className="mt-6 flex items-center gap-6 text-sm text-gray-500">
                <span>{featuredPost.author}</span>
                <span className="flex items-center gap-1">
                  <Calendar className="h-4 w-4" />
                  {featuredPost.date}
                </span>
                <span className="flex items-center gap-1">
                  <Clock className="h-4 w-4" />
                  {featuredPost.readTime}
                </span>
              </div>
              <Link
                href={`/blog/${featuredPost.slug}`}
                className="mt-6 inline-flex items-center gap-2 font-semibold text-primary-600 transition-all hover:gap-3"
              >
                Leer artículo
                <ArrowRight className="h-4 w-4" />
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Posts Grid */}
      <section className="bg-gray-50 py-16">
        <div className="container-section">
          <h2 className="mb-8 font-heading text-2xl font-bold text-gray-900">
            Artículos Recientes
          </h2>
          <div className="grid grid-cols-1 gap-8 md:grid-cols-2">
            {posts.map((post) => (
              <article
                key={post.slug}
                className="overflow-hidden rounded-xl bg-white shadow-sm transition-shadow hover:shadow-md"
              >
                <div className="flex h-48 items-center justify-center bg-gray-100">
                  <BookOpen className="h-12 w-12 text-gray-300" />
                </div>
                <div className="p-6">
                  <span className="inline-flex items-center gap-1 rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-700">
                    {post.category}
                  </span>
                  <h3 className="mt-4 font-heading text-xl font-semibold text-gray-900">
                    <Link
                      href={`/blog/${post.slug}`}
                      className="transition-colors hover:text-primary-600"
                    >
                      {post.title}
                    </Link>
                  </h3>
                  <p className="mt-2 line-clamp-2 text-sm text-gray-600">{post.excerpt}</p>
                  <div className="mt-4 flex items-center justify-between text-xs text-gray-500">
                    <span>{post.author}</span>
                    <div className="flex items-center gap-4">
                      <span className="flex items-center gap-1">
                        <Calendar className="h-3 w-3" />
                        {post.date}
                      </span>
                      <span className="flex items-center gap-1">
                        <Clock className="h-3 w-3" />
                        {post.readTime}
                      </span>
                    </div>
                  </div>
                </div>
              </article>
            ))}
          </div>

          {/* Load More */}
          <div className="mt-12 text-center">
            <button className="rounded-lg border border-gray-300 px-6 py-3 font-semibold text-gray-700 transition-colors hover:bg-gray-50">
              Cargar más artículos
            </button>
          </div>
        </div>
      </section>

      {/* Newsletter CTA */}
      <section className="bg-primary-600 py-16">
        <div className="container-section text-center">
          <h2 className="font-heading text-3xl font-bold text-white">
            Suscríbase a nuestro Newsletter
          </h2>
          <p className="mx-auto mt-4 max-w-2xl text-primary-100">
            Reciba las últimas noticias legales y artículos directamente en su correo.
          </p>
          <form className="mx-auto mt-8 flex max-w-md flex-col gap-4 sm:flex-row">
            <input
              type="email"
              placeholder="Su correo electrónico"
              className="flex-1 rounded-lg px-4 py-3 focus:ring-2 focus:ring-primary-300"
            />
            <button
              type="submit"
              className="rounded-lg bg-white px-6 py-3 font-semibold text-primary-600 transition-colors hover:bg-gray-100"
            >
              Suscribirse
            </button>
          </form>
          <p className="mt-4 text-sm text-primary-200">
            Sin spam. Puede cancelar en cualquier momento.
          </p>
        </div>
      </section>
    </div>
  );
}
