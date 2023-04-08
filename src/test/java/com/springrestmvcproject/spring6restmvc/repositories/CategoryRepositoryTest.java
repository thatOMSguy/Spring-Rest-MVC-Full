package com.springrestmvcproject.spring6restmvc.repositories;

import com.springrestmvcproject.spring6restmvc.entities.Beer;
import com.springrestmvcproject.spring6restmvc.entities.Category;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;

    @BeforeEach
    void setup(){
        testBeer = beerRepository.findAll().get(0);

    }

    @Transactional
    @Test
    void testAddCategory(){
        Category savedCateg = categoryRepository.save(Category.builder()
                .description("some description").build());

        testBeer.addCategory(savedCateg);

        Beer savedBeer = beerRepository.save(testBeer);

        System.out.println(savedBeer.getBeerName());
    }


}