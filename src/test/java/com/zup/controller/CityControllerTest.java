package com.zup.controller;

import com.zup.model.City;
import com.zup.service.CityServiceBean;
import org.hamcrest.Matchers;
import net.minidev.json.JSONObject;
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

import java.net.URI;
import java.util.*;
import java.util.List;

import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CityController.class)
public class CityControllerTest {

    @MockBean
    CityServiceBean cityService;

    @Autowired
    MockMvc mockMvc;

    private static final String PATH = URI.create("/cities").toString();
    private static final String CHARACTER_ENCODING = "utf-8";

    private Collection<City> citiesList;
    private City city1 = new City("Uberaba");
    private City city2 = new City("Uberlandia");

    @Before
    public void init(){
        this.citiesList = new ArrayList<>();

        this. citiesList.add(this.city1);
        this.citiesList.add(this.city2);
    }

    @Test
    public void testGetCities() throws Exception{
        Page<City> paginatedCities = new PageImpl<>((List<City>) this.citiesList);

        when(cityService.findAll(notNull())).thenReturn(paginatedCities);


        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cities", Matchers.hasSize(2)))
                .andExpect(jsonPath("$._embedded.cities[0].name",Matchers.is(this.city1.getName())))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void testGetCityById() throws Exception{
        this.city1.setId(5L);
        Long id = this.city1.getId();

        when(cityService.findById(id)).thenReturn(this.city1);

        mockMvc.perform(get(PATH + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",Matchers.is(this.city1.getName())))
                .andDo(MockMvcResultHandlers.print())
        ;
    }

    @Test
    public void testSearchCities() throws Exception{
        Page<City> paginatedCities = new PageImpl<>((List<City>) this.citiesList);

        when(cityService.findByName(notNull(),notNull())).thenReturn(paginatedCities);

        mockMvc.perform(get(PATH + "/search" )
                .param("name","Ub")
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.cities", Matchers.hasSize(2)))
                .andExpect(jsonPath("$._embedded.cities[0].name", Matchers.is(this.city1.getName())))
                .andExpect(jsonPath("$._embedded.cities[1].name", Matchers.is(this.city2.getName())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPostCity() throws Exception{
        City city = new City("Fornace");

        Map<String, String> data = new HashMap<>();
        data.put("name", city.getName());

        when(cityService.create(notNull())).thenReturn(city);

        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("name",Matchers.is("Fornace")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testUpdateCity() throws Exception{
        this.city1.setId(5L);
        Long id = this.city1.getId();
        String newName = "Pizzaria Zebu";
        this.city1.setName(newName);

        Map<String, String > data = new HashMap<>();
        data.put("name", this.city1.getName());

        when(cityService.update(notNull())).thenReturn(this.city1);

        mockMvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(data))
                .characterEncoding(CHARACTER_ENCODING)
        )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id", Matchers.is(id.intValue())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteCity() throws Exception{
        this.city1.setId(5L);
        Long id = this.city1.getId();

        mockMvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

    }

}
