package com.zup.service;

import com.zup.model.City;
import com.zup.model.Customer;
import com.zup.repository.CityRepository;
import com.zup.repository.CustomerRepository;
import com.zup.service.CityServiceBean;
import com.zup.service.CustomerService;
import com.zup.service.CustomerServiceBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CityServiceBean cityService;

    @InjectMocks
    private CustomerServiceBean customerService;

    private Collection<Customer> customerList = new ArrayList();
    private Customer customer1;
    private Customer customer2;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        customer1 = new Customer("Joaozim Fejao", new City("Uberlandia"));
        customer2 = new Customer("Joao Capim", new City("Uberaba"));

        customerList.add(customer1);
        customerList.add(customer2);

    }

    @Test
    public void testFindAll(){

        PageRequest pageRequest = new PageRequest(0,20);
        Page<Customer> paginatedCustomer = new PageImpl<Customer>((List<Customer>) customerList);


        when(customerRepository.findAll(pageRequest)).thenReturn(paginatedCustomer);

        Page<Customer> customers = customerService.findAll(pageRequest);

        Assert.assertNotNull("failure - expected not null", customers);
        Assert.assertEquals("failure - expected size", 2, customers.getNumberOfElements());

        verify(customerRepository, atMost(1)).findAll();


    }

    @Test
    public void testFindByName(){
        PageRequest pageRequest = new PageRequest(0,20);
        Page<Customer> paginatedCustomer = new PageImpl<Customer>((List<Customer>) customerList);

        String name = "Joao";
        when(customerRepository.findByNameContainingIgnoreCase(pageRequest, name)).thenReturn(paginatedCustomer);

        Page<Customer> customers = customerService.findByName(pageRequest, name);

        Assert.assertNotNull("failure - expected not null", customers);
        Assert.assertEquals("failure - expected size", 2, customers.getNumberOfElements());

        verify(customerRepository, atMost(1)).findByNameContainingIgnoreCase(pageRequest,name);
    }

    @Test
    public void testFindByNameNotFound(){
        Collection<Customer> emptyCustomerList = new ArrayList();

        PageRequest pageRequest = new PageRequest(0,20);
        Page<Customer> paginatedCustomer = new PageImpl<Customer>((List<Customer>) emptyCustomerList);

        String name = "OAEHUHAEOAHEU";
        when(customerRepository.findByNameContainingIgnoreCase(pageRequest, name)).thenReturn(paginatedCustomer);

        Page<Customer> customers = customerService.findByName(pageRequest, name);

        Assert.assertNotNull("failure - expected not null", customers);
        Assert.assertEquals("failure - expected size", 0, customers.getNumberOfElements());

        verify(customerRepository, atMost(1)).findByNameContainingIgnoreCase(pageRequest,name);

    }

    @Test
    public void testFindOne(){
        Long id = this.customer1.getId();
        when(customerRepository.findById(id)).thenReturn(Optional.of(this.customer1));

        Customer responseCustomer = customerService.findById(id);

        Assert.assertNotNull("failure - expected not null", responseCustomer);
        Assert.assertEquals("failure - expected size", this.customer1.getId(), responseCustomer.getId());

        verify(customerRepository, atMost(1)).findById(id);

    }

    @Test
    public void testFindOneNotFound(){
        Long id = Long.MAX_VALUE;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        Customer responseCustomer = customerService.findById(id);

        Assert.assertNull("failure - expected null", responseCustomer);

        verify(customerRepository, atMost(1)).findById(id);
    }

    @Test
    public void testCreate(){
        City newCity = new City("Araraquara");
        newCity.setId(16L);

        Customer customer = new Customer("Peh de Moleque",newCity);

        when(cityService.findById(notNull())).thenReturn(newCity);
        when(customerRepository.saveAndFlush(customer)).thenReturn(customer);

        Customer responseCustomer = customerService.create(customer);

        Assert.assertNotNull("failure - expected not null", responseCustomer);
        Assert.assertEquals("failure - expected customer id", customer.getId(), responseCustomer.getId());
        Assert.assertEquals("failure - expected customer name", customer.getName(), responseCustomer.getName());
        Assert.assertEquals("failure - expected customer city name", customer.getCity().getName(),
                responseCustomer.getCity().getName());

        verify(customerRepository, atMost(1)).saveAndFlush(customer);

    }

    @Test(expected = EntityExistsException.class)
    public void testCreateCustomerWithIdNotNull(){
        Customer customer = new Customer("Merlin", new City("Araxa"));
        customer.setId(100L);

        customerService.create(customer);
    }

    @Test(expected = NoResultException.class)
    public void testCreateCustomerWithNonExistingCity(){
        City newCity = new City("Pirapora");
        newCity.setId(Long.MAX_VALUE);
        Customer customer = new Customer("Grogg", newCity);

        customerService.create(customer);

    }


    @Test
    public void testUpdateCustomerName(){
        String newName = "Pedro Bial";
        this.customer1.setName(newName);

        when(customerRepository.findById(this.customer1.getId())).thenReturn(Optional.of(this.customer1));
        when(customerRepository.saveAndFlush(this.customer1)).thenReturn(this.customer1);

        Customer responseCustomer = customerService.update(this.customer1);
        Assert.assertNotNull("failure - expected not null",  responseCustomer);
        Assert.assertEquals("failure - expected customer id", this.customer1.getId(), responseCustomer.getId());
        Assert.assertEquals("failure - expected customer updated name", newName, responseCustomer.getName());

        verify(customerRepository, atMost(1)).findById(this.customer1.getId());
        verify(customerRepository, atMost(1)).saveAndFlush(this.customer1);

    }

    @Test
    public void testUpdateCustomerCity(){
        City cityToUpdate = new City("Beija Flor");
        this.customer1.setCity(cityToUpdate);

        when(customerRepository.findById(this.customer1.getId())).thenReturn(Optional.of(this.customer1));
        when(customerRepository.saveAndFlush(this.customer1)).thenReturn(this.customer1);

        Customer responseCustomer = customerService.update(this.customer1);

        Assert.assertNotNull("failure - expected not null",  responseCustomer);
        Assert.assertEquals("failure - expected customer id", this.customer1.getId(), responseCustomer.getId());
        Assert.assertEquals("failure - expected customer updated name",
                cityToUpdate.getName(), responseCustomer.getCity().getName());

        verify(customerRepository, atMost(1)).findById(this.customer1.getId());
        verify(customerRepository, atMost(1)).saveAndFlush(this.customer1);

    }

    @Test(expected = NoResultException.class)
    public void testUpdateNonExistingCustomer(){
        this.customer1.setId(Long.MAX_VALUE);

        customerService.update(this.customer1);

    }

    @Test
    public void testDelete(){

        customerRepository.deleteById(this.customer1.getId());
        verify(customerRepository, atMost(1)).deleteById(this.customer1.getId());
    }
}
