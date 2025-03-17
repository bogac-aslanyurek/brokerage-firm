package com.ing.brokeragefirm.security.service;

import com.ing.brokeragefirm.security.domain.AuthPrincipal;
import com.ing.brokeragefirm.security.domain.AuthPrincipalRepository;
import com.ing.brokeragefirm.security.domain.AuthRealm;
import com.ing.brokeragefirm.security.model.AuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public Boolean aclCheck(String realmObjectId) {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication().getPrincipal() instanceof AuthenticationToken) {
            final AuthenticationToken token = (AuthenticationToken) context.getAuthentication().getPrincipal();
            if (token == null) {
                return Boolean.FALSE;
            }
            if (AuthRealm.ADMIN.equals(token.getRealm())) {
                return Boolean.TRUE;
            }

            if (token.getRealmObjectId() == null) {
                return Boolean.FALSE;
            }

            return realmObjectId.equals(token.getRealmObjectId());
        }
        return Boolean.FALSE;
    }
}
