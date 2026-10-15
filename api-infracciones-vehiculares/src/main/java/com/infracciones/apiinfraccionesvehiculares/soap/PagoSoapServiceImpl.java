package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Pago;
import com.infracciones.apiinfraccionesvehiculares.Service.PagoService;
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

    public PagoSoapServiceImpl(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @Override
    public Pago calcularMontoAPagar(String numeroInfraccion) {
        return pagoService.calcularMontoAPagar(numeroInfraccion);
    }

    @Override
    public Pago aplicarPago(String numeroInfraccion) {
        return pagoService.aplicarPago(numeroInfraccion);
    }

    @Override
    public Pago consultarPagoPorInfraccion(String numeroInfraccion) {
        return pagoService.getByNumeroInfraccion(numeroInfraccion);
    }

    @Override
    public List<Pago> listarPagos() {
        return pagoService.listAll();
    }
}
