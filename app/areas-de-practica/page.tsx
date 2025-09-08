import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Card, CardContent } from "@/components/ui/card";
import { 
  Building, 
  Scale, 
  Shield, 
  FileText,
  Users,
  Gavel,
  BookOpen,
  Briefcase,
  Home
} from "lucide-react";

export default function PracticeAreas() {
  const practiceAreas = [
    {
      icon: Building,
      title: "Derecho Corporativo",
      description: "Asesoría integral en constitución de empresas, fusiones, adquisiciones, gobiernos corporativos y compliance. Acompañamos a nuestros clientes en cada etapa del crecimiento empresarial.",
      services: [
        "Constitución de sociedades",
        "Fusiones y adquisiciones",
        "Gobierno corporativo",
        "Compliance empresarial",
        "Contratos comerciales"
      ]
    },
    {
      icon: Scale,
      title: "Litigio y Arbitraje",
      description: "Representación experta en procedimientos judiciales y métodos alternativos de resolución de conflictos. Estrategias procesales efectivas para proteger sus intereses.",
      services: [
        "Litigio civil y comercial",
        "Arbitraje comercial",
        "Mediación empresarial",
        "Juicios ejecutivos",
        "Amparos empresariales"
      ]
    },
    {
      icon: Shield,
      title: "Propiedad Intelectual",
      description: "Protección y defensa de marcas, patentes, derechos de autor y secretos comerciales. Estrategias integrales de propiedad intelectual para empresas innovadoras.",
      services: [
        "Registro de marcas",
        "Patentes y modelos de utilidad",
        "Derechos de autor",
        "Licencias de PI",
        "Defensa antipiratería"
      ]
    },
    {
      icon: FileText,
      title: "Derecho Fiscal",
      description: "Asesoría especializada en planificación fiscal, cumplimiento tributario y defensa ante autoridades fiscales. Optimización de la carga fiscal empresarial.",
      services: [
        "Planificación fiscal",
        "Auditorías fiscales",
        "Juicios fiscales",
        "Reestructuras fiscales",
        "Precios de transferencia"
      ]
    },
    {
      icon: Users,
      title: "Derecho Laboral",
      description: "Gestión integral de relaciones laborales, desde la contratación hasta la terminación. Prevención y resolución de conflictos laborales.",
      services: [
        "Contratos de trabajo",
        "Políticas internas",
        "Juicios laborales",
        "Seguridad social",
        "Outsourcing laboral"
      ]
    },
    {
      icon: Gavel,
      title: "Derecho Penal Empresarial",
      description: "Defensa especializada en delitos económicos y empresariales. Prevención de riesgos penales corporativos y compliance penal.",
      services: [
        "Compliance penal",
        "Delitos económicos",
        "Investigaciones internas",
        "Programas de integridad",
        "Defensa penal corporativa"
      ]
    },
    {
      icon: BookOpen,
      title: "Derecho Financiero",
      description: "Asesoría en operaciones financieras, bursátiles y bancarias. Regulación financiera y estructuración de productos financieros.",
      services: [
        "Operaciones bursátiles",
        "Créditos sindicados",
        "Regulación bancaria",
        "Fintech y regulación",
        "Fondos de inversión"
      ]
    },
    {
      icon: Briefcase,
      title: "Derecho de la Competencia",
      description: "Asesoría en prácticas monopólicas, concentraciones empresariales y libre competencia económica. Defensa ante COFECE.",
      services: [
        "Concentraciones empresariales",
        "Prácticas monopólicas",
        "Investigaciones COFECE",
        "Compliance antimonopolio",
        "Leniencia empresarial"
      ]
    },
    {
      icon: Home,
      title: "Derecho Inmobiliario",
      description: "Asesoría integral en transacciones inmobiliarias, desarrollo de proyectos y regulación urbana. From acquisitions to zoning compliance.",
      services: [
        "Compraventa inmobiliaria",
        "Desarrollo inmobiliario",
        "Arrendamientos comerciales",
        "Fidecomisos inmobiliarios",
        "Regulación urbana"
      ]
    }
  ];

  return (
    <div className="min-h-screen">
      <Header />
      
      {/* Hero Section */}
      <section className="section-padding bg-primary text-primary-foreground">
        <div className="container-custom text-center">
          <h1 className="text-4xl lg:text-6xl font-merriweather font-bold mb-6">
            Áreas de Práctica
          </h1>
          <p className="text-xl text-primary-foreground/90 max-w-3xl mx-auto">
            Servicios jurídicos especializados diseñados para atender las necesidades 
            más complejas de empresas e individuos en el entorno legal actual.
          </p>
        </div>
      </section>

      {/* Practice Areas Grid */}
      <section className="section-padding">
        <div className="container-custom">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {practiceAreas.map((area, index) => (
              <Card key={index} className="practice-card group h-full">
                <CardContent className="p-6 h-full flex flex-col">
                  <div className="w-12 h-12 bg-accent/10 rounded-lg flex items-center justify-center mb-4 group-hover:bg-accent/20 transition-colors duration-300">
                    <area.icon className="w-6 h-6 text-accent" />
                  </div>
                  
                  <h3 className="text-xl font-merriweather font-semibold mb-3 text-primary">
                    {area.title}
                  </h3>
                  
                  <p className="text-muted-foreground leading-relaxed mb-4 flex-grow">
                    {area.description}
                  </p>
                  
                  <div className="border-t border-border pt-4">
                    <h4 className="font-lato font-semibold text-primary mb-2">
                      Servicios incluidos:
                    </h4>
                    <ul className="space-y-1">
                      {area.services.map((service, serviceIndex) => (
                        <li key={serviceIndex} className="text-sm text-muted-foreground flex items-center">
                          <div className="w-1.5 h-1.5 bg-accent rounded-full mr-2 flex-shrink-0"></div>
                          {service}
                        </li>
                      ))}
                    </ul>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="section-padding bg-muted/30">
        <div className="container-custom text-center">
          <h2 className="text-3xl font-merriweather font-bold text-primary mb-4">
            ¿Necesita Asesoría Especializada?
          </h2>
          <p className="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">
            Nuestro equipo de expertos está listo para analizar su caso y 
            ofrecer soluciones jurídicas estratégicas.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <button className="btn-hero">
              Agendar Consulta Gratuita
            </button>
            <button className="btn-outline-gold">
              Solicitar Información
            </button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
}
