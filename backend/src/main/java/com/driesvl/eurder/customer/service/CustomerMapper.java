package com.driesvl.eurder.customer.service;

import com.driesvl.eurder.authorization.repository.domain.User;
import com.driesvl.eurder.customer.api.dto.AddressDTO;
import com.driesvl.eurder.customer.api.dto.CreateCustomerDTO;
import com.driesvl.eurder.customer.api.dto.CustomerDTO;
import com.driesvl.eurder.customer.api.dto.NameDTO;
import com.driesvl.eurder.customer.repository.domain.Address;
import com.driesvl.eurder.customer.repository.domain.Customer;
import com.driesvl.eurder.customer.repository.domain.Name;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {
    public CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(customer.getUser().getId().toString(),
                customer.getUser().getEmail(),
                toDTO(customer.getName()),
                customer.getPhoneNumber(),
                toDTO(customer.getAddress()));
    }

    public List<CustomerDTO> toDTO(List<Customer> customers) {
        return customers.stream().map(this::toDTO).toList();
    }

    public Customer fromCreateDTOAndUser(CreateCustomerDTO customer, User user) {
        return new Customer(user,
                new Name(customer.firstName(), customer.lastName()),
                customer.phoneNumber(),
                fromDTO(customer.address()));
    }

    private AddressDTO toDTO(Address address) {
        return new AddressDTO(address.getStreet(), address.getStreetNumber(), address.getCity());
    }

    private Address fromDTO(AddressDTO addressDTO) {
        return new Address(addressDTO.street(), addressDTO.streetNumber(), addressDTO.city());
    }

    private NameDTO toDTO(Name name) {
        return new NameDTO(name.getFirstName(), name.getLastName());
    }

}
