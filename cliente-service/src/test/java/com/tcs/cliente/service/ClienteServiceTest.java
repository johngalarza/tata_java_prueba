package com.tcs.cliente.service;

import com.tcs.cliente.dto.ClienteRequest;
import com.tcs.cliente.dto.ClienteResponse;
import com.tcs.cliente.entity.Cliente;
import com.tcs.cliente.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void deberiaCrearCliente() {

        ClienteRequest request = new ClienteRequest(
                "Jose Lema",
                "M",
                30,
                "0102030405",
                "Otavalo",
                "0987654321",
                "jose",
                "1234",
                true
        );

        when(clienteRepository.existsByClienteId("jose"))
                .thenReturn(false);

        when(clienteRepository.existsByIdentificacion("0102030405"))
                .thenReturn(false);

        Cliente cliente = new Cliente();
        cliente.setClienteId("jose");
        cliente.setNombre("Jose Lema");
        cliente.setIdentificacion("0102030405");
        cliente.setEstado(true);

        when(clienteRepository.save(any(Cliente.class)))
                .thenReturn(cliente);

        ClienteResponse response = clienteService.crear(request);

        assertEquals("Jose Lema", response.nombre());
        assertEquals("jose", response.clienteId());
        assertTrue(response.estado());

        verify(clienteRepository).save(any(Cliente.class));
    }
}