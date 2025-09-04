import { Link } from "react-router-dom";
import { Scale, Phone, Mail, MapPin, Facebook, Twitter, Linkedin } from "lucide-react";

const Footer = () => {
  const quickLinks = [
    { name: "Inicio", href: "/" },
    { name: "Áreas de Práctica", href: "/areas-de-practica" },
    { name: "Nuestro Equipo", href: "/nuestro-equipo" },
    { name: "Contacto", href: "/contacto" },
  ];

  const socialLinks = [
    { icon: Facebook, href: "#", label: "Facebook" },
    { icon: Twitter, href: "#", label: "Twitter" },
    { icon: Linkedin, href: "#", label: "LinkedIn" },
  ];

  return (
    <footer className="bg-primary text-primary-foreground">
      <div className="container-custom py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Logo and Mission */}
          <div className="lg:col-span-2">
            <Link to="/" className="flex items-center space-x-2 mb-4">
              <div className="w-8 h-8 bg-accent rounded-lg flex items-center justify-center">
                <Scale className="w-5 h-5 text-accent-foreground" />
              </div>
              <div>
                <h3 className="text-xl font-merriweather font-bold">
                  Carrillo
                </h3>
                <p className="text-xs font-lato text-primary-foreground/80 -mt-1">
                  ABOGADOS
                </p>
              </div>
            </Link>
            <p className="text-sm text-primary-foreground/80 leading-relaxed max-w-md">
              Con más de 20 años de experiencia, ofrecemos asesoría legal estratégica 
              y soluciones integrales para empresas e individuos. Nuestro compromiso 
              es brindar resultados excepcionales con la más alta confidencialidad.
            </p>
            
            {/* Social Media */}
            <div className="flex space-x-4 mt-6">
              {socialLinks.map((social) => (
                <a
                  key={social.label}
                  href={social.href}
                  className="w-10 h-10 bg-primary-foreground/10 rounded-lg flex items-center justify-center hover:bg-accent transition-colors duration-300"
                  aria-label={social.label}
                >
                  <social.icon className="w-5 h-5" />
                </a>
              ))}
            </div>
          </div>

          {/* Contact Info */}
          <div>
            <h4 className="text-lg font-merriweather font-semibold mb-4">
              Contacto
            </h4>
            <div className="space-y-3">
              <div className="flex items-start space-x-3">
                <MapPin className="w-5 h-5 text-accent mt-0.5" />
                <div className="text-sm">
                  <p>Av. Reforma 123, Piso 15</p>
                  <p>Col. Juárez, CDMX 06600</p>
                </div>
              </div>
              <div className="flex items-center space-x-3">
                <Phone className="w-5 h-5 text-accent" />
                <p className="text-sm">+52 55 1234 5678</p>
              </div>
              <div className="flex items-center space-x-3">
                <Mail className="w-5 h-5 text-accent" />
                <p className="text-sm">info@carrillo-abogados.com</p>
              </div>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="text-lg font-merriweather font-semibold mb-4">
              Enlaces Rápidos
            </h4>
            <nav className="space-y-2">
              {quickLinks.map((link) => (
                <Link
                  key={link.name}
                  to={link.href}
                  className="block text-sm text-primary-foreground/80 hover:text-accent transition-colors duration-300"
                >
                  {link.name}
                </Link>
              ))}
            </nav>
          </div>
        </div>

        {/* Bottom Border */}
        <div className="border-t border-primary-foreground/20 mt-8 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center">
            <p className="text-sm text-primary-foreground/60">
              © 2024 Carrillo Abogados. Todos los derechos reservados.
            </p>
            <div className="flex space-x-6 mt-4 md:mt-0">
              <a href="#" className="text-sm text-primary-foreground/60 hover:text-accent transition-colors duration-300">
                Política de Privacidad
              </a>
              <a href="#" className="text-sm text-primary-foreground/60 hover:text-accent transition-colors duration-300">
                Términos de Servicio
              </a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;