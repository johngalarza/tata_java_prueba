package com.tcs.cuenta.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CuentaRequest(

        @NotBlank(message = "El numero de cuenta es obligatorio")
        String numeroCuenta,

        @NotBlank(message = "El tipo de cuenta es obligatorio")
        String tipoCuenta,

        @NotNull(message = "El saldo inicial es obligatorio")
        @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo")
        BigDecimal saldoInicial,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado,

        @NotBlank(message = "El clienteId es obligatorio")
        String clienteId
) {
}