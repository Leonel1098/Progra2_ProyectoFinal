package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.VehiculoEnUsoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.VehiculoNoEncontradoException;
import com.infracciones.apiinfraccionesvehiculares.Model.Vehiculo;
import com.infracciones.apiinfraccionesvehiculares.Repository.VehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository repository;

    public VehiculoService(VehiculoRepository repository) {
        this.repository = repository;
    }

    public List<Vehiculo> listAll() {
        return repository.findAll();
    }

    public Vehiculo getByPlaca(String placa) {
        return repository.findByPlaca(placa)
                .orElseThrow(() -> new VehiculoNoEncontradoException(
                        "No existe un vehículo con placa: " + placa));
    }

    public Vehiculo create(String placa, String marca, String modelo, String color) {
        validateBasicData(placa, marca, modelo, color);

        if (repository.existsByPlaca(placa)) {
            throw new IllegalArgumentException(
                    "Ya existe un vehículo registrado con la placa: " + placa);
        }

        Vehiculo nuevo = new Vehiculo();
        nuevo.setPlaca(placa);
        nuevo.setMarca(marca);
        nuevo.setModelo(modelo);
        nuevo.setColor(color);

        Integer idGenerado = repository.insert(nuevo);
        nuevo.setIdVehiculo(idGenerado);
        return nuevo;
    }

    public Vehiculo update(String placa, String marca, String modelo, String color) {
        validateBasicData(placa, marca, modelo, color);

        Vehiculo existente = getByPlaca(placa);
        existente.setMarca(marca);
        existente.setModelo(modelo);
        existente.setColor(color);

        repository.updateByPlaca(existente);
        return existente;
    }

    public void delete(String placa) {
        Vehiculo existente = getByPlaca(placa);

        if (repository.tieneInfraccionesAsociadas(existente.getPlaca())) {
            throw new VehiculoEnUsoException(
                    "No se puede eliminar el vehículo con placa '" + placa +
                            "' porque tiene infracciones asociadas.");
        }

        repository.deleteByPlaca(placa);
    }

    private void validateBasicData(String placa, String marca, String modelo, String color) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa es obligatoria.");
        }
        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("La marca es obligatoria.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo es obligatorio.");
        }
        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("El color es obligatorio.");
        }
    }
}