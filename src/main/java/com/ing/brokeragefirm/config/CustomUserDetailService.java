package com.ing.brokeragefirm.config;

import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final CustomerRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Customer customer = repository.findByUsername(username);

        if (customer == null) {
            throw new UsernameNotFoundException("No users found with that username");
        }

        return customer;
    }
}
