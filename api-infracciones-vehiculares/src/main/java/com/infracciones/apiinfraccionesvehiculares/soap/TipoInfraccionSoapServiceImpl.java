package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.TipoInfraccion;
import com.infracciones.apiinfraccionesvehiculares.Service.SeguridadService;
import com.infracciones.apiinfraccionesvehiculares.Service.TipoInfraccionService;

import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@WebService(
        serviceName = "TipoInfraccionSoapService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.TipoInfraccionSoapService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class TipoInfraccionSoapServiceImpl implements TipoInfraccionSoapService {

    private final TipoInfraccionService tipoInfraccionService;
    private final SeguridadService seguridad;

    public TipoInfraccionSoapServiceImpl(TipoInfraccionService tipoInfraccionService, SeguridadService seguridad) {
        this.tipoInfraccionService = tipoInfraccionService;
        this.seguridad = seguridad;
    }

    @Override
    public List<TipoInfraccion> listarTipoInfracciones() {
        seguridad.requerirSesion();
        return tipoInfraccionService.listAll();
    }

    @Override
    public TipoInfraccion consultarTipoInfraccionPorId(Integer id) {
        seguridad.requerirSesion();
        return tipoInfraccionService.findByID(id);
    }

    @Override
    public TipoInfraccion consultarTipoInfraccionPorCodigo(String codigo) {
        seguridad.requerirSesion();
        return tipoInfraccionService.findByCode(codigo);
    }

    @Override
    public TipoInfraccion crearTipoInfraccion(String codigo, String nombre, String descripcion, BigDecimal monto) {
        seguridad.requerirRol(SeguridadService.ADMIN);
        return tipoInfraccionService.create(codigo, nombre, descripcion, monto);
    }

    @Override
    public TipoInfraccion actualizarTipoInfraccion(Integer id, String codigo, String nombre, String descripcion, BigDecimal monto) {
        seguridad.requerirRol(SeguridadService.ADMIN);
        return tipoInfraccionService.update(id, codigo, nombre, descripcion, monto);
    }

    @Override
    public void eliminarTipoInfraccion(Integer id) {
        seguridad.requerirRol(SeguridadService.ADMIN);
        tipoInfraccionService.delete(id);
    }
}