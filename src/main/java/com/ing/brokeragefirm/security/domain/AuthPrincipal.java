package com.ing.brokeragefirm.security.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "T_AUTH_PRINCIPAL")
@Getter
@Setter
public class AuthPrincipal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "REALM_OBJECT_ID")
    private String realmObjectId;

    @Column( name = "REALM")
    @Enumerated(EnumType.STRING)
    private AuthRealm realm;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;
}
