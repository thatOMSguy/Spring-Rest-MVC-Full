package com.springrestmvcproject.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springrestmvcproject.spring6restmvc.config.SpringSecurityConfig;
import com.springrestmvcproject.spring6restmvc.model.BeerDTO;
import com.springrestmvcproject.spring6restmvc.services.BeerService;
import com.springrestmvcproject.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.springrestmvcproject.spring6restmvc.controller.BeerController.BEER_PATH;
import static com.springrestmvcproject.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;


//@SpringBootTest removed to use mockMVC
@WebMvcTest(BeerController.class)
@Import(SpringSecurityConfig.class)
class BeerControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    @Captor
    ArgumentCaptor<UUID> argumentCaptor;

    @Captor
    ArgumentCaptor<BeerDTO> beerArgumentCaptor;


    static final String USER_NAME = "myuser";
    static final String PASSWORD = "password";
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor
            JWT_REQUEST_POST_PROCESSOR
            = jwt().jwt(jwt ->
    {
        jwt.claims(
                        claims -> {
                            claims.put("scope", "message-read");
                            claims.put("scope", "message-write");

                        })
                .subject("messaging-client")
                .notBefore(Instant.now().minusSeconds(5l));

    });


    @BeforeEach
    void setup() {
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerById() throws Exception {

        BeerDTO testBeer = beerServiceImpl.listBeers(null, null,
                false, 1, 25).getContent().get(0);

        given(beerService.getBeerById(testBeer.getId())).willReturn(Optional.of(testBeer));

        mockMvc.perform(get(BEER_PATH_ID, testBeer.getId())
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(testBeer.getBeerName())));


    }

    @Test
    void testListBeers() throws Exception {
        given(beerService.listBeers(any(), any(), any(), any(), any()))
                .willReturn(beerServiceImpl.listBeers(null, null,
                        false, 1, 25));

        mockMvc.perform(get("/api/v1/beer")
//added the http basic auth however this only works with GET not POST PUT etc
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(3)));

    }


    @Test
    void testCreateNewBeer() throws Exception {

        BeerDTO beer = beerServiceImpl.listBeers(null, null,
                false, 1, 25).getContent().get(0);
        //  System.out.println(objectMapper.writeValueAsString(beer));

        beer.setId(null);
        beer.setVersion(null);

        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers(null, null, false,
                        1, 25).getContent().get(1));

        mockMvc.perform(post(BEER_PATH)

                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

    }


    @Test
    void testUpdateBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false,
                1, 25).getContent().get(0);

        given(beerService.updateBeerById(any(), any())).willReturn(Optional.of(beer));
        mockMvc.perform(put(BEER_PATH_ID, beer.getId())
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(any(UUID.class), any(BeerDTO.class));
    }


    @Test
    void testDeleteBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false,
                1, 25).getContent().get(0);

        given(beerService.deleteBeerById(any())).willReturn(true);
        mockMvc.perform(delete(BEER_PATH_ID, beer.getId())
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).deleteBeerById(argumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    void testPatchUpdateBeer() throws Exception {
        BeerDTO beer = beerServiceImpl.listBeers(null, null, false,
                1, 25).getContent().get(0);

        Map<String, Object> beerMap = new HashMap<>();
        beerMap.put("beerName", "New Name");

        mockMvc.perform(patch(BEER_PATH_ID, beer.getId())
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerMap)))

                .andExpect(status().isNoContent());

        verify(beerService).patchBeerById(argumentCaptor.capture(),
                beerArgumentCaptor.capture());

        assertThat(beer.getId()).isEqualTo(argumentCaptor.getValue());
        System.out.println("captured value ===== > " +
                beerArgumentCaptor.getValue().getBeerName());
        assertThat(beerMap.get("beerName")).isEqualTo(beerArgumentCaptor.getValue().getBeerName());
    }


    @Test
    void getBeerByIdNotFound() throws Exception {

        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID())
                        .with(JWT_REQUEST_POST_PROCESSOR))
                .andExpect(status().isNotFound());

    }


    @Test
    void testCreateBeerWithNoName() throws Exception {

        BeerDTO beerDTO = BeerDTO.builder().build();
        given(beerService.saveNewBeer(any(BeerDTO.class)))
                .willReturn(beerServiceImpl.listBeers(null, null, false,
                        1, 25).getContent().get(1));

        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                        .with(JWT_REQUEST_POST_PROCESSOR)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isBadRequest()).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());

    }


}
