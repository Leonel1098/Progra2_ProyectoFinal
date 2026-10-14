package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.InfraccionDetalle;
import com.infracciones.apiinfraccionesvehiculares.Service.InfraccionServiceLayer;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
        serviceName = "InfraccionService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.InfraccionService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class InfraccionServiceImpl implements InfraccionService {

    private final InfraccionServiceLayer service;

    public InfraccionServiceImpl(InfraccionServiceLayer service) {
        this.service = service;
    }

    @Override
    public List<InfraccionDetalle> listarInfracciones() {
        return service.listAll();
    }

    @Override
    public InfraccionDetalle consultarPorNumero(String numeroInfraccion) {
        return service.getByNumero(numeroInfraccion);
    }

    @Override
    public List<InfraccionDetalle> consultarPorPlaca(String placa) {
        return service.listByPlaca(placa);
    }

    @Override
    public List<InfraccionDetalle> consultarPorDpi(String dpi) {
        return service.listByDpi(dpi);
    }

    @Override
    public InfraccionDetalle registrarInfraccion(String numeroInfraccion, String fechaInfraccion,
                                                 String direccionInfraccion, String dpiPersona,
                                                 String placaVehiculo, String usuarioAgente,
                                                 String codigoTipoInfraccion) {
        return service.registrarInfraccion(
                numeroInfraccion, fechaInfraccion, direccionInfraccion,
                dpiPersona, placaVehiculo, usuarioAgente, codigoTipoInfraccion);
    }

    @Override
    public InfraccionDetalle anularInfraccion(String numeroInfraccion) {
        return service.anularInfraccion(numeroInfraccion);
    }
}