package com.tcs.cuenta.integration;

import com.tcs.cuenta.entity.Cuenta;
import com.tcs.cuenta.repository.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CuentaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CuentaRepository cuentaRepository;

    @Test
    void deberiaRegistrarDepositoYActualizarSaldo() throws Exception {

        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("TEST-F6-001");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(new java.math.BigDecimal("100.00"));
        cuenta.setSaldoActual(new java.math.BigDecimal("100.00"));
        cuenta.setEstado(true);
        cuenta.setClienteId("test-f6");

        cuentaRepository.save(cuenta);

        String json = """
                {
                    "numeroCuenta": "TEST-F6-001",
                    "tipoMovimiento": "Deposito",
                    "valor": 50
                }
                """;

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("TEST-F6-001"))
                .andExpect(jsonPath("$.valor").value(50))
                .andExpect(jsonPath("$.saldo").value(150));
    }
}