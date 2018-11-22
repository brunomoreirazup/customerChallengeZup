package com.zup.controller;

import com.zup.model.CustomPage;
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
    public CustomPage getCustomers(Pageable pageable){

        CustomPage customPage = new CustomPage(customerService.findAll(pageable), "customers");
        return customPage;
    }

    @GetMapping("/customers/search")
    public CustomPage searchCustomer(Pageable pageable, @RequestParam(value = "name") String name){
        CustomPage customPage = new CustomPage(customerService.findByName(pageable, name), "customers");
        return customPage;
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable Long id){
        return customerService.findById(id);
    }


    @PostMapping("/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public Customer postCostumer(@RequestBody Customer customer){
        return customerService.create(customer);
    }

    @PutMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Customer putCustomer(@PathVariable Long id, @RequestBody Customer customer){
        customer.setId(id);
        return customerService.update(customer);
    }

    @DeleteMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id){
        customerService.delete(id);
    }

}
