package com.zup.controller;

import com.zup.model.City;
import com.zup.model.CustomPage;
import com.zup.service.CityServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RestController
public class CityController {

    @Autowired
    CityServiceBean cityService;

    @GetMapping("/cities")
    public CustomPage getCities(Pageable pageable){
        return new CustomPage(cityService.findAll(pageable), "cities");
    }

    @GetMapping("/cities/search")
    public CustomPage searchCities(Pageable pageable, @RequestParam(value = "name") String name){
        return new CustomPage(cityService.findByName(pageable, name), "cities");
    }

    @GetMapping("/cities/{id}")
    public City getCityById(@PathVariable Long id){
        return cityService.findById(id);
    }

    @PostMapping("/cities")
    @ResponseStatus(HttpStatus.CREATED)
    public City postCiy(@RequestBody City city){
        return cityService.create(city);
    }

    @PutMapping("/cities/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public City putCity(@PathVariable Long id, @RequestBody City city){
        city.setId(id);
        return cityService.update(city);
    }

    @DeleteMapping("/cities/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Long id){
        cityService.delete(id);
    }

}
