package com.tcs.cuenta.service;

import com.tcs.cuenta.dto.CuentaRequest;
import com.tcs.cuenta.dto.CuentaResponse;
import com.tcs.cuenta.entity.Cuenta;
import com.tcs.cuenta.exception.ConflictException;
import com.tcs.cuenta.exception.ResourceNotFoundException;
import com.tcs.cuenta.repository.CuentaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public CuentaService(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public CuentaResponse crear(CuentaRequest request) {

        if (cuentaRepository.existsByNumeroCuenta(request.numeroCuenta())) {
            throw new ConflictException("El numero de cuenta ya existe");
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setNumeroCuenta(request.numeroCuenta());
        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setSaldoInicial(request.saldoInicial());
        cuenta.setSaldoActual(request.saldoInicial());
        cuenta.setEstado(request.estado());
        cuenta.setClienteId(request.clienteId());

        cuenta = cuentaRepository.save(cuenta);

        return toResponse(cuenta);
    }

    public List<CuentaResponse> listar() {
        return cuentaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CuentaResponse obtener(Long id) {

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cuenta no encontrada"));

        return toResponse(cuenta);
    }

    public CuentaResponse actualizar(Long id, CuentaRequest request) {

        Cuenta cuenta = cuentaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cuenta no encontrada"));

        cuenta.setNumeroCuenta(request.numeroCuenta());
        cuenta.setTipoCuenta(request.tipoCuenta());
        cuenta.setEstado(request.estado());
        cuenta.setClienteId(request.clienteId());

        cuenta = cuentaRepository.save(cuenta);

        return toResponse(cuenta);
    }

    public void eliminar(Long id) {

        if (!cuentaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cuenta no encontrada");
        }

        cuentaRepository.deleteById(id);
    }

    private CuentaResponse toResponse(Cuenta cuenta) {
        return new CuentaResponse(
                cuenta.getId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldoInicial(),
                cuenta.getSaldoActual(),
                cuenta.getEstado(),
                cuenta.getClienteId()
        );
    }
}