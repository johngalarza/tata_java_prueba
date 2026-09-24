package com.tcs.cuenta.repository;

import com.tcs.cuenta.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByCuentaNumeroCuentaOrderByFechaDesc(String numeroCuenta);
}