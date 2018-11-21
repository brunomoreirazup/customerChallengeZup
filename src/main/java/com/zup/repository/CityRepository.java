package com.zup.repository;

import com.zup.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Page<City> findByNameContainingIgnoreCase(Pageable pageable, String name);
}
