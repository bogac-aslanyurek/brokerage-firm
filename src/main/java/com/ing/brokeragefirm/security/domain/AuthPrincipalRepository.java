package com.ing.brokeragefirm.security.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthPrincipalRepository extends JpaRepository<AuthPrincipal, Long> {

    AuthPrincipal findByUsername(String username);
}