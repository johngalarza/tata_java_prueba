package com.tcs.cuenta.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MovimientoRequest(

        @NotBlank(message = "El numero de cuenta es obligatorio")
        String numeroCuenta,

        @NotBlank(message = "El tipo de movimiento es obligatorio")
        String tipoMovimiento,

        @NotNull(message = "El valor es obligatorio")
        @DecimalMin(value = "0.01", message = "El valor debe ser mayor a cero")
        BigDecimal valor
) {
}