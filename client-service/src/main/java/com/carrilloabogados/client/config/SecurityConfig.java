package com.carrilloabogados.client.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.carrilloabogados.client.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * Configuración de Spring Security para client-service.
 * 
 * Endpoints públicos:
 * - /api/auth/** (registro, login)
 * - POST /api/leads (formulario de contacto)
 * - /actuator/** (health checks)
 * - /swagger-ui/**, /v3/api-docs/** (documentación)
 * 
 * Endpoints protegidos:
 * - /api/clients/** (ROLE_CLIENT, ROLE_LAWYER, ROLE_ADMIN)
 * - /api/users/** (ROLE_ADMIN)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF para API REST stateless
                .csrf(csrf -> csrf.disable())

                // Configuración CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Sesiones stateless (JWT)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuración de autorización
                .authorizeHttpRequests(auth -> auth
                        // ===== ENDPOINTS PÚBLICOS =====

                        // Autenticación (registro, login, etc.)
                        .requestMatchers("/api/auth/**").permitAll()

                        // Formulario de contacto (captura de leads)
                        .requestMatchers(HttpMethod.POST, "/api/leads").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/leads/contact").permitAll()

                        // Health checks y actuator
                        .requestMatchers("/actuator/**").permitAll()

                        // Swagger/OpenAPI docs
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/api-docs/**").permitAll()

                        // Error endpoints
                        .requestMatchers("/error").permitAll()

                        // ===== ENDPOINTS PROTEGIDOS POR ROL =====

                        // Leads: Solo staff puede ver/gestionar leads
                        .requestMatchers(HttpMethod.GET, "/api/leads/**").hasAnyAuthority("ROLE_LAWYER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/leads/**").hasAnyAuthority("ROLE_LAWYER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/leads/**").hasAuthority("ROLE_ADMIN")

                        // Clientes: Acceso diferenciado
                        // - Clientes pueden ver su propia información
                        // - Abogados pueden ver/editar clientes
                        // - Admins tienen acceso completo
                        .requestMatchers(HttpMethod.GET, "/api/clients/me")
                        .hasAnyAuthority("ROLE_CLIENT", "ROLE_LAWYER", "ROLE_ADMIN")
                        .requestMatchers("/api/clients/**").hasAnyAuthority("ROLE_LAWYER", "ROLE_ADMIN")

                        // Gestión de usuarios: Solo admin
                        .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                        // Por defecto, requiere autenticación
                        .anyRequest().authenticated())

                // Agregar filtro JWT antes del filtro de autenticación
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Cost factor 12 para mayor seguridad
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Orígenes permitidos (frontend Next.js)
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"));

        // Exponer headers en respuesta
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"));

        // Permitir credenciales (cookies, auth headers)
        configuration.setAllowCredentials(true);

        // Tiempo de caché para preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
