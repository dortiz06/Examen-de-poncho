package com.huertosurbanos.parcelas.controller;

import com.huertosurbanos.parcelas.dto.ParcelResponse;
import com.huertosurbanos.parcelas.exception.DuplicateParcelException;
import com.huertosurbanos.parcelas.exception.ParcelNotFoundException;
import com.huertosurbanos.parcelas.model.SoilType;
import com.huertosurbanos.parcelas.service.ParcelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParcelController.class)
class ParcelControllerTest {

    private static final String BASE_URL = "/api/v1/parcels";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParcelService parcelService;

    @Test
    void create_returns201AndLocationHeader() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        ParcelResponse response = new ParcelResponse(
                id,
                "Huerto Centro",
                19.4326,
                -99.1332,
                SoilType.LIMOSO,
                "Ana Lopez",
                Instant.parse("2026-10-14T18:00:00Z")
        );
        when(parcelService.create(any())).thenReturn(response);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Huerto Centro",
                                  "latitude": 19.4326,
                                  "longitude": -99.1332,
                                  "soilType": "LIMOSO",
                                  "ownerOrLead": "Ana Lopez"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(BASE_URL + "/" + id)))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Huerto Centro"))
                .andExpect(jsonPath("$.soilType").value("LIMOSO"));
    }

    @Test
    void create_withInvalidFields_returns400WithFieldErrors() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "ab",
                                  "latitude": 120,
                                  "longitude": -200,
                                  "soilType": "ARENOSO",
                                  "ownerOrLead": "x"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Los datos enviados no son válidos"))
                .andExpect(jsonPath("$.path").value(BASE_URL))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("name")))
                .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("latitude")))
                .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("longitude")))
                .andExpect(jsonPath("$.fieldErrors[*].field").value(hasItem("ownerOrLead")));
    }

    @Test
    void findById_whenMissing_returns404() throws Exception {
        UUID id = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(parcelService.findById(id)).thenThrow(new ParcelNotFoundException(id));

        mockMvc.perform(get(BASE_URL + "/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No se encontró la parcela con id " + id))
                .andExpect(jsonPath("$.path").value(BASE_URL + "/" + id))
                .andExpect(jsonPath("$.fieldErrors").doesNotExist());
    }

    @Test
    void create_whenDuplicate_returns409() throws Exception {
        when(parcelService.create(any()))
                .thenThrow(new DuplicateParcelException("Huerto Centro", "Ana Lopez"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Huerto Centro",
                                  "latitude": 19.4326,
                                  "longitude": -99.1332,
                                  "soilType": "LIMOSO",
                                  "ownerOrLead": "Ana Lopez"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value(
                        "Ya existe una parcela con el nombre 'Huerto Centro' y el responsable 'Ana Lopez'"))
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }

    @Test
    void create_withMalformedJson_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ name: sin comillas }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "El cuerpo de la petición está malformado o contiene datos con formato inválido"))
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }

    @Test
    void create_withInvalidSoilType_returns400() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Huerto Centro",
                                  "latitude": 19.4326,
                                  "longitude": -99.1332,
                                  "soilType": "VOLCANICO",
                                  "ownerOrLead": "Ana Lopez"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "El tipo de suelo no es válido. Valores permitidos: ARCILLOSO, LIMOSO, ARENOSO"))
                .andExpect(jsonPath("$.path").value(BASE_URL));
    }
}
