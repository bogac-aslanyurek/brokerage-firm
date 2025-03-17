package com.ing.brokeragefirm.security.service;

import com.ing.brokeragefirm.security.domain.AuthPrincipal;
import com.ing.brokeragefirm.security.domain.AuthPrincipalRepository;
import com.ing.brokeragefirm.security.domain.AuthRealm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SecurityService {

    private final PasswordEncoder encoder;
    private final AuthPrincipalRepository authPrincipalRepository;

    public AuthPrincipal createPrincipal(String realmObjectId, String username, String password) {

        AuthPrincipal principal = new AuthPrincipal();
        principal.setRealmObjectId((realmObjectId));
        principal.setRealm(AuthRealm.USER);
        principal.setUsername(username);
        principal.setPassword(encoder.encode(password));
        return authPrincipalRepository.save(principal);

    }
}
