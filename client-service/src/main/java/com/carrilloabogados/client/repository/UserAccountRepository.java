package com.carrilloabogados.client.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carrilloabogados.client.domain.RoleBasedAuthority;
import com.carrilloabogados.client.domain.UserAccount;
import com.carrilloabogados.client.domain.UserAccount.AuthProvider;

/**
 * Repository para UserAccount.
 */
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    // ========== Búsquedas básicas ==========

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    // ========== Por proveedor de autenticación ==========

    Optional<UserAccount> findByProviderIdAndAuthProvider(String providerId, AuthProvider authProvider);

    List<UserAccount> findByAuthProvider(AuthProvider authProvider);

    // ========== Por rol ==========

    List<UserAccount> findByRole(RoleBasedAuthority role);

    Page<UserAccount> findByRole(RoleBasedAuthority role, Pageable pageable);

    @Query("SELECT u FROM UserAccount u WHERE u.role IN :roles")
    List<UserAccount> findByRoleIn(@Param("roles") List<RoleBasedAuthority> roles);

    // ========== Staff (abogados y admins) ==========

    @Query("SELECT u FROM UserAccount u WHERE u.role IN ('ROLE_LAWYER', 'ROLE_ADMIN') AND u.isActive = true")
    List<UserAccount> findActiveStaff();

    @Query("SELECT u FROM UserAccount u WHERE u.role = 'ROLE_LAWYER' AND u.isActive = true")
    List<UserAccount> findActiveLawyers();

    @Query("SELECT u FROM UserAccount u WHERE u.role = 'ROLE_ADMIN' AND u.isActive = true")
    List<UserAccount> findActiveAdmins();

    // ========== Clientes ==========

    @Query("SELECT u FROM UserAccount u WHERE u.role = 'ROLE_CLIENT' AND u.isActive = true")
    List<UserAccount> findActiveClients();

    @Query("SELECT u FROM UserAccount u WHERE u.role = 'ROLE_CLIENT' AND u.isActive = true")
    Page<UserAccount> findActiveClients(Pageable pageable);

    // ========== Por estado ==========

    List<UserAccount> findByIsActiveTrue();

    List<UserAccount> findByIsActiveFalse();

    List<UserAccount> findByIsEmailVerifiedFalse();

    // ========== Búsquedas por dominio ==========

    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.email) LIKE LOWER(CONCAT('%@', :domain))")
    List<UserAccount> findByEmailDomain(@Param("domain") String domain);

    /**
     * Busca usuarios del dominio @carrilloabgd.com
     */
    @Query("SELECT u FROM UserAccount u WHERE LOWER(u.email) LIKE '%@carrilloabgd.com'")
    List<UserAccount> findCompanyUsers();

    // ========== Búsqueda por texto ==========

    @Query("SELECT u FROM UserAccount u WHERE " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<UserAccount> searchByText(@Param("search") String search, Pageable pageable);

    // ========== Estadísticas ==========

    @Query("SELECT u.role, COUNT(u) FROM UserAccount u WHERE u.isActive = true GROUP BY u.role")
    List<Object[]> countByRole();

    @Query("SELECT COUNT(u) FROM UserAccount u WHERE u.role = :role AND u.isActive = true")
    long countActiveByRole(@Param("role") RoleBasedAuthority role);
}
