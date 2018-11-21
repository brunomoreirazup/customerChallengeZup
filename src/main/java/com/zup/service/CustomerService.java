package com.zup.service;

import com.zup.model.City;
import com.zup.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface CustomerService {

    Page<Customer> findAll(Pageable pageable);

    Page<Customer> findByName(Pageable pageable, String name);

    Customer findById(Long id);

    Customer create(Customer customer);

    Customer update(Customer customer);

    void delete(Long id);
}
