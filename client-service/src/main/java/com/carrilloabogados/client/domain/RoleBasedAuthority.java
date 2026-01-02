package com.carrilloabogados.client.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Roles del sistema Carrillo Abogados Legal Tech.
 * 
 * Jerarquía de permisos:
 * - VISITOR: Sin autenticación, acceso público (formulario contacto, info
 * general)
 * - CLIENT: Cliente del bufete, accede a portal cliente (sus casos, documentos)
 * - LAWYER: Abogado @carrilloabgd.com, gestiona casos asignados
 * - ADMIN: Administrador, acceso total al sistema
 */
@RequiredArgsConstructor
@Getter
public enum RoleBasedAuthority {

    /**
     * Visitante - Sin autenticación requerida
     * Acceso: Portal público, formulario contacto, información general
     */
    ROLE_VISITOR("VISITOR"),

    /**
     * Cliente del bufete - Autenticación por email
     * Acceso: Ver sus casos, subir documentos, mensajes con abogado
     */
    ROLE_CLIENT("CLIENT"),

    /**
     * Abogado - Autenticación OAuth2 @carrilloabgd.com
     * Acceso: Gestionar casos asignados, clientes, calendario
     */
    ROLE_LAWYER("LAWYER"),

    /**
     * Administrador - Autenticación OAuth2 @carrilloabgd.com
     * Acceso: Total - configuración, usuarios, contenido, workflows
     */
    ROLE_ADMIN("ADMIN");

    private final String role;

    /**
     * Verifica si el rol tiene permisos de staff (abogado o admin)
     */
    public boolean isStaff() {
        return this == ROLE_LAWYER || this == ROLE_ADMIN;
    }

    /**
     * Verifica si el rol requiere autenticación
     */
    public boolean requiresAuthentication() {
        return this != ROLE_VISITOR;
    }

    /**
     * Verifica si el rol requiere dominio @carrilloabgd.com
     */
    public boolean requiresCompanyDomain() {
        return this == ROLE_LAWYER || this == ROLE_ADMIN;
    }
}
