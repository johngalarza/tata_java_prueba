package com.tcs.cliente.dto;

public record ClienteRequest(
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        String clienteId,
        String contrasena,
        Boolean estado
) {
}