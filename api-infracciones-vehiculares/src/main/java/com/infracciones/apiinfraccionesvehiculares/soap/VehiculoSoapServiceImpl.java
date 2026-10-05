package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Vehiculo;
import com.infracciones.apiinfraccionesvehiculares.Service.VehiculoService;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
        serviceName = "VehiculoSoapService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.VehiculoSoapService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class VehiculoSoapServiceImpl implements VehiculoSoapService {

    private final VehiculoService vehiculoService;

    public VehiculoSoapServiceImpl(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @Override
    public List<Vehiculo> listarVehiculos() {
        return vehiculoService.listAll();
    }

    @Override
    public Vehiculo consultarVehiculoPorPlaca(String placa) {
        return vehiculoService.getByPlaca(placa);
    }

    @Override
    public Vehiculo crearVehiculo(String placa, String marca, String modelo, String color) {
        return vehiculoService.create(placa, marca, modelo, color);
    }

    @Override
    public Vehiculo actualizarVehiculo(String placa, String marca, String modelo, String color) {
        return vehiculoService.update(placa, marca, modelo, color);
    }

    @Override
    public void eliminarVehiculo(String placa) {
        vehiculoService.delete(placa);
    }
}