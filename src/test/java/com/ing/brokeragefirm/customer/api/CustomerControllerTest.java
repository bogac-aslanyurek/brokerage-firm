package com.ing.brokeragefirm.customer.api;

import com.ing.brokeragefirm.customer.domain.Customer;
import com.ing.brokeragefirm.customer.model.CreateCustomerRequest;
import com.ing.brokeragefirm.customer.model.CustomerDto;
import com.ing.brokeragefirm.customer.service.CustomerService;
import com.ing.brokeragefirm.security.domain.AuthPrincipal;
import com.ing.brokeragefirm.security.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ing.brokeragefirm.security.domain.AuthRealm.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private SecurityService securityService;
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private CustomerController controller;

    @Test
    public void createCustomer_success() {
        CreateCustomerRequest request = new CreateCustomerRequest("ACME", "acme", "password");

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setId(1L);

        AuthPrincipal principal = new AuthPrincipal();
        principal.setId(1L);
        principal.setUsername("acme");
        principal.setPassword("password");
        principal.setRealm(USER);
        principal.setRealmObjectId("1");

        when(customerService.createCustomer(request.name())).thenReturn(customer);
        when(securityService.createPrincipal("1", request.username(), request.password())).thenReturn(principal);

        final CustomerDto customerDto = controller.createCustomer(request).getBody();

        assertThat(customerDto.id()).isEqualTo(1L);
        assertThat(customerDto.name()).isEqualTo("ACME");
        assertThat(customerDto.username()).isEqualTo("acme");
    }

}