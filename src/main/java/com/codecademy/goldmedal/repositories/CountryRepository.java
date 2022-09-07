package com.codecademy.goldmedal.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.codecademy.goldmedal.model.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {
    Optional<Country> findByName(String countryName);
    List<Country> findAll();
}
