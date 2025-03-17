package com.ing.brokeragefirm.customer.api;

import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.model.CreateCustomerRequest;
import com.ing.brokeragefirm.customer.model.CustomerDto;
import com.ing.brokeragefirm.customer.service.CustomerService;
import com.ing.brokeragefirm.security.domain.AuthPrincipal;
import com.ing.brokeragefirm.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final SecurityService securityService;
    private final CustomerService customerService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CreateCustomerRequest request) {
        Customer customer = customerService.createCustomer(request.name());
        final AuthPrincipal principal = securityService.createPrincipal(String.valueOf(customer.getId()), request.username(), request.password());
        return ResponseEntity.ok(new CustomerDto(customer.getId(), customer.getName(), principal.getUsername()));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Customer> listCustomers() {
        return customerService.list();
    }

}
