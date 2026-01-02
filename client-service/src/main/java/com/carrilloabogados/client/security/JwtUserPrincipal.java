package com.carrilloabogados.client.security;

import java.util.UUID;

import com.carrilloabogados.client.domain.RoleBasedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Principal personalizado que contiene información del usuario autenticado
 * extraída del token JWT.
 */
@Getter
@AllArgsConstructor
public class JwtUserPrincipal {

    private final UUID accountId;
    private final String email;
    private final RoleBasedAuthority role;

    /**
     * @return true si el usuario es staff (abogado o admin)
     */
    public boolean isStaff() {
        return role.isStaff();
    }

    /**
     * @return true si el usuario es administrador
     */
    public boolean isAdmin() {
        return role == RoleBasedAuthority.ROLE_ADMIN;
    }

    /**
     * @return true si el usuario es abogado
     */
    public boolean isLawyer() {
        return role == RoleBasedAuthority.ROLE_LAWYER;
    }

    /**
     * @return true si el usuario es cliente
     */
    public boolean isClient() {
        return role == RoleBasedAuthority.ROLE_CLIENT;
    }

    @Override
    public String toString() {
        return "JwtUserPrincipal{accountId=" + accountId + ", email='" + email + "', role=" + role + "}";
    }
}
