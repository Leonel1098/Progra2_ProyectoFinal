package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.*;
import com.infracciones.apiinfraccionesvehiculares.Model.*;
import com.infracciones.apiinfraccionesvehiculares.Repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InfraccionServiceLayer {

    private final InfraccionRepository infraccionRepository;
    private final PersonaRepository personaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoInfraccionRepository tipoInfraccionRepository;

    public InfraccionServiceLayer(InfraccionRepository infraccionRepository,
                                  PersonaRepository personaRepository,
                                  VehiculoRepository vehiculoRepository,
                                  UsuarioRepository usuarioRepository,
                                  TipoInfraccionRepository tipoInfraccionRepository) {
        this.infraccionRepository = infraccionRepository;
        this.personaRepository = personaRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoInfraccionRepository = tipoInfraccionRepository;
    }

    public List<InfraccionDetalle> listAll() {
        return infraccionRepository.findAll();
    }

    public InfraccionDetalle getByNumero(String numeroInfraccion) {
        return infraccionRepository.findByNumero(numeroInfraccion)
                .orElseThrow(() -> new InfraccionNoEncontradaException(
                        "No existe una infracción con número: " + numeroInfraccion));
    }

    public List<InfraccionDetalle> listByPlaca(String placa) {
        if (placa == null || !vehiculoRepository.existsByPlaca(placa.trim())) {
            throw new VehiculoNoEncontradoException("No existe un vehículo con placa: " + placa);
        }
        return infraccionRepository.findByPlaca(placa.trim());
    }

    public List<InfraccionDetalle> listByDpi(String dpi) {
        return infraccionRepository.findByDpi(dpi);
    }

    public InfraccionDetalle registrarInfraccion(String numeroInfraccion,
                                                 String fechaInfraccionStr,
                                                 String direccionInfraccion,
                                                 String dpiPersona,
                                                 String placaVehiculo,
                                                 String usuarioAgente,
                                                 String codigoTipoInfraccion) {
        if (numeroInfraccion == null || numeroInfraccion.isBlank()) {
            throw new IllegalArgumentException("El número de infracción es obligatorio.");
        }
        if (direccionInfraccion == null || direccionInfraccion.isBlank()) {
            throw new IllegalArgumentException("La dirección de la infracción es obligatoria.");
        }
        if (infraccionRepository.existsByNumero(numeroInfraccion)) {
            throw new InfraccionDuplicadaException(
                    "Ya existe una infracción con el número: " + numeroInfraccion);
        }

        LocalDate fecha = (fechaInfraccionStr != null && !fechaInfraccionStr.isBlank())
                ? LocalDate.parse(fechaInfraccionStr)
                : LocalDate.now();

        Persona persona = personaRepository.findByDpi(dpiPersona)
                .orElseThrow(() -> new PersonaNoEncontradaException(
                        "No existe persona con DPI: " + dpiPersona));

        Vehiculo vehiculo = vehiculoRepository.findByPlaca(placaVehiculo)
                .orElseThrow(() -> new VehiculoNoEncontradoException(
                        "No existe vehículo con placa: " + placaVehiculo));

        Usuario agente = usuarioRepository.findByUsername(usuarioAgente)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "No existe el usuario agente: " + usuarioAgente));

        TipoInfraccion tipo = tipoInfraccionRepository.findByCodigo(codigoTipoInfraccion)
                .orElseThrow(() -> new TipoInfraccionNoEncontradoException(
                        "No existe tipo de infracción con código: " + codigoTipoInfraccion));

        Integer idEstadoPendiente = infraccionRepository.findIdEstadoByNombre("Pendiente")
                .orElseThrow(() -> new IllegalStateException("Estado 'Pendiente' no configurado en la BD."));

        infraccionRepository.insert(
                numeroInfraccion,
                fecha,
                direccionInfraccion,
                tipo.getMonto(),
                persona.getIdPersona(),
                vehiculo.getIdVehiculo(),
                agente.getIdUsuario(),
                tipo.getIdTipoInfraccion(),
                idEstadoPendiente
        );

        return getByNumero(numeroInfraccion);
    }

    public InfraccionDetalle anularInfraccion(String numeroInfraccion) {
        InfraccionDetalle existente = getByNumero(numeroInfraccion);

        if ("Pagada".equalsIgnoreCase(existente.getEstado())) {
            throw new IllegalStateException("No se puede anular una infracción que ya fue pagada.");
        }
        if ("Anulada".equalsIgnoreCase(existente.getEstado())) {
            throw new IllegalStateException("La infracción ya está anulada.");
        }

        Integer idEstadoAnulada = infraccionRepository.findIdEstadoByNombre("Anulada")
                .orElseThrow(() -> new IllegalStateException("Estado 'Anulada' no configurado en la BD."));

        infraccionRepository.updateEstado(existente.getIdInfraccion(), idEstadoAnulada);
        return getByNumero(numeroInfraccion);
    }
}