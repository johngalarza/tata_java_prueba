package com.tcs.cuenta.dto;

import java.math.BigDecimal;

public record CuentaResponse(
        Long id,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldoActual,
        Boolean estado,
        String clienteId
) {
}