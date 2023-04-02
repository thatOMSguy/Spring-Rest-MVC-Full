package com.springrestmvcproject.spring6restmvc.services;

import com.springrestmvcproject.spring6restmvc.model.BeerCSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BeerCSVServiceImplTest {
    BeerCSVService beerCSVService = new BeerCSVServiceImpl();

    @Test
    void convertCSV() throws FileNotFoundException {

        File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
        List<BeerCSVRecord> beerRecords = beerCSVService.convertCSV(file);
        System.out.println(beerRecords.size());
        assertThat(beerRecords.size()).isGreaterThan(0);
    }
}