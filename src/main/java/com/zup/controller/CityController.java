package com.zup.controller;

import com.zup.model.City;
import com.zup.service.CityServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;


@RestController
public class CityController {

    @Autowired
    CityServiceBean cityService;

    @GetMapping("/cities")
    public Page<City> getCities(Pageable pageable){
        return cityService.findAll(pageable);
    }

    @GetMapping("/cities/search")
    public Page<City> searchCities(Pageable pageable, @RequestParam(value = "name") String name){
        return cityService.findByName(pageable, name);
    }

    @GetMapping("/cities/{id}")
    public City getCityById(@PathVariable Long id){
        return cityService.findById(id);
    }

    @PostMapping("/cities")
    public City postCiy(@RequestBody City city){
        return cityService.create(city);
    }

    @PutMapping("/cities/{id}")
    public City putCity(@PathVariable Long id, @RequestBody City city){
        city.setId(id);
        return cityService.update(city);
    }

    @DeleteMapping("/cities/{id}")
    public void deleteCity(@PathVariable Long id){
        cityService.delete(id);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    String exceptionHandler(ValidationException e){
        return e.getMessage();
    }

}
