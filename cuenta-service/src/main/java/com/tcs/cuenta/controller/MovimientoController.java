package com.tcs.cuenta.controller;

import com.tcs.cuenta.dto.MovimientoRequest;
import com.tcs.cuenta.dto.MovimientoResponse;
import com.tcs.cuenta.service.MovimientoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponse crear(
            @Valid @RequestBody MovimientoRequest request) {
        return movimientoService.crear(request);
    }

    @GetMapping
    public List<MovimientoResponse> listar() {
        return movimientoService.listar();
    }
}