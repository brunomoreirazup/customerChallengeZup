package com.zup.controller;

import com.zup.model.City;
import com.zup.model.Customer;
import com.zup.service.CustomerServiceBean;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
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

    private static final String PATH = URI.create("/customers").toString();
    private static final String CHARACTER_ENCODING = "utf-8";

    private City city1 = new City("Uberaba");
    private City city2 = new City("Uberlandia");

    private Collection<Customer> customersList;

    private Customer customer1 = new Customer("Serginho Caneca", this.city1);
    private Customer customer2 = new Customer("Serginho Groisman", this.city2);
    private Customer customer3 = new Customer("Serginho Namorado", this.city1);


    @Before
    public void init(){

        this.customersList = new ArrayList<>();

        this.customersList.add(this.customer1);
        this.customersList.add(this.customer2);
        this.customersList.add(this.customer3);

    }

    @Test
    public void testGetCustomers() throws Exception{
        Page<Customer> paginatedCustomers = new PageImpl<>((List<Customer>) this.customersList);

        when(customerService.findAll(notNull())).thenReturn(paginatedCustomers);

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customers", Matchers.hasSize(3)))
        ;
    }

    @Test
    public void testGetCustomerById() throws Exception{
        Long id = 5L;
        String name = this.customer1.getName();
        this.customer1.setId(id);

        when(customerService.findById(notNull())).thenReturn(this.customer1);

        mockMvc.perform(get(PATH + "/" + id)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(name)))
        ;
    }

    @Test
    public void testSearchCustomers() throws Exception{
        Page<Customer> paginatedCustomer = new PageImpl<>((List<Customer>) this.customersList);
        String name = "Serginho";

        when(customerService.findByName(notNull(),notNull())).thenReturn(paginatedCustomer);

        mockMvc.perform(get(PATH + "/search")
                .param("name", name)
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.customers", Matchers.hasSize(3)))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testPostCustomer() throws Exception{
        this.city1.setId(5L);

        Customer customer = new Customer("Ze Cariona", this.city1);

        Map<String, String> city = new HashMap<>();
        city.put("id", this.city1.getId().toString());
        city.put("name", this.city1.getName());

        String cityString = JSONObject.toJSONString(city);

        Map<String, String> data = new HashMap<>();
        data.put("name", customer.getName());
        data.put("city", cityString);

        when(customerService.create(notNull())).thenReturn(customer);

        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",Matchers.is(customer.getName())))
                .andDo(MockMvcResultHandlers.print())
        ;

    }

    @Test
    public void testUpdateCustomerName() throws Exception{
        this.customer1.setId(5L);
        Long id = this.customer1.getId();
        String newName = "Zeca Uburubu";

        this.customer1.setName(newName);

        Map<String, String> data = new HashMap<>();
        data.put("name", this.customer1.getName());

        when(customerService.update(notNull())).thenReturn(this.customer1);

        mockMvc.perform(put(PATH + "/" + id )
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", Matchers.is(id.intValue())))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testUpdateCustomerCity() throws Exception{

        this.customer1.setId(5L);
        Long id = this.customer1.getId();

        this.city1.setId(5L);
        this.city1.setName("Chorume");
        Map<String, String> city = new HashMap<>();
        city.put("id", this.city1.getId().toString());
        city.put("name", this.city1.getName());
        String cityString = JSONObject.toJSONString(city);

        Map<String,String> data = new HashMap<>();
        data.put("city", cityString);

        when(customerService.update(notNull())).thenReturn(this.customer1);

        mockMvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", Matchers.is(id.intValue())))
                .andDo(MockMvcResultHandlers.print())
        ;

    }

    @Test
    public void testDeleteCustomer() throws Exception{
        this.customer1.setId(5L);
        Long id = this.customer1.getId();

        mockMvc.perform(delete(PATH + "/" + id))
                .andExpect(status().isNoContent());

    }


}
