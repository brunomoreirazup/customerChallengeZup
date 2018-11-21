package com.zup.integration;

import com.zup.controller.CustomerController;
import com.zup.model.City;
import com.zup.model.Customer;
import com.zup.service.CityServiceBean;
import com.zup.service.CustomerServiceBean;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CustomerIntegrationTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerServiceBean customerService;

    @Autowired
    CityServiceBean cityService;


    private City city1;
    private City city2;

    private Customer customer1;
    private Customer customer2;

    private static final String PATH = "/customers";

    @Before
    public void init(){
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        this.city1 = cityService.create(new City("Uberlandia"));
        this.city2 = cityService.create(new City("Uberaba"));

        this.customer1 = customerService.create(new Customer("Jeremias", this.city1));
        this.customer2 = customerService.create(new Customer("Jerbas", this.city2));
    }

    @Test
    public void testGetAllCustomers() throws Exception{

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", Matchers.greaterThan(0)))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testGetCustomerById() throws Exception{
        Long id = this.customer1.getId();

        mockMvc.perform(get(PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(this.customer1.getName())));
    }

    @Test
    public void testSearchCustomers() throws Exception{
        String searchName = "Jer";

        mockMvc.perform(get(PATH + "/search")
                .param("name", searchName)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", Matchers.greaterThan(0)))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testPostCustomer() throws Exception{
        Customer customer = new Customer("Jean", this.city1);

        Map<String, Object> data = new HashMap();
        data.put("name", customer.getName());
        data.put("city", this.city1);

        mockMvc.perform(post(PATH)
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
        Long id = this.customer1.getId();
        this.customer1.setName("Cobalto");

        Map<String, String> data = new HashMap();
        data.put("name", this.customer1.getName());

        mockMvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testUpdateCustomerCity() throws Exception{
        Long id = this.customer1.getId();

        Map<String,Object> data = new HashMap();
        data.put("city", this.city2);

        mockMvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testDeleteCustomer() throws Exception{
        Long id = this.customer1.getId();

        mockMvc.perform(delete(PATH + "/" + id))
                .andExpect(status().isOk());
    }

}
