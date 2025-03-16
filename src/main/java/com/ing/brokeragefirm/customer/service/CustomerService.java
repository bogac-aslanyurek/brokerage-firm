package com.ing.brokeragefirm.customer.service;

import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.domain.CustomerRepository;
import com.ing.brokeragefirm.exception.ApiException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public Customer getCustomer(Long customerId) {
        if (customerId == null) {
            throw new ApiException(1000, "Customer not found");
        }
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ApiException(1000, "Customer not found"));
    }

    public Customer createCustomer(String name) {
        Customer customer = new Customer();
        customer.setName(name);
        return customerRepository.save(customer);
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }
}
