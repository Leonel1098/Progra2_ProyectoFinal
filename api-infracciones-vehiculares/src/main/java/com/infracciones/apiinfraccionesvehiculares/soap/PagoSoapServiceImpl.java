package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Pago;
import com.infracciones.apiinfraccionesvehiculares.Service.PagoService;
import com.infracciones.apiinfraccionesvehiculares.Model.Sesion;
import com.infracciones.apiinfraccionesvehiculares.Service.InfraccionServiceLayer;
import com.infracciones.apiinfraccionesvehiculares.Service.SeguridadService;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
        serviceName = "PagoSoapService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.PagoSoapService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class PagoSoapServiceImpl implements PagoSoapService {

    private final PagoService pagoService;
    private final InfraccionServiceLayer infraccionService;
    private final SeguridadService seguridad;

    public PagoSoapServiceImpl(PagoService pagoService, InfraccionServiceLayer infraccionService, SeguridadService seguridad) {
        this.pagoService = pagoService;
        this.infraccionService = infraccionService;
        this.seguridad = seguridad;

    }

    @Override
    public Pago calcularMontoAPagar(String numeroInfraccion) {
        Sesion sesion = seguridad.requerirRol(SeguridadService.CIUDADANO);
        seguridad.requerirPropietarioSiCiudadano(sesion,
                infraccionService.getByNumero(numeroInfraccion).getDpiPersona());
        return pagoService.calcularMontoAPagar(numeroInfraccion);
    }

    @Override
    public Pago aplicarPago(String numeroInfraccion) {
        Sesion sesion = seguridad.requerirRol(SeguridadService.CIUDADANO);
        seguridad.requerirPropietarioSiCiudadano(sesion,
                infraccionService.getByNumero(numeroInfraccion).getDpiPersona());
        return pagoService.aplicarPago(numeroInfraccion);
    }

    @Override
    public Pago consultarPagoPorInfraccion(String numeroInfraccion) {
        Sesion sesion = seguridad.requerirRol(SeguridadService.ADMIN, SeguridadService.CIUDADANO);
        seguridad.requerirPropietarioSiCiudadano(sesion,
                infraccionService.getByNumero(numeroInfraccion).getDpiPersona());
        return pagoService.getByNumeroInfraccion(numeroInfraccion);
    }

    @Override
    public List<Pago> listarPagos() {
        seguridad.requerirRol(SeguridadService.ADMIN);
        return pagoService.listAll();
    }
}
