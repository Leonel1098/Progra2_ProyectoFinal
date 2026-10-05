package com.infracciones.apiinfraccionesvehiculares.Config;

import com.infracciones.apiinfraccionesvehiculares.soap.*;
import jakarta.xml.ws.Endpoint;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    public Endpoint personaEndpoint(
            Bus bus,
            PersonaSoapService personaSoapService
    ){
        EndpointImpl endpoint = new EndpointImpl(bus, personaSoapService);
        endpoint.publish("/personas");
        return endpoint;
    }

    @Bean
    public Endpoint usuarioEndpoint(
            Bus bus,
            UsuarioSoapService usuarioSoapService
    ){
        EndpointImpl endpoint = new EndpointImpl(bus, usuarioSoapService);
        endpoint.publish("/usuarios");
        return endpoint;
    }
    @Bean
    public Endpoint vehiculoEndpoint(
            Bus bus,
            VehiculoSoapService vehiculoSoapService
    ){
        EndpointImpl endpoint = new EndpointImpl(bus, vehiculoSoapService);
        endpoint.publish("/vehiculos");
        return endpoint;
    }
}