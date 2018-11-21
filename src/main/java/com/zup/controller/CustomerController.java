package com.zup.controller;

import com.zup.model.Customer;
import com.zup.service.CustomerService;
import com.zup.service.CustomerServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;

@RestController
public class CustomerController {

    @Autowired
    CustomerServiceBean customerService;

    @GetMapping("/customers")
    public Page<Customer> getCustomers(Pageable pageable){
        return customerService.findAll(pageable);
    }

    @GetMapping("/customers/search")
    public Page<Customer> searchCustomer(Pageable pageable, @RequestParam(value = "name") String name){
        return customerService.findByName(pageable, name);
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable Long id){
        return customerService.findById(id);
    }


    @PostMapping("/customers")
    public Customer postCostumer(@RequestBody Customer customer){
        return customerService.create(customer);
    }

    @PutMapping("/customers/{id}")
    public Customer putCustomer(@PathVariable Long id, @RequestBody Customer customer){
        customer.setId(id);
        return customerService.update(customer);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable Long id){
        customerService.delete(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    String exceptionHandler(ValidationException e){
        return e.getMessage();
    }
}
