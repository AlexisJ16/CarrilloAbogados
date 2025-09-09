import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Mail, Linkedin, GraduationCap, Award } from "lucide-react";

const omarImage = "https://carrilloabgd.com/Imagenes/doc.jpg";
const lawyerMaria = "/lawyer-maria.jpg";
const lawyerCarlos = "/lawyer-carlos.jpg";

export default function OurTeam() {
  const contactLink =
    "https://api.whatsapp.com/send?phone=573153344411&text=Hola,%20quiero%20contactarme%20con%20Carrillo%20Abogados";

  const teamMembers = [
    {
      name: "Omar Alberto Carrillo Martínez",
      title: "Director General",
      specialization: "Derecho Administrativo y Constitucional",
      image: omarImage,
      education: [
        "Abogado del Colegio Mayor de Nuestra Señora del Rosario",
        "Especialista en Derecho Administrativo del Rosario",
      ],
      experience: "Más de 30 años de experiencia",
      achievements: [
        "Ex Superintendente Delegado para la Propiedad Industrial (SIC)",
        "Jefe de la Oficina Jurídica del Ministerio de Comunicaciones",
        "Abogado asistente en el Consejo de Estado",
      ],
      bio: "Abogado especializado en derecho administrativo con amplia trayectoria en el sector público y privado.",
      linkedin:
        "https://www.linkedin.com/in/omar-alberto-carrillo-martinez-9016b261/?originalSubdomain=co",
      whatsapp: contactLink,
      languages: ["Español", "Inglés"],
    },
    {
      name: "María González",
      title: "Abogada Senior",
      specialization: "Derecho Corporativo",
      image: lawyerMaria,
      education: [
        "LLM en Derecho Corporativo",
        "Universidad de los Andes",
      ],
      experience: "15 años de experiencia",
      achievements: [
        "Asesora de empresas nacionales e internacionales",
        "Miembro de la Barra de Abogados",
      ],
      bio: "Especialista en estructuración corporativa y fusiones.",
      linkedin: "#",
      whatsapp: contactLink,
      languages: ["Español", "Inglés"],
    },
    {
      name: "Carlos Mendoza",
      title: "Abogado",
      specialization: "Litigio Comercial",
      image: lawyerCarlos,
      education: [
        "Maestría en Arbitraje Internacional",
        "Universidad de Londres",
      ],
      experience: "10 años de experiencia",
      achievements: [
        "Árbitro certificado por la CCI",
        "Profesor invitado en universidades",
      ],
      bio: "Litigante enfocado en disputas comerciales complejas.",
      linkedin: "#",
      whatsapp: contactLink,
      languages: ["Español", "Inglés"],
    },
    {
      name: "Ana Rodríguez",
      title: "Abogada",
      specialization: "Propiedad Intelectual",
      image: lawyerMaria,
      education: [
        "LLM en Propiedad Intelectual",
        "Stanford University",
      ],
      experience: "8 años de experiencia",
      achievements: [
        "IP Star 2024 - Managing IP",
        "Miembro de asociaciones de tecnología",
      ],
      bio: "Lidera estrategias de protección para innovaciones tecnológicas.",
      linkedin: "#",
      whatsapp: contactLink,
      languages: ["Español", "Inglés"],
    },
  ];

  return (
    <div className="min-h-screen">
      <Header />
      
      {/* Hero Section */}
      <section className="section-padding bg-primary text-primary-foreground">
        <div className="container-custom text-center">
          <h1 className="text-4xl lg:text-6xl font-merriweather font-bold mb-6">
            Nuestro Equipo
          </h1>
          <p className="text-xl text-primary-foreground/90 max-w-3xl mx-auto">
            Profesionales destacados unidos por la excelencia, la integridad y 
            el compromiso inquebrantable con el éxito de nuestros clientes.
          </p>
        </div>
      </section>

      {/* Team Grid */}
      <section className="section-padding">
        <div className="container-custom">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
            {teamMembers.map((member, index) => (
              <Card key={index} className="team-card overflow-visible">
                <div className="flex flex-col lg:flex-row">
                  {/* Image */}
                  <div className="lg:w-1/3">
                    <div className="aspect-square lg:aspect-auto lg:h-full overflow-hidden">
                      <img 
                        src={member.image} 
                        alt={member.name}
                        className="w-full h-full object-cover"
                      />
                    </div>
                  </div>
                  
                  {/* Content */}
                  <CardContent className="p-6 lg:w-2/3 lg:p-8">
                    <div className="mb-4">
                      <h3 className="text-2xl font-merriweather font-bold text-primary mb-1">
                        {member.name}
                      </h3>
                      <p className="text-accent font-semibold text-lg mb-1">
                        {member.title}
                      </p>
                      <p className="text-muted-foreground font-medium">
                        {member.specialization}
                      </p>
                      <p className="text-sm text-muted-foreground mt-1">
                        {member.experience}
                      </p>
                    </div>

                    <p className="text-muted-foreground leading-relaxed mb-4 text-sm">
                      {member.bio}
                    </p>

                    {/* Education */}
                    <div className="mb-4">
                      <h4 className="flex items-center font-semibold text-primary text-sm mb-2">
                        <GraduationCap className="w-4 h-4 mr-2 text-accent" />
                        Formación Académica
                      </h4>
                      <ul className="space-y-1">
                        {member.education.map((edu, eduIndex) => (
                          <li key={eduIndex} className="text-xs text-muted-foreground">
                            • {edu}
                          </li>
                        ))}
                      </ul>
                    </div>

                    {/* Achievements */}
                    <div className="mb-4">
                      <h4 className="flex items-center font-semibold text-primary text-sm mb-2">
                        <Award className="w-4 h-4 mr-2 text-accent" />
                        Reconocimientos
                      </h4>
                      <ul className="space-y-1">
                        {member.achievements.slice(0, 2).map((achievement, achIndex) => (
                          <li key={achIndex} className="text-xs text-muted-foreground">
                            • {achievement}
                          </li>
                        ))}
                      </ul>
                    </div>

                    {/* Languages */}
                    <div className="mb-4">
                      <p className="text-xs text-muted-foreground">
                        <span className="font-semibold">Idiomas:</span> {member.languages.join(", ")}
                      </p>
                    </div>

                    {/* Contact */}
                    <div className="flex items-center space-x-3 pt-4 border-t border-border">
                      <Button
                        asChild
                        variant="outline"
                        size="sm"
                        className="flex items-center"
                      >
                        <a
                          href={member.whatsapp}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <Mail className="w-4 h-4 mr-2" />
                          Contactar
                        </a>
                      </Button>
                      <Button
                        asChild
                        variant="outline"
                        size="sm"
                        className="flex items-center"
                      >
                        <a
                          href={member.linkedin}
                          target="_blank"
                          rel="noopener noreferrer"
                        >
                          <Linkedin className="w-4 h-4 mr-2" />
                          LinkedIn
                        </a>
                      </Button>
                    </div>
                  </CardContent>
                </div>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="section-padding bg-muted/30">
        <div className="container-custom text-center">
          <h2 className="text-3xl font-merriweather font-bold text-primary mb-4">
            ¿Listo para Trabajar con Nosotros?
          </h2>
          <p className="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">
            Nuestro equipo está preparado para brindarle la asesoría legal 
            que su empresa o caso requiere.
          </p>
          <Button className="btn-hero">
            Agendar Reunión con Nuestro Equipo
          </Button>
        </div>
      </section>

      <Footer />
    </div>
  );
}
