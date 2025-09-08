import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Mail, Linkedin, GraduationCap, Award } from "lucide-react";

const lawyerMaria = "/lawyer-maria.jpg";
const lawyerCarlos = "/lawyer-carlos.jpg";

export default function OurTeam() {
  const teamMembers = [
    {
      name: "María Carrillo",
      title: "Socia Fundadora",
      specialization: "Derecho Corporativo y M&A",
      image: lawyerMaria,
      education: [
        "LLM en Derecho Corporativo, Harvard Law School",
        "Licenciada en Derecho, Universidad Nacional Autónoma de México"
      ],
      experience: "25 años de experiencia",
      achievements: [
        "Reconocida por Chambers & Partners como Leading Lawyer",
        "Mejor Abogada del Año 2023 - Revista Legal 500",
        "Miembro del Consejo Directivo de la Barra Mexicana de Abogados"
      ],
      bio: "María Carrillo es una destacada especialista en derecho corporativo con más de 25 años de experiencia asesorando a empresas multinacionales y fondos de inversión. Ha liderado algunas de las transacciones más importantes del país.",
      email: "mcarrillo@carrillo-abogados.com",
      languages: ["Español", "Inglés", "Francés"]
    },
    {
      name: "Carlos Mendoza",
      title: "Socio Senior",
      specialization: "Litigio Comercial y Arbitraje",
      image: lawyerCarlos,
      education: [
        "Maestría en Arbitraje Internacional, Universidad de Londres",
        "Licenciado en Derecho, Instituto Tecnológico Autónomo de México"
      ],
      experience: "20 años de experiencia",
      achievements: [
        "Árbitro certificado por la Cámara de Comercio Internacional",
        "Top Litigator 2024 - International Law Review",
        "Profesor invitado en la Universidad Panamericana"
      ],
      bio: "Carlos Mendoza es un litigante experimentado especializado en disputas comerciales complejas y arbitraje internacional. Ha representado clientes en más de 200 procedimientos arbitrales.",
      email: "cmendoza@carrillo-abogados.com",
      languages: ["Español", "Inglés", "Italiano"]
    },
    {
      name: "Ana Rodríguez",
      title: "Socia",
      specialization: "Propiedad Intelectual y Tecnología",
      image: lawyerMaria,
      education: [
        "LLM en Propiedad Intelectual, Stanford University",
        "Licenciada en Derecho, Universidad Iberoamericana"
      ],
      experience: "15 años de experiencia",
      achievements: [
        "IP Star 2024 - Managing Intellectual Property",
        "Líder en Innovación Legal - Expansión Magazine",
        "Miembro del Advisory Board de Tech Law Association"
      ],
      bio: "Ana Rodríguez lidera nuestro departamento de propiedad intelectual, especializada en protección de innovaciones tecnológicas y estrategias de PI para startups y empresas tech.",
      email: "arodriguez@carrillo-abogados.com",
      languages: ["Español", "Inglés", "Alemán"]
    },
    {
      name: "Roberto Jiménez",
      title: "Socio",
      specialization: "Derecho Fiscal y Financiero",
      image: lawyerCarlos,
      education: [
        "Maestría en Derecho Fiscal, Universidad de Nueva York",
        "Contador Público, Universidad Nacional Autónoma de México"
      ],
      experience: "18 años de experiencia",
      achievements: [
        "Tax Lawyer of the Year 2023 - Mexican Tax Review",
        "Certificado en Precios de Transferencia por la OCDE",
        "Autor del libro 'Reforma Fiscal Contemporánea'"
      ],
      bio: "Roberto Jiménez combina su formación en derecho y contaduría para ofrecer asesoría fiscal integral, especializándose en reestructuras fiscales y precios de transferencia.",
      email: "rjimenez@carrillo-abogados.com",
      languages: ["Español", "Inglés"]
    }
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
                      <Button variant="outline" size="sm" className="flex items-center">
                        <Mail className="w-4 h-4 mr-2" />
                        Contactar
                      </Button>
                      <Button variant="outline" size="sm" className="flex items-center">
                        <Linkedin className="w-4 h-4 mr-2" />
                        LinkedIn
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
