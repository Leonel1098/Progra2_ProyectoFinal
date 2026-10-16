package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
import com.infracciones.apiinfraccionesvehiculares.Service.PersonaService;
import com.infracciones.apiinfraccionesvehiculares.Service.SeguridadService;
import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@WebService(
        serviceName = "PersonaSoapService",
        endpointInterface = "com.infracciones.apiinfraccionesvehiculares.soap.PersonaSoapService",
        targetNamespace = "https://soap.apiinfraccionesvehiculares.infracciones.com/"
)
public class PersonaSoapServiceImpl implements PersonaSoapService {

    private final PersonaService personaService;
    private final SeguridadService seguridad;

    public PersonaSoapServiceImpl(PersonaService personaService, SeguridadService seguridad) {
        this.personaService = personaService;
        this.seguridad = seguridad;
    }

    @Override
    public List<Persona> listarPersonas() {
        seguridad.requerirRol(SeguridadService.ADMIN, SeguridadService.AGENTE);
        return personaService.listAll();
    }

    @Override
    public Persona consultarPersonaPorDpi(String dpi) {
        seguridad.requerirRol(SeguridadService.ADMIN, SeguridadService.AGENTE);
        return personaService.getByDpi(dpi);
    }

    @Override
    public Persona crearPersona(String dpi, String nombre, String apellido, String telefono, String direccion) {
        seguridad.requerirRol(SeguridadService.ADMIN, SeguridadService.AGENTE);
        return personaService.create(dpi, nombre, apellido, telefono, direccion);
    }

    @Override
    public Persona actualizarPersona(String dpi, String nombre, String apellido, String telefono, String direccion) {
        seguridad.requerirRol(SeguridadService.ADMIN);
        return personaService.update(dpi, nombre, apellido, telefono, direccion);
    }

    @Override
    public void eliminarPersona(String dpi) {
        seguridad.requerirRol(SeguridadService.ADMIN);
        personaService.delete(dpi);
    }
}