package com.zup.integration;

import com.zup.controller.CityController;
import com.zup.model.City;
import com.zup.service.CityService;
import com.zup.service.CityServiceBean;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CityIntegrationTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    CityController cityController;

    @Autowired
    CityServiceBean cityService;

    private static final String PATH = "/cities";

    private City city1;
    private City city2;
    private City city3;


    @Before
    public void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();

        this.city1 = cityService.create(new City("Serrana"));
        this.city2 = cityService.create(new City("Bavaria"));
        this.city3 = cityService.create(new City("Sub Zero"));

    }


    @Test
    public void testGetCities() throws Exception{

        String name = "Sub Zero";

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Glacial")));
    }

    @Test
    public void testGetCityById() throws Exception{
        Long id = this.city1.getId();

        mockMvc.perform(get(PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",Matchers.is(this.city1.getName())));
    }

    @Test
    public void testGetCitiesByName() throws Exception{

        String searchName = "Bavaria";

        mockMvc.perform(get(PATH + "/search")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", Matchers.is(searchName)))
                .andDo(MockMvcResultHandlers.print());
        ;


    }

    @Test
    public void testPostCity() throws Exception{
        City newCity = new City("Kaiser");

        Map<String, String> data = new HashMap();

        data.put("name", newCity.getName());

        mockMvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding("utf-8")
        )
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testPutCity() throws Exception{
        Long id = this.city1.getId();
        String newName = "Antarctica";

        this.city1.setName(newName);

        Map<String,String> data = new HashMap();
        data.put("name", this.city1.getName());

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
    public void testDeleteCity() throws Exception{
        Long id = this.city1.getId();

        mockMvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
        ;
    }

    @After
    public void tearDown(){
    }
}
