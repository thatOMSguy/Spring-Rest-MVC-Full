package com.springrestmvcproject.spring6restmvc.controller;

import com.springrestmvcproject.spring6restmvc.entities.Customer;
import com.springrestmvcproject.spring6restmvc.entities.Customer;
import com.springrestmvcproject.spring6restmvc.mappers.CustomerMapper;
import com.springrestmvcproject.spring6restmvc.model.CustomerDTO;
import com.springrestmvcproject.spring6restmvc.model.CustomerDTO;
import com.springrestmvcproject.spring6restmvc.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomerControllerIntegTest {

    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    CustomerMapper customerMapper;

    @Test
    void testListCustomers() {
        List<CustomerDTO> customerDTOList = customerController.listCustomers();
        assertThat(customerDTOList.size()).isEqualTo(3);
    }


    //the below test actually alter the in mem data base by deleting all entries in dtolist, and since sprint boot test
    //runs alphabetically so the testEmptyList runs but the testListCustomer fails since it now does not have any fata in dtoList
    //what we need to do in this case is mark that operation on db done here is transactional and rollback the DB operation once
    //test run is completed
    //Alsp by default SB does rollback for operation that happens as part of transaction, but since we are dealing with controller layer here
    //it does not do so.
    @Rollback
    @Transactional
    @Test
    void testEmptyList() {

        customerRepository.deleteAll();

        List<CustomerDTO> customerDTOList = customerController.listCustomers();
        assertThat(customerDTOList.size()).isEqualTo(0);

    }

    @Test
    void testGetCustomerById(){
        Customer customer = customerRepository.findAll().get(0);
        CustomerDTO customerDTO = customerController.getCustomerById(customer.getId());

        assertThat(customerDTO).isNotNull();
    }

    @Test
    void testCustomerIdNotFound(){

        //using lambda
        assertThrows(NotFoundException.class, () -> {
            customerController.getCustomerById(UUID.randomUUID());
        });

/**
 //without using lambda
 */
        /*assertThrows(NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                customerController.getCustomerById(UUID.randomUUID());
            }
        });*/


    }


    @Rollback
    @Transactional
    @Test
    void updateCustomerByIdTest() {

        Customer customer = customerRepository.findAll().get(0);

        CustomerDTO customerDTO = customerMapper.customerToCustomerDTO(customer);
        customerDTO.setId(null);
        customerDTO.setVersion(null);

        final String customerName = customer.getName() + " NEW";
        customerDTO.setCustomerName(customerName);

        ResponseEntity responseEntity = customerController.updateById(customer.getId(), customerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Customer updateCustomer = customerRepository.findById(customer.getId()).get();

        assertThat(updateCustomer.getName()).isEqualTo(customerName);

    }

    @Test
    void testUpdateCustomerNotFound() {
        assertThrows(NotFoundException.class, () ->
        {
            customerController.updateById(UUID.randomUUID(), CustomerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void deleteCustomerById() {

        Customer customer = customerRepository.findAll().get(0);
        ResponseEntity responseEntity = customerController.deleteById(customer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(customerRepository.findById(customer.getId())).isEmpty();


    }

    @Test
    void testDeleteByIdNotFound(){
        assertThrows(NotFoundException.class,()->{
            customerController.deleteById(UUID.randomUUID());
        });
    }


}