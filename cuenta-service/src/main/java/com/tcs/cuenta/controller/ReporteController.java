package com.tcs.cuenta.controller;

import com.tcs.cuenta.dto.ReporteResponse;
import com.tcs.cuenta.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping
    public List<ReporteResponse> generar(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        return reporteService.generar(
                clienteId,
                fechaInicio,
                fechaFin
        );
    }
}