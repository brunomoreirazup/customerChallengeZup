package com.zup.controller;

import com.zup.model.City;
import com.zup.model.Customer;
import com.zup.service.CustomerService;
import com.zup.service.CustomerServiceBean;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.persistence.NoResultException;
import javax.print.attribute.standard.Media;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @MockBean
    CustomerServiceBean customerService;

    @Autowired
    MockMvc mockMvc;

    private static final String PATH = "/customers";

    private Collection<Customer> customersList;

    private Customer customer1;
    private Customer customer2;
    private Customer customer3;

    private Collection<City> citiesList;
    private City city1;
    private City city2;

    @Before
    public void init(){
        this.citiesList = new ArrayList();
        this.city1 = new City("Uberaba");
        this.city2 = new City("Uberlandia");

        this.citiesList.add(city1);
        this.citiesList.add(city2);

        this.customersList = new ArrayList();
        this.customer1 = new Customer("Serginho Caneca", this.city1);
        this.customer2 = new Customer("Serginho Groisman", this.city2);
        this.customer3 = new Customer("Serginho Namorado", this.city1);

        this.customersList.add(this.customer1);
        this.customersList.add(this.customer2);
        this.customersList.add(this.customer3);

    }

    @Test
    public void testGetCustomers() throws Exception{
        Page<Customer> paginatedCustomers = new PageImpl<Customer>((List<Customer>) this.customersList);

        when(customerService.findAll(notNull())).thenReturn(paginatedCustomers);

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(3)))
        ;
    }

    @Test
    public void testGetCustomerById() throws Exception{
        Long id = 5L;
        String name = this.customer1.getName();
        this.customer1.setId(id);

        when(customerService.findById(notNull())).thenReturn(this.customer1);

        mockMvc.perform(get("/customers" + "/" + id)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(name)))
        ;
    }

    @Test
    public void testSearchCustomers() throws Exception{
        Page<Customer> paginatedCustomer = new PageImpl<Customer>((List<Customer>) this.customersList);
        String name = "Serginho";

        when(customerService.findByName(notNull(),notNull())).thenReturn(paginatedCustomer);

        mockMvc.perform(get("/customers/search")
                .param("name", name)
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", Matchers.hasSize(3)))
        ;
    }

    @Test
    public void testPostCustomer() throws Exception{
        this.city1.setId(5L);

        Customer customer = new Customer("Ze Cariona", this.city1);

        Map<String, String> city = new HashMap();
        city.put("id", this.city1.getId().toString());
        city.put("name", this.city1.getName());

        String cityString = JSONObject.toJSONString(city);

        Map<String, String> data = new HashMap();
        data.put("name", customer.getName());
        data.put("city", cityString);

        when(customerService.create(notNull())).thenReturn(customer);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
        ;

    }

    @Test
    public void testUpdateCustomerName() throws Exception{
        this.customer1.setId(5L);
        Long id = this.customer1.getId();
        String newName = "Zeca Uburubu";

        this.customer1.setName(newName);

        Map<String, String> data = new HashMap();
        data.put("name", this.customer1.getName());

        when(customerService.update(notNull())).thenReturn(this.customer1);

        mockMvc.perform(put("/customers" + "/" + id )
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(5)))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testUpdateCustomerCity() throws Exception{

        this.customer1.setId(5L);
        Long id = this.customer1.getId();

        this.city1.setId(5L);
        this.city1.setName("Chorume");
        Map<String, String> city = new HashMap();
        city.put("id", this.city1.getId().toString());
        city.put("name", this.city1.getName());
        String cityString = JSONObject.toJSONString(city);

        Map<String,String> data = new HashMap();
        data.put("city", cityString);

        when(customerService.update(notNull())).thenReturn(this.customer1);

        mockMvc.perform(put("/customers" + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(5)))
                .andDo(MockMvcResultHandlers.print())
        ;

    }

    @Test
    public void testDeleteCustomer() throws Exception{
        this.customer1.setId(5L);
        Long id = this.customer1.getId();

        mockMvc.perform(delete("/customers" + "/" + id))
                .andExpect(status().isOk());

    }

    @After
    public void tearDown(){}

}
