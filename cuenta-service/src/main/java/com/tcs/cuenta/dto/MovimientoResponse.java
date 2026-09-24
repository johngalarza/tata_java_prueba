package com.tcs.cuenta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoResponse(
        Long id,
        LocalDateTime fecha,
        String tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldo,
        String numeroCuenta
) {
}