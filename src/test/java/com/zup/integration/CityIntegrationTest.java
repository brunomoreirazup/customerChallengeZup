package com.zup.integration;

import com.zup.controller.CityController;
import com.zup.model.City;
import com.zup.service.CityServiceBean;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CityIntegrationTest extends AbstractTest{

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext context;

    @Autowired
    CityController cityController;

    @Autowired
    CityServiceBean cityService;

    private static final String PATH = URI.create("/cities").toString();
    private static final String CHARACTER_ENCODING = "utf-8";

    private City city1;
    private City city2;
    private City city3;


    @Before
    public void init() {
        documentationResultHandler = MockMvcRestDocumentation.document("{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));
        mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(this.restDocumentation))
                .alwaysDo(this.documentationResultHandler)
                .build();

        this.city1 = cityService.create(new City("Serrana"));
        this.city2 = cityService.create(new City("Bavaria"));
        this.city3 = cityService.create(new City("Sub Zero"));

    }


    @Test
    public void testGetAllCities() throws Exception{


        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", Matchers.greaterThan(0)))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testGetCityById() throws Exception{
        Long id = this.city1.getId();

        mockMvc.perform(get(PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",Matchers.is(this.city1.getName())));
    }


    @Test
    public void testSearchCities() throws Exception{

        String searchName = "Bavaria";

        mockMvc.perform(get(PATH + "/search")
                .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements", Matchers.greaterThan(0)))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testPostCity() throws Exception{
        City newCity = new City("Kaiser");

        Map<String, String> data = new HashMap<>();

        data.put("name", newCity.getName());

        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testPutCity() throws Exception{
        Long id = this.city1.getId();
        String newName = "Antarctica";

        this.city1.setName(newName);

        Map<String,String> data = new HashMap<>();
        data.put("name", this.city1.getName());

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
    public void testDeleteCity() throws Exception{
        Long id = this.city1.getId();

        mockMvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent())
        ;
    }
}
