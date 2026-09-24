package com.tcs.cliente.dto;

import jakarta.validation.constraints.*;

public record ClienteRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        String genero,

        @NotNull(message = "La edad es obligatoria")
        @Min(value = 0, message = "La edad no puede ser negativa")
        Integer edad,

        @NotBlank(message = "La identificacion es obligatoria")
        String identificacion,

        String direccion,

        String telefono,

        @NotBlank(message = "El clienteId es obligatorio")
        String clienteId,

        @NotBlank(message = "La contrasena es obligatoria")
        String contrasena,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado
) {
}