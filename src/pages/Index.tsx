import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { 
  Building, 
  Scale, 
  Shield, 
  Award, 
  Lock, 
  TrendingUp,
  ArrowRight,
  CheckCircle,
  Users
} from "lucide-react";
import { Link } from "react-router-dom";
import heroImage from "@/assets/hero-office.jpg";
import handshakeImage from "@/assets/handshake.jpg";
import lawyerMaria from "@/assets/lawyer-maria.jpg";
import lawyerCarlos from "@/assets/lawyer-carlos.jpg";

const Index = () => {
  const practiceAreas = [
    {
      icon: Building,
      title: "Derecho Corporativo",
      description: "Asesoría integral en constitución de empresas, fusiones, adquisiciones y gobiernos corporativos.",
    },
    {
      icon: Scale,
      title: "Litigio y Arbitraje",
      description: "Representación experta en procedimientos judiciales y métodos alternativos de resolución de conflictos.",
    },
    {
      icon: Shield,
      title: "Propiedad Intelectual",
      description: "Protección y defensa de marcas, patentes, derechos de autor y secretos comerciales.",
    },
  ];

  const differentiators = [
    {
      icon: Award,
      title: "Experiencia Comprobada",
      description: "Más de 20 años de trayectoria exitosa respaldando a empresas líderes y profesionales destacados.",
      image: handshakeImage,
      reverse: false,
    },
    {
      icon: Lock,
      title: "Confidencialidad Absoluta",
      description: "Manejo estricto de información privilegiada con los más altos estándares de seguridad y discreción.",
      image: heroImage,
      reverse: true,
    },
    {
      icon: TrendingUp,
      title: "Resultados Medibles",
      description: "Estrategias orientadas a objetivos concretos con seguimiento continuo y métricas de éxito definidas.",
      image: handshakeImage,
      reverse: false,
    },
  ];

  const teamMembers = [
    {
      name: "María Carrillo",
      title: "Socia Fundadora",
      specialization: "Derecho Corporativo",
      image: lawyerMaria,
    },
    {
      name: "Carlos Mendoza",
      title: "Socio Senior",
      specialization: "Litigio Comercial",
      image: lawyerCarlos,
    },
  ];

  return (
    <div className="min-h-screen">
      <Header />
      
      {/* Hero Section */}
      <section className="relative h-screen flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0">
          <img 
            src={heroImage} 
            alt="Modern law office interior"
            className="w-full h-full object-cover"
          />
          <div className="hero-overlay"></div>
        </div>
        
        <div className="relative z-10 text-center text-white max-w-4xl mx-auto px-4">
          <h1 className="text-5xl lg:text-7xl font-merriweather font-bold mb-6 leading-tight">
            Asesoría Legal Estratégica para un 
            <span className="text-accent"> Mundo Complejo</span>
          </h1>
          <p className="text-xl lg:text-2xl font-lato mb-8 text-white/90 max-w-3xl mx-auto">
            Soluciones jurídicas integrales respaldadas por décadas de experiencia 
            y un compromiso inquebrantable con la excelencia.
          </p>
          <Link to="/areas-de-practica">
            <Button className="btn-hero text-lg">
              Conozca Nuestros Servicios
              <ArrowRight className="ml-2 w-5 h-5" />
            </Button>
          </Link>
        </div>
      </section>

      {/* Practice Areas Section */}
      <section className="section-padding bg-muted/30">
        <div className="container-custom">
          <div className="text-center mb-12">
            <h2 className="text-primary mb-4">Áreas de Práctica</h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Ofrecemos servicios especializados en las principales áreas del derecho, 
              con un enfoque estratégico y soluciones personalizadas.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {practiceAreas.map((area, index) => (
              <Card key={index} className="practice-card">
                <CardContent className="p-6">
                  <div className="w-12 h-12 bg-accent/10 rounded-lg flex items-center justify-center mb-4 group-hover:bg-accent/20 transition-colors duration-300">
                    <area.icon className="w-6 h-6 text-accent" />
                  </div>
                  <h3 className="text-xl font-merriweather font-semibold mb-3 text-primary">
                    {area.title}
                  </h3>
                  <p className="text-muted-foreground leading-relaxed">
                    {area.description}
                  </p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Why Choose Us Section */}
      <section className="section-padding">
        <div className="container-custom">
          <div className="text-center mb-16">
            <h2 className="text-primary mb-4">¿Por Qué Elegirnos?</h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Nuestro compromiso va más allá de la asesoría legal; construimos 
              relaciones de confianza a largo plazo.
            </p>
          </div>

          <div className="space-y-24">
            {differentiators.map((item, index) => (
              <div 
                key={index} 
                className={`flex flex-col lg:flex-row items-center gap-12 ${
                  item.reverse ? 'lg:flex-row-reverse' : ''
                }`}
              >
                <div className="flex-1">
                  <div className="w-16 h-16 bg-accent/10 rounded-xl flex items-center justify-center mb-6">
                    <item.icon className="w-8 h-8 text-accent" />
                  </div>
                  <h3 className="text-3xl font-merriweather font-bold text-primary mb-4">
                    {item.title}
                  </h3>
                  <p className="text-lg text-muted-foreground leading-relaxed mb-6">
                    {item.description}
                  </p>
                  <div className="flex items-center text-accent font-medium">
                    <CheckCircle className="w-5 h-5 mr-2" />
                    Resultados garantizados
                  </div>
                </div>
                <div className="flex-1">
                  <img 
                    src={item.image} 
                    alt={item.title}
                    className="w-full h-80 object-cover rounded-xl shadow-lg"
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Meet the Team Section */}
      <section className="section-padding bg-muted/30">
        <div className="container-custom">
          <div className="text-center mb-12">
            <h2 className="text-primary mb-4">Conozca Nuestro Equipo</h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Profesionales destacados con amplia experiencia y reconocimiento 
              en sus respectivas áreas de especialización.
            </p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            {teamMembers.map((member, index) => (
              <Card key={index} className="team-card">
                <div className="aspect-square overflow-hidden">
                  <img 
                    src={member.image} 
                    alt={member.name}
                    className="w-full h-full object-cover"
                  />
                </div>
                <CardContent className="p-6 text-center">
                  <h3 className="text-xl font-merriweather font-semibold text-primary mb-1">
                    {member.name}
                  </h3>
                  <p className="text-accent font-medium mb-2">{member.title}</p>
                  <p className="text-muted-foreground">{member.specialization}</p>
                </CardContent>
              </Card>
            ))}
          </div>
          
          <div className="text-center mt-8">
            <Link to="/nuestro-equipo">
              <Button className="btn-outline-gold">
                <Users className="mr-2 w-5 h-5" />
                Ver Todo el Equipo
              </Button>
            </Link>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
};

export default Index;