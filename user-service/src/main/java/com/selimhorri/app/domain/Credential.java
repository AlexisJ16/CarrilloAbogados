package com.selimhorri.app.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "credentials")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"user", "verificationTokens"})
@Data
@Builder
public final class Credential extends AbstractMappedEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "credential_id", unique = true, nullable = false, updatable = false)
	private Integer credentialId;
	
	@Column(unique = true)
	private String username;
	
	private String password;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private RoleBasedAuthority roleBasedAuthority;
	
	@Column(name = "is_enabled")
	private Boolean isEnabled;
	
	@Column(name = "is_account_non_expired")
	private Boolean isAccountNonExpired;
	
	@Column(name = "is_account_non_locked")
	private Boolean isAccountNonLocked;
	
	@Column(name = "is_credentials_non_expired")
	private Boolean isCredentialsNonExpired;
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "credential")
	private Set<VerificationToken> verificationTokens;
	
}







