package com.infracciones.apiinfraccionesvehiculares.Config;

import com.infracciones.apiinfraccionesvehiculares.Service.TipoInfraccionService;
import com.infracciones.apiinfraccionesvehiculares.soap.TipoInfraccionSoapService;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.infracciones.apiinfraccionesvehiculares.soap.InfraccionService;

@Configuration
public class SoapConfig {

    @Bean
    public Endpoint infraccionEndpoint(
            Bus bus,
            InfraccionService infraccionService
    ) {
        EndpointImpl endpoint = new EndpointImpl(bus, infraccionService);
        endpoint.publish("/infracciones");
        return endpoint;
    }
    @Bean
    public Endpoint tipoInfraccionEndpoint(
            Bus bus,
            TipoInfraccionSoapService tipoInfraccionSoapService
    ){
        EndpointImpl endpoint = new EndpointImpl(bus, tipoInfraccionSoapService);
        endpoint.publish("/tipoinfracciones");
        return endpoint;
    }
}