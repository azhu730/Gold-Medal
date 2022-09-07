package com.codecademy.goldmedal.controller;

import com.codecademy.goldmedal.model.*;
import com.codecademy.goldmedal.repositories.CountryRepository;
import com.codecademy.goldmedal.repositories.GoldMedalRepository;
import org.apache.commons.text.WordUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {
    // declare references to your repositories
    private final GoldMedalRepository goldMedalRepository;
    private final CountryRepository countryRepository;

    // update your constructor to include your repositories
    public GoldMedalController(final GoldMedalRepository goldMedalRepository,
                               final CountryRepository countryRepository) {
        this.goldMedalRepository = goldMedalRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam String sort_by, @RequestParam String ascending) {
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable String country) {
        String countryName = WordUtils.capitalizeFully(country);
        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country, @RequestParam String sort_by,
                                                          @RequestParam String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy,
                                                                   boolean ascendingOrder) {
        List<GoldMedal> medalsList;
        switch (sortBy) {
            case "year":
                // list of medals sorted by year in the given order
                medalsList = this.goldMedalRepository.findByCountry(countryName);
                if (ascendingOrder) {
                    medalsList.sort(Comparator.comparing(GoldMedal::getYear));
                } else {
                    medalsList.sort(Comparator.comparing(GoldMedal::getYear).reversed());
                }
                break;
            case "season":
                // list of medals sorted by season in the given order
                medalsList = this.goldMedalRepository.findByCountry(countryName);
                if (ascendingOrder) {
                    medalsList.sort(Comparator.comparing(GoldMedal::getSeason));
                } else {
                    medalsList.sort(Comparator.comparing(GoldMedal::getSeason).reversed());
                }
                break;
            case "city":
                // list of medals sorted by city in the given order
                medalsList = this.goldMedalRepository.findByCountry(countryName);
                if (ascendingOrder) {
                    medalsList.sort(Comparator.comparing(GoldMedal::getCity));
                } else {
                    medalsList.sort(Comparator.comparing(GoldMedal::getCity).reversed());
                }
                break;
            case "name":
                // list of medals sorted by athlete's name in the given order
                medalsList = this.goldMedalRepository.findByCountry(countryName);
                if (ascendingOrder) {
                    medalsList.sort(Comparator.comparing(GoldMedal::getName));
                } else {
                    medalsList.sort(Comparator.comparing(GoldMedal::getName).reversed());
                }
                break;
            case "event":
                // list of medals sorted by event in the given order
                medalsList = this.goldMedalRepository.findByCountry(countryName);
                if (ascendingOrder) {
                    medalsList.sort(Comparator.comparing(GoldMedal::getEvent));
                } else {
                    medalsList.sort(Comparator.comparing(GoldMedal::getEvent).reversed());
                }
                break;
            default:
                medalsList = new ArrayList<>();
                break;
        }

        return new CountryMedalsListResponse(medalsList);
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        // get the country; this repository method should return a java.util.Optional
        Optional<Country> countryOptional = this.countryRepository.findByName(countryName);
        if (countryOptional.isEmpty()) {
            return new CountryDetailsResponse(countryName);
        }

        Country country = countryOptional.get();
        // get the medal count
        int goldMedalCount = this.goldMedalRepository.findByCountry(country.getName()).size();

        // get the collection of wins at the Summer Olympics, sorted by year in ascending order
        List<GoldMedal> summerWins = this.goldMedalRepository.findBySeasonAndCountry("Summer", countryName);
        summerWins.sort(Comparator.comparing(GoldMedal::getYear));
        Integer numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;
        // get the total number of events at the Summer Olympics
        int totalSummerEvents = this.goldMedalRepository.findBySeason("Summer").size();
        Float percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != null ? (float) summerWins.size()
                / totalSummerEvents : null;
        Integer yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        // get the collection of wins at the Winter Olympics
        List<GoldMedal> winterWins = this.goldMedalRepository.findBySeasonAndCountry("Winter", countryName);
        winterWins.sort(Comparator.comparing(GoldMedal::getYear));
        Integer numberWinterWins = winterWins.size() > 0 ? winterWins.size() : null;
        // get the total number of events at the Winter Olympics, sorted by year in ascending order
        int totalWinterEvents = this.goldMedalRepository.findBySeason("Winter").size();
        Float percentageTotalWinterWins = totalWinterEvents != 0 && numberWinterWins != null ? (float) winterWins.size()
                / totalWinterEvents : null;
        Integer yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;

        // get the number of wins by female athletes
        int numberEventsWonByFemaleAthletes = this.goldMedalRepository.findByGender("Women").size();
        // get the number of wins by male athletes
        int numberEventsWonByMaleAthletes = this.goldMedalRepository.findByGender("Men").size();

        return new CountryDetailsResponse(
                countryName,
                country.getGdp(),
                country.getPopulation(),
                goldMedalCount,
                numberSummerWins,
                percentageTotalSummerWins,
                yearFirstSummerWin,
                numberWinterWins,
                percentageTotalWinterWins,
                yearFirstWinterWin,
                numberEventsWonByFemaleAthletes,
                numberEventsWonByMaleAthletes);
    }

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;
        switch (sortBy) {
            case "name":
                // list of countries sorted by name in the given order
                countries = this.countryRepository.findAll();
                if (ascendingOrder) {
                    countries.sort(Comparator.comparing(Country::getName));
                } else {
                    countries.sort(Comparator.comparing(Country::getName).reversed());
                }
                break;
            case "gdp":
                // list of countries sorted by gdp in the given order
                countries = this.countryRepository.findAll();
                if (ascendingOrder) {
                    countries.sort(Comparator.comparing(Country::getGdp));
                } else {
                    countries.sort(Comparator.comparing(Country::getGdp).reversed());
                }
                break;
            case "population":
                // list of countries sorted by population in the given order
                countries = this.countryRepository.findAll();
                if (ascendingOrder) {
                    countries.sort(Comparator.comparing(Country::getPopulation));
                } else {
                    countries.sort(Comparator.comparing(Country::getPopulation).reversed());
                }
                break;
            case "medals":
            default:
                // list of countries in any order you choose; for sorting by medal count, additional logic below will handle that
                countries = this.countryRepository.findAll();
                if (ascendingOrder) {
                    countries.sort(Comparator.comparing(Country::getCode));
                } else {
                    countries.sort(Comparator.comparing(Country::getCode).reversed());
                }
                break;
        }

        List<CountrySummary> countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries.stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();
        for (Country country : countries) {
            // get count of medals for the given country
            int goldMedalCount = this.goldMedalRepository.findByCountry(country.getName()).size();
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }
        return countrySummaries;
    }
}
