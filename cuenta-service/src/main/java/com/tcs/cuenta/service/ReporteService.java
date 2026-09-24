package com.tcs.cuenta.service;

import com.tcs.cuenta.dto.MovimientoResponse;
import com.tcs.cuenta.dto.ReporteResponse;
import com.tcs.cuenta.entity.Cuenta;
import com.tcs.cuenta.entity.Movimiento;
import com.tcs.cuenta.repository.CuentaRepository;
import com.tcs.cuenta.repository.MovimientoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public ReporteService(
            CuentaRepository cuentaRepository,
            MovimientoRepository movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public List<ReporteResponse> generar(
            String clienteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

        return cuentas.stream()
                .map(cuenta -> {

                    List<MovimientoResponse> movimientos =
                            movimientoRepository
                                    .findByCuentaNumeroCuentaAndFechaBetweenOrderByFechaAsc(
                                            cuenta.getNumeroCuenta(),
                                            fechaInicio,
                                            fechaFin)
                                    .stream()
                                    .map(this::toResponse)
                                    .toList();

                    return new ReporteResponse(
                            cuenta.getClienteId(),
                            cuenta.getNumeroCuenta(),
                            cuenta.getTipoCuenta(),
                            cuenta.getSaldoActual(),
                            movimientos
                    );
                })
                .toList();
    }

    private MovimientoResponse toResponse(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipoMovimiento(),
                movimiento.getValor(),
                movimiento.getSaldo(),
                movimiento.getCuenta().getNumeroCuenta()
        );
    }
}