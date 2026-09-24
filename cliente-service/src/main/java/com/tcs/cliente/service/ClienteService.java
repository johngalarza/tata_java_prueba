package com.tcs.cliente.service;

import com.tcs.cliente.dto.ClienteRequest;
import com.tcs.cliente.dto.ClienteResponse;
import com.tcs.cliente.entity.Cliente;
import com.tcs.cliente.exception.ConflictException;
import com.tcs.cliente.exception.ResourceNotFoundException;
import com.tcs.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public ClienteResponse crear(ClienteRequest request) {

        if (clienteRepository.existsByClienteId(request.clienteId())) {
            throw new ConflictException("El clienteId ya existe");
        }

        if (clienteRepository.existsByIdentificacion(request.identificacion())) {
            throw new ConflictException("La identificacion ya existe");
        }

        Cliente cliente = new Cliente();

        cliente.setNombre(request.nombre());
        cliente.setGenero(request.genero());
        cliente.setEdad(request.edad());
        cliente.setIdentificacion(request.identificacion());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setClienteId(request.clienteId());
        cliente.setContrasena(request.contrasena());
        cliente.setEstado(request.estado());

        cliente = clienteRepository.save(cliente);

        return toResponse(cliente);
    }

    public List<ClienteResponse> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ClienteResponse obtener(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        return toResponse(cliente);
    }

    public ClienteResponse actualizar(Long id, ClienteRequest request) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        cliente.setNombre(request.nombre());
        cliente.setGenero(request.genero());
        cliente.setEdad(request.edad());
        cliente.setIdentificacion(request.identificacion());
        cliente.setDireccion(request.direccion());
        cliente.setTelefono(request.telefono());
        cliente.setClienteId(request.clienteId());
        cliente.setContrasena(request.contrasena());
        cliente.setEstado(request.estado());

        cliente = clienteRepository.save(cliente);

        return toResponse(cliente);
    }

    public void eliminar(Long id) {

        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado");
        }

        clienteRepository.deleteById(id);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getClienteId(),
                cliente.getEstado()
        );
    }
}