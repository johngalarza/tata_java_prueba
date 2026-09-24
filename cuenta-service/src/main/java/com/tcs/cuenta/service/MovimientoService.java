package com.tcs.cuenta.service;

import com.tcs.cuenta.dto.MovimientoRequest;
import com.tcs.cuenta.dto.MovimientoResponse;
import com.tcs.cuenta.entity.Cuenta;
import com.tcs.cuenta.entity.Movimiento;
import com.tcs.cuenta.exception.InsufficientBalanceException;
import com.tcs.cuenta.exception.ResourceNotFoundException;
import com.tcs.cuenta.repository.CuentaRepository;
import com.tcs.cuenta.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;

    public MovimientoService(
            MovimientoRepository movimientoRepository,
            CuentaRepository cuentaRepository) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
    }

    @Transactional
    public MovimientoResponse crear(MovimientoRequest request) {

        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(request.numeroCuenta())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cuenta no encontrada"));

        BigDecimal valor = request.valor();

        if (request.tipoMovimiento().equalsIgnoreCase("Retiro")) {
            valor = valor.negate();
        }

        BigDecimal nuevoSaldo = cuenta.getSaldoActual().add(valor);

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Saldo no disponible");
        }

        cuenta.setSaldoActual(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = new Movimiento();
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setValor(valor);
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setCuenta(cuenta);

        movimiento = movimientoRepository.save(movimiento);

        return toResponse(movimiento);
    }

    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::toResponse)
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

    @Transactional
    public MovimientoResponse actualizar(Long id, MovimientoRequest request) {

        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movimiento no encontrado"));

        Cuenta cuenta = movimiento.getCuenta();

        BigDecimal valor = request.valor();

        if (request.tipoMovimiento().equalsIgnoreCase("Retiro")) {
            valor = valor.negate();
        }

        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setValor(valor);

        recalcularSaldo(cuenta);

        movimiento = movimientoRepository.save(movimiento);

        return toResponse(movimiento);
    }

    @Transactional
    public void eliminar(Long id) {

        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movimiento no encontrado"));

        Cuenta cuenta = movimiento.getCuenta();

        movimientoRepository.delete(movimiento);

        recalcularSaldo(cuenta);
    }

    private void recalcularSaldo(Cuenta cuenta) {

        List<Movimiento> movimientos =
                movimientoRepository.findByCuentaIdOrderByFechaAsc(cuenta.getId());

        BigDecimal saldo = cuenta.getSaldoInicial();

        for (Movimiento movimiento : movimientos) {
            saldo = saldo.add(movimiento.getValor());
            movimiento.setSaldo(saldo);
        }

        cuenta.setSaldoActual(saldo);
        cuentaRepository.save(cuenta);
    }
}