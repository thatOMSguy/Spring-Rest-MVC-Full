package com.springrestmvcproject.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springrestmvcproject.spring6restmvc.entities.Beer;
import com.springrestmvcproject.spring6restmvc.mappers.BeerMapper;
import com.springrestmvcproject.spring6restmvc.model.BeerDTO;
import com.springrestmvcproject.spring6restmvc.model.BeerStyle;
import com.springrestmvcproject.spring6restmvc.repositories.BeerRepository;
import jakarta.transaction.Transactional;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.springrestmvcproject.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerIntegTest {

    @Autowired
    BeerController beerController;
    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    WebApplicationContext wac;
    @Autowired
    ObjectMapper objectMapper;

    MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(springSecurity()).build();
    }

    @Test
    void testInvalidAuth() throws Exception{

        mockMvc.perform(get(BeerController.BEER_PATH)
                .queryParam("beerStyle",BeerStyle.IPA.name())
                .queryParam("pageSize","800"))
                .andExpect(status().isUnauthorized());

    }


    @Test
    void tesListBeersByStyleAndNameShowInventoryTruePage2() throws Exception {

        MvcResult result = mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                        .queryParam("beerName", "%IPA%")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageNumber","2")
                        .queryParam("pageSize","50"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(50)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.notNullValue()))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryTrue() throws Exception {

        MvcResult result = mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "true")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)))
                .andExpect(jsonPath("$.content[4].quantityOnHand").value(IsNull.notNullValue()))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    void tesListBeersByStyleAndNameShowInventoryFalse() throws Exception {
        MvcResult result = mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("showInventory", "false")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)))
                .andExpect(jsonPath("$.content[0].quantityOnHand").value(IsNull.nullValue()))
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @Test
    void tesListBeersByStyleAndName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                        .queryParam("beerName", "IPA")
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(310)));
    }

    @Test
    void tesListBeersByStyle() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                        .queryParam("beerStyle", BeerStyle.IPA.name())
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(548)));
    }

    @Test
    void tesListBeersByName() throws Exception {
        mockMvc.perform(get(BeerController.BEER_PATH)
                        .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                        .queryParam("beerName", "IPA")
                        .queryParam("pageSize", "800"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()", is(336)));
    }

    @Test
    void testPatchBeerBadName() throws Exception {
        Beer beer = beerRepository.findAll().get(0);
        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name1111111111111111111111111111111111111" +
                "11111111111111111111111111111111111111111111111111111111111");


        MvcResult result =
                mockMvc.perform(patch(BEER_PATH_ID, beer.getId())
                                .with(BeerControllerTest.JWT_REQUEST_POST_PROCESSOR)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(beerMap)))
                        .andExpect(status().isBadRequest()).andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }


    @Test
    void testListBeers() {
        Page<BeerDTO> beerDTOList = beerController.listBeers(null,
                null, false, 1, 2413);
        assertThat(beerDTOList.getContent().size()).isEqualTo(1000); //since 1000 is max result per page
    }


    //the below test actually alter the in mem data base by deleting all entries in dtolist, and since sprint boot test
    //runs alphabetically so the testEmptyList runs but the testListBeer fails since it now does not have any fata in dtoList
    //what we need to do in this case is mark that operation on db done here is transactional and rollback the DB operation once
    //test run is completed
    //Alsp by default SB does rollback for operation that happens as part of transaction, but since we are dealing with controller layer here
    //it does not do so.
    @Rollback
    @Transactional
    @Test
    void testEmptyList() {

        beerRepository.deleteAll();
        Page<BeerDTO> beerDTOList = beerController
                .listBeers(null, null, false, 1, 25);
       // beerDTOList.getContent().removeAll(beerDTOList.getContent());
        assertThat(beerDTOList.getContent().size()).isEqualTo(0);

    }

    @Test
    void testGetBeerById() {
        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerController.getBeerById(beer.getId());

        assertThat(beerDTO).isNotNull();
    }

    @Test
    void testBeerIdNotFound() {

        //using lambda
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(UUID.randomUUID());
        });

/**
 //without using lambda
 */
        /*assertThrows(NotFoundException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                beerController.getBeerById(UUID.randomUUID());
            }
        });*/


    }


    @Transactional
    @Rollback
    @Test
    void saveNewBeerTest() {
        BeerDTO beerDTO = BeerDTO.builder().beerName("New Beer").build();

        ResponseEntity responseEntity = beerController.handlePost(beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();

        String[] locationUUID = responseEntity.getHeaders().getLocation().getPath().split("/");

        UUID savedUUID = UUID.fromString(locationUUID[4]);
        Beer beer = beerRepository.findById(savedUUID).get();
        assertThat(beer).isNotNull();
    }


    @Rollback
    @Transactional
    @Test
    void updateBeerByIdTest() {

        Beer beer = beerRepository.findAll().get(0);

        BeerDTO beerDTO = beerMapper.beerTobeerDTO(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);

        final String beerName = beer.getBeerName() + " NEW";
        beerDTO.setBeerName(beerName);

        ResponseEntity responseEntity = beerController.updateById(beer.getId(), beerDTO);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));

        Beer updateBeer = beerRepository.findById(beer.getId()).get();

        assertThat(updateBeer.getBeerName()).isEqualTo(beerName);

    }

    @Test
    void testUpdateBeerNotFound() {
        assertThrows(NotFoundException.class, () ->
        {
            beerController.updateById(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    @Rollback
    @Transactional
    @Test
    void deleteBeerById() {

        Beer beer = beerRepository.findAll().get(0);
        ResponseEntity responseEntity = beerController.deleteById(beer.getId());

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
        assertThat(beerRepository.findById(beer.getId())).isEmpty();


    }

    @Test
    void testDeleteByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteById(UUID.randomUUID());
        });
    }


}

