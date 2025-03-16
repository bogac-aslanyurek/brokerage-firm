package com.ing.brokeragefirm.security.service;

import com.ing.brokeragefirm.security.domain.AuthPrincipal;
import com.ing.brokeragefirm.security.domain.AuthPrincipalRepository;
import com.ing.brokeragefirm.security.model.AuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthPrincipalRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final AuthPrincipal principal = repository.findByUsername(username);

        if (principal != null) {
            AuthenticationToken token = new AuthenticationToken();
            token.setUsername(principal.getUsername());
            token.setPassword(principal.getPassword());
            token.setRealmObjectId(principal.getRealmObjectId());
            token.setGrantedAuthority(principal.getRealm().name());
            return token;
        }
        return null;
    }
}
