package com.zup.service;

import com.zup.model.City;
import com.zup.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class CityServiceBean implements CityService{

    @Autowired
    private CityRepository cityRepository;

    @Override
    public Page<City> findAll(Pageable pageable) {

        Page<City> cities = cityRepository.findAll(pageable);
        return cities;
    }

    @Override
    public Page<City> findByName(Pageable pageable, String name) {
        Page<City> paginatedCity = cityRepository.findByNameContainingIgnoreCase(pageable, name);
        return paginatedCity;
    }

    @Override
    public City findById(Long id) {

        Optional<City> city = cityRepository.findById(id);
        return city.orElse(null);
    }


    @Override
    public City create(City city) {

        if(city.getId() != null){
            throw new EntityExistsException("The id attribute must be null to persist a new entity");
        }

        City returnedCity = cityRepository.saveAndFlush(city);
        return returnedCity;
    }

    @Override
    public City update(City city) {

        City returnedCity = findById(city.getId());

        if(returnedCity == null ){
            throw new NoResultException("Requested entity not found");
        }

        returnedCity.setName(city.getName());
        returnedCity = cityRepository.saveAndFlush(returnedCity);

        return returnedCity;
    }

    @Override
    public void delete(Long id) {

        cityRepository.deleteById(id);

    }
}
