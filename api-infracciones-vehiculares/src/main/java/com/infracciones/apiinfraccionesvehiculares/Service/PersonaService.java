package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.PersonaEnUsoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.PersonaNoEncontradaException;
import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
import com.infracciones.apiinfraccionesvehiculares.Repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository repository;

    public PersonaService(PersonaRepository repository) {
        this.repository = repository;
    }

    public List<Persona> listAll() {
        return repository.findAll();
    }

    public Persona getByDpi(String dpi) {
        return repository.findByDpi(dpi)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "No existe una persona con DPI: " + dpi));
    }

    public Persona create(String dpi, String nombre, String apellido, String telefono, String direccion) {
        validateBasicData(dpi, nombre, apellido, telefono, direccion);

        if (repository.existsByDpi(dpi)) {
            throw new IllegalArgumentException("Ya existe una persona registrada con el DPI: " + dpi);
        }

        Persona nueva = new Persona();
        nueva.setDpiPersona(dpi);
        nueva.setNombrePersona(nombre);
        nueva.setApellidoPersona(apellido);
        nueva.setTelefonoPersona(telefono);
        nueva.setDireccionPersona(direccion);

        Integer idGenerado = repository.insert(nueva);
        nueva.setIdPersona(idGenerado);
        return nueva;
    }

    public Persona update(String dpi, String nombre, String apellido, String telefono, String direccion) {
        validateBasicData(dpi, nombre, apellido, telefono, direccion);

        Persona existente = getByDpi(dpi);

        existente.setNombrePersona(nombre);
        existente.setApellidoPersona(apellido);
        existente.setTelefonoPersona(telefono);
        existente.setDireccionPersona(direccion);

        repository.updateByDpi(existente);
        return existente;
    }

    public void delete(String dpi) {
        Persona existente = getByDpi(dpi);

        if (repository.tieneUsuarioAsociado(existente.getDpiPersona())) {
            throw new PersonaEnUsoException(
                    "No se puede eliminar a '" + existente.getNombrePersona() + " " + existente.getApellidoPersona() +
                            "' porque tiene una cuenta de usuario asociada.");
        }

        if (repository.tieneInfraccionesAsociadas(existente.getDpiPersona())) {
            throw new PersonaEnUsoException(
                    "No se puede eliminar a '" + existente.getNombrePersona() + " " + existente.getApellidoPersona() +
                            "' porque tiene infracciones registradas a su nombre.");
        }

        repository.deleteByDpi(dpi);
    }

    private void validateBasicData(String dpi, String nombre, String apellido, String telefono, String direccion) {
        if (dpi == null || dpi.isBlank()) {
            throw new IllegalArgumentException("El DPI es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio.");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }
    }
}