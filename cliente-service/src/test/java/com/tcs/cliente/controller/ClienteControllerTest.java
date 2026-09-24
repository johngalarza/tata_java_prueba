package com.tcs.cliente.controller;

import com.tcs.cliente.dto.ClienteResponse;
import com.tcs.cliente.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService clienteService;

    @Test
    void deberiaCrearCliente() throws Exception {

        ClienteResponse response = new ClienteResponse(
                1L,
                "Jose Lema",
                "M",
                30,
                "0102030405",
                "Otavalo",
                "0987654321",
                "jose",
                true
        );

        when(clienteService.crear(any()))
                .thenReturn(response);

        String json = """
                {
                    "nombre": "Jose Lema",
                    "genero": "M",
                    "edad": 30,
                    "identificacion": "0102030405",
                    "direccion": "Otavalo",
                    "telefono": "0987654321",
                    "clienteId": "jose",
                    "contrasena": "1234",
                    "estado": true
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Jose Lema"))
                .andExpect(jsonPath("$.clienteId").value("jose"));
    }

    @Test
    void deberiaRechazarClienteSinNombre() throws Exception {

        String json = """
                {
                    "nombre": "",
                    "genero": "M",
                    "edad": 30,
                    "identificacion": "0102030405",
                    "direccion": "Otavalo",
                    "telefono": "0987654321",
                    "clienteId": "jose",
                    "contrasena": "1234",
                    "estado": true
                }
                """;

        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre")
                        .value("El nombre es obligatorio"));
    }
}