package com.codecademy.goldmedal.repositories;

import java.util.List;

import com.codecademy.goldmedal.model.Country;
import org.springframework.data.repository.CrudRepository;

import com.codecademy.goldmedal.model.GoldMedal;

public interface GoldMedalRepository extends CrudRepository<GoldMedal, Integer> {
    List<GoldMedal> findByCountry(String countryName);
    List<GoldMedal> findByGender(String gender);
    List<GoldMedal> findBySeason(String season);
    List<GoldMedal> findBySeasonAndCountry(String season, String countryName);
}
