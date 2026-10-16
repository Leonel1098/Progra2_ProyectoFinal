package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.InfraccionDetalle;
import com.infracciones.apiinfraccionesvehiculares.Service.InfraccionServiceLayer;
import com.infracciones.apiinfraccionesvehiculares.Model.Sesion;
import com.infracciones.apiinfraccionesvehiculares.Service.SeguridadService;
import com.infracciones.apiinfraccionesvehiculares.Service.SesionService;

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
    private final SeguridadService seguridad;

    public InfraccionServiceImpl(InfraccionServiceLayer service, SeguridadService seguridad) {
        this.service = service;
        this.seguridad = seguridad;
    }

    @Override
    public List<InfraccionDetalle> listarInfracciones() {
        seguridad.requerirRol(SeguridadService.ADMIN);
        return service.listAll();
    }

    @Override
    public InfraccionDetalle consultarPorNumero(String numeroInfraccion) {
        Sesion sesion = seguridad.requerirSesion();
        InfraccionDetalle infraccion = service.getByNumero(numeroInfraccion);
        seguridad.requerirPropietarioSiCiudadano(sesion, infraccion.getDpiPersona());
        return infraccion;
    }

    @Override
    public List<InfraccionDetalle> consultarPorPlaca(String placa) {
        Sesion sesion = seguridad.requerirSesion();
        List<InfraccionDetalle> lista = service.listByPlaca(placa);
        if (SeguridadService.CIUDADANO.equalsIgnoreCase(sesion.getNombreRol())) {
            return lista.stream()
                    .filter(i -> sesion.getDpiPersona().equals(i.getDpiPersona()))
                    .toList();
        }
        return lista;
    }

    @Override
    public List<InfraccionDetalle> consultarPorDpi(String dpi) {
        Sesion sesion = seguridad.requerirSesion();
        seguridad.requerirPropietarioSiCiudadano(sesion, dpi);
        return service.listByDpi(dpi);
    }

    @Override
    public InfraccionDetalle registrarInfraccion(String numeroInfraccion, String fechaInfraccion,
                                                 String direccionInfraccion, String dpiPersona,
                                                 String placaVehiculo, String usuarioAgente,
                                                 String codigoTipoInfraccion) {
        Sesion sesion = seguridad.requerirRol(SeguridadService.ADMIN, SeguridadService.AGENTE);
        // El agente sale del token; se ignora lo que envíe el cliente.
        return service.registrarInfraccion(
                numeroInfraccion, fechaInfraccion, direccionInfraccion,
                dpiPersona, placaVehiculo, sesion.getUsuario(), codigoTipoInfraccion);
    }

    @Override
    public InfraccionDetalle anularInfraccion(String numeroInfraccion) {
        seguridad.requerirRol(SeguridadService.ADMIN);
        return service.anularInfraccion(numeroInfraccion);
    }
}