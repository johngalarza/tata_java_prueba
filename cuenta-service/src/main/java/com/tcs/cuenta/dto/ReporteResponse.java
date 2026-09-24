package com.tcs.cuenta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ReporteResponse(
        String clienteId,
        String numeroCuenta,
        String tipoCuenta,
        BigDecimal saldoActual,
        List<MovimientoResponse> movimientos
) {
}