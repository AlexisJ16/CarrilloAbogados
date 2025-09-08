import Header from "@/components/Header";
import Footer from "@/components/Footer";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { 
  MapPin, 
  Phone, 
  Mail, 
  Clock,
  Send,
  MessageSquare
} from "lucide-react";

export default function Contact() {
  const contactInfo = [
    {
      icon: MapPin,
      title: "Oficina Principal",
      details: ["Av. Reforma 123, Piso 15", "Col. Juárez, CDMX 06600", "México"],
    },
    {
      icon: Phone,
      title: "Teléfonos",
      details: ["+52 55 1234 5678", "+52 55 8765 4321", "Línea de emergencias: +52 55 9999 0000"],
    },
    {
      icon: Mail,
      title: "Correo Electrónico",
      details: ["info@carrillo-abogados.com", "consultas@carrillo-abogados.com", "emergencias@carrillo-abogados.com"],
    },
    {
      icon: Clock,
      title: "Horarios de Atención",
      details: ["Lunes a Viernes: 9:00 - 18:00", "Sábados: 10:00 - 14:00", "Emergencias: 24/7"],
    },
  ];

  return (
    <div className="min-h-screen">
      <Header />
      
      {/* Hero Section */}
      <section className="section-padding bg-primary text-primary-foreground">
        <div className="container-custom text-center">
          <h1 className="text-4xl lg:text-6xl font-merriweather font-bold mb-6">
            Contacto
          </h1>
          <p className="text-xl text-primary-foreground/90 max-w-3xl mx-auto">
            Estamos aquí para ayudarle. Póngase en contacto con nuestro equipo 
            para una consulta personalizada y soluciones jurídicas estratégicas.
          </p>
        </div>
      </section>

      {/* Contact Information */}
      <section className="section-padding">
        <div className="container-custom">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-16">
            {contactInfo.map((info, index) => (
              <Card key={index} className="text-center h-full">
                <CardContent className="p-6">
                  <div className="w-12 h-12 bg-accent/10 rounded-lg flex items-center justify-center mx-auto mb-4">
                    <info.icon className="w-6 h-6 text-accent" />
                  </div>
                  <h3 className="text-lg font-merriweather font-semibold text-primary mb-3">
                    {info.title}
                  </h3>
                  <div className="space-y-1">
                    {info.details.map((detail, detailIndex) => (
                      <p key={detailIndex} className="text-sm text-muted-foreground">
                        {detail}
                      </p>
                    ))}
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Main Contact Section */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
            {/* Contact Form */}
            <Card>
              <CardContent className="p-8">
                <div className="flex items-center mb-6">
                  <MessageSquare className="w-6 h-6 text-accent mr-3" />
                  <h2 className="text-2xl font-merriweather font-bold text-primary">
                    Envíenos un Mensaje
                  </h2>
                </div>
                
                <form className="space-y-6">
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="nombre" className="text-sm font-medium text-primary">
                        Nombre Completo *
                      </Label>
                      <Input 
                        id="nombre" 
                        placeholder="Su nombre completo"
                        className="mt-1"
                        required
                      />
                    </div>
                    <div>
                      <Label htmlFor="empresa" className="text-sm font-medium text-primary">
                        Empresa / Organización
                      </Label>
                      <Input 
                        id="empresa" 
                        placeholder="Nombre de su empresa"
                        className="mt-1"
                      />
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                      <Label htmlFor="email" className="text-sm font-medium text-primary">
                        Correo Electrónico *
                      </Label>
                      <Input 
                        id="email" 
                        type="email"
                        placeholder="su@email.com"
                        className="mt-1"
                        required
                      />
                    </div>
                    <div>
                      <Label htmlFor="telefono" className="text-sm font-medium text-primary">
                        Teléfono
                      </Label>
                      <Input 
                        id="telefono" 
                        type="tel"
                        placeholder="+52 55 1234 5678"
                        className="mt-1"
                      />
                    </div>
                  </div>

                  <div>
                    <Label htmlFor="asunto" className="text-sm font-medium text-primary">
                      Área de Consulta
                    </Label>
                    <select 
                      id="asunto"
                      className="w-full mt-1 px-3 py-2 border border-border rounded-md bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-accent focus:border-transparent"
                    >
                      <option value="">Seleccione un área</option>
                      <option value="corporativo">Derecho Corporativo</option>
                      <option value="litigio">Litigio y Arbitraje</option>
                      <option value="propiedad-intelectual">Propiedad Intelectual</option>
                      <option value="fiscal">Derecho Fiscal</option>
                      <option value="laboral">Derecho Laboral</option>
                      <option value="penal">Derecho Penal Empresarial</option>
                      <option value="financiero">Derecho Financiero</option>
                      <option value="competencia">Derecho de la Competencia</option>
                      <option value="inmobiliario">Derecho Inmobiliario</option>
                      <option value="otro">Otro</option>
                    </select>
                  </div>

                  <div>
                    <Label htmlFor="mensaje" className="text-sm font-medium text-primary">
                      Mensaje *
                    </Label>
                    <Textarea 
                      id="mensaje" 
                      placeholder="Describa brevemente su consulta o caso..."
                      className="mt-1 min-h-[120px]"
                      required
                    />
                  </div>

                  <div className="text-sm text-muted-foreground">
                    <p>
                      Al enviar este formulario, acepta nuestros términos de privacidad 
                      y el manejo confidencial de su información.
                    </p>
                  </div>

                  <Button type="submit" className="btn-hero w-full">
                    <Send className="w-4 h-4 mr-2" />
                    Enviar Mensaje
                  </Button>
                </form>
              </CardContent>
            </Card>

            {/* Map and Additional Info */}
            <div className="space-y-6">
              {/* Map Placeholder */}
              <Card>
                <CardContent className="p-0">
                  <div className="aspect-video bg-muted rounded-lg flex items-center justify-center">
                    <div className="text-center">
                      <MapPin className="w-12 h-12 text-accent mx-auto mb-2" />
                      <p className="text-muted-foreground">
                        Mapa de Google Maps
                      </p>
                      <p className="text-sm text-muted-foreground mt-1">
                        Av. Reforma 123, Piso 15, Col. Juárez, CDMX
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>

              {/* Office Information */}
              <Card>
                <CardContent className="p-6">
                  <h3 className="text-xl font-merriweather font-semibold text-primary mb-4">
                    Información de la Oficina
                  </h3>
                  <div className="space-y-4">
                    <div>
                      <h4 className="font-semibold text-primary mb-2">Ubicación Estratégica</h4>
                      <p className="text-sm text-muted-foreground">
                        Nuestra oficina se encuentra en el corazón del distrito financiero 
                        de la Ciudad de México, con fácil acceso en transporte público y 
                        estacionamiento disponible.
                      </p>
                    </div>
                    
                    <div>
                      <h4 className="font-semibold text-primary mb-2">Facilidades</h4>
                      <ul className="text-sm text-muted-foreground space-y-1">
                        <li>• Salas de reuniones ejecutivas</li>
                        <li>• Acceso para personas con discapacidad</li>
                        <li>• Estacionamiento gratuito para clientes</li>
                        <li>• Recepción multilingüe</li>
                      </ul>
                    </div>

                    <div>
                      <h4 className="font-semibold text-primary mb-2">Consultas de Emergencia</h4>
                      <p className="text-sm text-muted-foreground">
                        Ofrecemos servicios de consulta de emergencia 24/7 para 
                        situaciones urgentes que requieren atención legal inmediata.
                      </p>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="section-padding bg-muted/30">
        <div className="container-custom text-center">
          <h2 className="text-3xl font-merriweather font-bold text-primary mb-4">
            ¿Prefiere una Consulta Telefónica?
          </h2>
          <p className="text-lg text-muted-foreground mb-8 max-w-2xl mx-auto">
            Llámenos directamente para agendar una consulta inicial gratuita 
            de 30 minutos con uno de nuestros especialistas.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button className="btn-hero">
              <Phone className="w-4 h-4 mr-2" />
              +52 55 1234 5678
            </Button>
            <Button className="btn-outline-gold">
              Solicitar Videollamada
            </Button>
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
}
