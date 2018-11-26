package com.zup.service;

import com.zup.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CityService {

    Page<City> findAll(Pageable pageable);

    Page<City> findByName(Pageable pageable, String name);

    City findById(Long id);

    City create(City city);

    City update(City city);

    void delete(Long id);

}
