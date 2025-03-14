package com.ing.brokeragefirm.customer.api;

import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.domain.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    @PostMapping("/create")
    public void createCustomer(@RequestBody CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setUsername(request.username());
        customer.setPassword(request.password());
        customerRepository.save(customer);
    }

    @GetMapping("/list")
    public void listCustomers() {
        customerRepository.findAll();
    }

}
