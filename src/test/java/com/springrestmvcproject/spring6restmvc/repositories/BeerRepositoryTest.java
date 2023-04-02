package com.springrestmvcproject.spring6restmvc.repositories;

import com.springrestmvcproject.spring6restmvc.entities.Beer;
import com.springrestmvcproject.spring6restmvc.model.BeerStyle;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    //added needed fields to ensure validation is fine
    //since h2 runs very fast and validation culdnt be done that we did a flush opern
    @Test
    void testSaveBeer() {
        Beer savedBeer = beerRepository.save(Beer.builder()
                .beerName("New Beer")
                .beerStyle(BeerStyle.SAISON)
                .upc("11234")
                .price(new BigDecimal(12.345)).build());

        beerRepository.flush();
        assertThat(savedBeer).isNotNull();
        assertThat(savedBeer.getId()).isNotNull();
    }
    @Test
    void testSaveBeerNameTooLongException() {

        assertThrows(ConstraintViolationException.class, ()->{

            beerRepository.save(Beer.builder()
                    .beerName("New Beer 11111111111111111111111111111111111111111111111111111111111111111111111111111111111")
                    .beerStyle(BeerStyle.SAISON)
                    .upc("11234")
                    .price(new BigDecimal(12.345)).build());

            beerRepository.flush();

        });



//        assertThat(savedBeer).isNotNull();
//        assertThat(savedBeer.getId()).isNotNull();
    }

}