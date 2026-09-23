package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.TipoInfraccionEnUsoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.TipoInfraccionNoEncontradoException;
import com.infracciones.apiinfraccionesvehiculares.Model.TipoInfraccion;
import com.infracciones.apiinfraccionesvehiculares.Repository.TipoInfraccionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TipoInfraccionService {

    private final TipoInfraccionRepository repository;

    public TipoInfraccionService(TipoInfraccionRepository repository) {
        this.repository = repository;
    }

    public List<TipoInfraccion> listAll() {
        return repository.findAll();
    }

    public TipoInfraccion findByID(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new TipoInfraccionNoEncontradoException(
                        "No existe un tipo de infracción con id: " + id));
    }

    public TipoInfraccion findByCode(String codigo) {
        return repository.findByCodigo(codigo)
                .orElseThrow(() -> new TipoInfraccionNoEncontradoException(
                        "No existe un tipo de infracción con código: " + codigo));
    }

    public TipoInfraccion create(String codigo, String nombre, String descripcion, BigDecimal monto) {
        validateBasicData(codigo, nombre, monto);

        if (repository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Ya existe un tipo de infracción con el código: " + codigo);
        }

        TipoInfraccion nuevo = new TipoInfraccion();
        nuevo.setCodigo(codigo);
        nuevo.setNombre(nombre);
        nuevo.setDescripcion(descripcion);
        nuevo.setMonto(monto);

        Integer idGenerado = repository.insert(nuevo);
        nuevo.setIdTipoInfraccion(idGenerado);
        return nuevo;
    }

    public TipoInfraccion update(Integer id, String codigo, String nombre, String descripcion, BigDecimal monto) {
        validateBasicData(codigo, nombre, monto);

        TipoInfraccion existente = findByID(id);

        if (!existente.getCodigo().equalsIgnoreCase(codigo) && repository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException("Ya existe otro tipo de infracción con el código: " + codigo);
        }

        existente.setCodigo(codigo);
        existente.setNombre(nombre);
        existente.setDescripcion(descripcion);
        existente.setMonto(monto);

        repository.update(existente);
        return existente;
    }

    public void delete(Integer id) {
        TipoInfraccion existente = findByID(id);

        if (repository.associatedViolations(existente.getIdTipoInfraccion())) {
            throw new TipoInfraccionEnUsoException(
                    "No se puede eliminar el tipo de infracción '" + existente.getNombre() +
                            "' porque tiene infracciones registradas asociadas.");
        }

        repository.deleteById(id);
    }

    private void validateBasicData(String codigo, String nombre, BigDecimal monto) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del tipo de infracción es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del tipo de infracción es obligatorio.");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
    }
}