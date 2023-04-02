package com.springrestmvcproject.spring6restmvc.mappers;

import com.springrestmvcproject.spring6restmvc.entities.Beer;
import com.springrestmvcproject.spring6restmvc.entities.Customer;
import com.springrestmvcproject.spring6restmvc.model.BeerDTO;
import com.springrestmvcproject.spring6restmvc.model.CustomerDTO;
import org.mapstruct.Mapper;

//with the mapper anntn it ll create an impl in target u can build and check it in target folder
@Mapper
public interface CustomerMapper {

    Customer customerDtoToCustomer(CustomerDTO dto);
    CustomerDTO customerToCustomerDTO(Customer beer);

}
