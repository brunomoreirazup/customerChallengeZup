package com.zup.service;

import com.zup.model.City;
import com.zup.repository.CityRepository;
import com.zup.service.CityService;
import com.zup.service.CityServiceBean;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

public class CityServiceTest {


    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    CityServiceBean cityService;

    private Collection<City> citiesList = new ArrayList();
    private City city1;
    private City city2;


    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        this.city1 = new City("Uberlandia");
        this.city2 = new City("Uberaba");
        citiesList.add(this.city1);
        citiesList.add(this.city2);


    }

    @Test
    public void testFindAll(){
        Pageable pageRequest = new PageRequest(0,20);
        Page<City> paginatedCities = new PageImpl<City>((List<City>) citiesList);

        when(cityRepository.findAll(pageRequest)).thenReturn(paginatedCities);

        Page<City> cities = cityService.findAll(pageRequest);

        Assert.assertNotNull("failure - expected not null", cities);
        Assert.assertEquals("failure - expected list size", 2, cities.getNumberOfElements());
        verify(cityRepository, atMost(1)).findAll();
    }

    @Test
    public void testFindByName(){
        Pageable pageRequest = new PageRequest(0, 20);
        Page<City> paginatedCities = new PageImpl<City>((List<City>) citiesList);

        String name = "Uber";

        when(cityRepository.findByNameContainingIgnoreCase(pageRequest, name)).thenReturn(paginatedCities);

        Page<City> cities = cityService.findByName(pageRequest,name);
        Assert.assertNotNull("failure - expected no null", cities);
        Assert.assertEquals("failure - expected list size", 2, cities.getNumberOfElements());
        verify(cityRepository, atMost(1)).findByNameContainingIgnoreCase(pageRequest, name);

    }

    @Test
    public void testFindByNameNotFound(){
        Collection<City> emptyCityList = new ArrayList();
        Pageable pageRequest = new PageRequest(0, 20);
        Page<City> paginatedCities = new PageImpl<City>((List<City>) emptyCityList);

        String name = "AEOUHAEOUHEOUHAEOUEHAOEUH";

        when(cityRepository.findByNameContainingIgnoreCase(pageRequest, name)).thenReturn(paginatedCities);
        Page<City> responseCities = cityService.findByName(pageRequest,name);

        Assert.assertNotNull("failure - expected no null", responseCities);
        Assert.assertEquals("failure - expected list size", 0, responseCities.getNumberOfElements());

        verify(cityRepository, atMost(1)).findByNameContainingIgnoreCase(pageRequest, name);


    }

    @Test
    public void testFindOne(){
        when(cityRepository.findById(this.city1.getId())).thenReturn(Optional.of(this.city1));

        City city = cityService.findById(city1.getId());

        Assert.assertNotNull("failure - expected not null", city);
        Assert.assertEquals("failure - expected id ", city1.getId(), city.getId());
        verify(cityRepository, atMost(1)).findById(this.city1.getId());

    }

    @Test
    public void testFindOneNotFound(){
        Long id = Long.MAX_VALUE;
        when(cityRepository.findById(id)).thenReturn(Optional.empty());

        City city = cityService.findById(id);

        Assert.assertNull("failure - expected null", city);
        verify(cityRepository, atMost(1)).findById(this.city1.getId());

    }

    @Test
    public void testCreate(){

        City city = new City("Araguari");

        when(cityRepository.saveAndFlush(city)).thenReturn(city);

        City returnedCity = cityService.create(city);

        Assert.assertNotNull("failure - expected not null", returnedCity);
        Assert.assertEquals("failure - expected city id",  city.getId(), returnedCity.getId());
        Assert.assertEquals("failure - expected city name", city.getName(), returnedCity.getName());

        verify(cityRepository, atMost(1)).saveAndFlush(city);
    }

    @Test
    public void testUpdate(){
        String newName = "Aracaju";

        this.city1.setName(newName);

        when(cityRepository.findById(this.city1.getId())).thenReturn(Optional.of(this.city1));
        when(cityRepository.saveAndFlush(this.city1)).thenReturn(this.city1);

        City returnedCity = cityService.update(this.city1);

        Assert.assertNotNull("failure - expected not null", returnedCity);
        Assert.assertEquals("failure - expected city name", newName, returnedCity.getName());
        Assert.assertEquals("failure - expected cityName", this.city1.getId(), returnedCity.getId());


        verify(cityRepository, atMost(1)).findById(this.city1.getId());
        verify(cityRepository, atMost(1)).saveAndFlush(this.city1);
    }

    @Test
    public void testDelete(){

        cityService.delete(this.city1.getId());
        verify(cityRepository, atMost(1)).deleteById(this.city1.getId());


    }

    @After
    public void tearDown(){}

}