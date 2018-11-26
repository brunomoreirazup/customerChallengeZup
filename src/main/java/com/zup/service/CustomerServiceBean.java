package com.zup.service;

import com.zup.model.City;
import com.zup.model.Customer;
import com.zup.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceBean implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CityServiceBean cityService;

    @Override
    public Page<Customer> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Page<Customer> findByName(Pageable pageable, String name) {
        return customerRepository.findByNameContainingIgnoreCase(pageable, name);
    }

    @Override
    public Customer findById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer create(Customer customer) {
        if(customer.getId() != null){
            throw new EntityExistsException("The id attribute must be null to persist a new entity");
        }

        City city = cityService.findById(customer.getCity().getId());
        if(city == null){
            throw new NoResultException("Requested entity not found");
        }

        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public Customer update(Customer customer) {
        Customer responseCustomer = findById(customer.getId());

        if( responseCustomer == null){
            throw new NoResultException("Requested entity not found");
        }

        if(customer.getName() != null && customer.getName()!=""){
            responseCustomer.setName(customer.getName());
        }

        if(customer.getCity() != null){
            responseCustomer.setCity(customer.getCity());
        }

        responseCustomer = customerRepository.saveAndFlush(responseCustomer);
        return responseCustomer;

    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
