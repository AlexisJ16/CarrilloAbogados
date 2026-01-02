package com.carrilloabogados.client.helper;

import com.carrilloabogados.client.domain.UserAccount;
import com.carrilloabogados.client.dto.UserAccountDto;
import com.carrilloabogados.client.dto.auth.RegisterRequest;

/**
 * Helper para mapear entre UserAccount entity y DTOs.
 */
public interface UserAccountMappingHelper {

    /**
     * Mapea de UserAccount entity a UserAccountDto.
     */
    static UserAccountDto map(final UserAccount account) {
        if (account == null) {
            return null;
        }

        return UserAccountDto.builder()
                .accountId(account.getAccountId())
                // Datos básicos
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .avatarUrl(account.getAvatarUrl())
                .phone(account.getPhone())
                // Rol y estado
                .role(account.getRole())
                .isActive(account.getIsActive())
                .isEmailVerified(account.getIsEmailVerified())
                .authProvider(account.getAuthProvider())
                // Campos calculados
                .fullName(account.getFullName())
                .isStaff(account.isStaff())
                // Timestamps
                .lastLoginAt(account.getLastLoginAt())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    /**
     * Mapea de UserAccountDto a UserAccount entity.
     * No incluye password ni campos sensibles.
     */
    static UserAccount map(final UserAccountDto dto) {
        if (dto == null) {
            return null;
        }

        return UserAccount.builder()
                .accountId(dto.getAccountId())
                // Datos básicos
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .avatarUrl(dto.getAvatarUrl())
                .phone(dto.getPhone())
                // Rol y estado
                .role(dto.getRole())
                .isActive(dto.getIsActive())
                .isEmailVerified(dto.getIsEmailVerified())
                .authProvider(dto.getAuthProvider())
                .build();
    }

    /**
     * Mapea de RegisterRequest a UserAccount entity.
     * Para crear nuevas cuentas de clientes.
     */
    static UserAccount mapFromRegister(final RegisterRequest request) {
        if (request == null) {
            return null;
        }

        return UserAccount.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                // Defaults para nuevos clientes
                .role(com.carrilloabogados.client.domain.RoleBasedAuthority.ROLE_CLIENT)
                .isActive(true)
                .isEmailVerified(false)
                .authProvider(UserAccount.AuthProvider.LOCAL)
                .build();
    }

    /**
     * Actualiza un UserAccount existente con datos del DTO.
     * Solo actualiza campos permitidos.
     */
    static void updateEntity(final UserAccount account, final UserAccountDto dto) {
        if (account == null || dto == null) {
            return;
        }

        if (dto.getFirstName() != null) {
            account.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            account.setLastName(dto.getLastName());
        }
        if (dto.getAvatarUrl() != null) {
            account.setAvatarUrl(dto.getAvatarUrl());
        }
        if (dto.getPhone() != null) {
            account.setPhone(dto.getPhone());
        }
        // Email y rol no se actualizan por este método (requieren flujo especial)
    }
}
