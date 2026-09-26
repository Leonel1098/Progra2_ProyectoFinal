package com.infracciones.apiinfraccionesvehiculares.soap;

import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
import com.infracciones.apiinfraccionesvehiculares.Service.PersonaService;
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

    public PersonaSoapServiceImpl(PersonaService personaService) {
        this.personaService = personaService;
    }

    @Override
    public List<Persona> listarPersonas() {
        return personaService.listAll();
    }

    @Override
    public Persona consultarPersonaPorDpi(String dpi) {
        return personaService.getByDpi(dpi);
    }

    @Override
    public Persona crearPersona(String dpi, String nombre, String apellido, String telefono, String direccion) {
        return personaService.create(dpi, nombre, apellido, telefono, direccion);
    }

    @Override
    public Persona actualizarPersona(String dpi, String nombre, String apellido, String telefono, String direccion) {
        return personaService.update(dpi, nombre, apellido, telefono, direccion);
    }

    @Override
    public void eliminarPersona(String dpi) {
        personaService.delete(dpi);
    }
}