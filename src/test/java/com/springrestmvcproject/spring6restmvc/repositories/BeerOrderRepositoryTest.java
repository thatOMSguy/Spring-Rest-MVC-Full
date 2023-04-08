package com.springrestmvcproject.spring6restmvc.repositories;

import com.springrestmvcproject.spring6restmvc.entities.Beer;
import com.springrestmvcproject.spring6restmvc.entities.BeerOrder;
import com.springrestmvcproject.spring6restmvc.entities.BeerOrderShipment;
import com.springrestmvcproject.spring6restmvc.entities.Customer;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@DataJpaTest

@SpringBootTest
class BeerOrderRepositoryTest {


    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerRepository beerRepository;

    Beer testBeer;
    Customer testCustomer;

    @BeforeEach
    void setUp(){
        testCustomer = customerRepository.findAll().get(0);
        testBeer = beerRepository.findAll().get(0);

    }


    @Transactional
    @Test
    void testBeerOrders(){

        BeerOrder beerOrder = BeerOrder.builder()
                .customerRef("test order")
                .customer(testCustomer)
                .beerOrderShipment(BeerOrderShipment.builder()
                        .trackingNumber("1234-566-76").build()).build();

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);

        System.out.println(savedBeerOrder.getCustomerRef());


    }

}