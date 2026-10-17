package com.infracciones.apiinfraccionesvehiculares.Service;

import com.infracciones.apiinfraccionesvehiculares.Excepcion.InfraccionNoEncontradaException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.PagoNoEncontradoException;
import com.infracciones.apiinfraccionesvehiculares.Excepcion.ReglaDescuentoActivaException;
import com.infracciones.apiinfraccionesvehiculares.Model.InfraccionDetalle;
import com.infracciones.apiinfraccionesvehiculares.Model.Pago;
import com.infracciones.apiinfraccionesvehiculares.Model.ReglaDescuento;
import com.infracciones.apiinfraccionesvehiculares.Repository.InfraccionRepository;
import com.infracciones.apiinfraccionesvehiculares.Repository.PagoRepository;
import com.infracciones.apiinfraccionesvehiculares.Repository.ReglaDescuentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final InfraccionRepository infraccionRepository;
    private final ReglaDescuentoRepository reglaDescuentoRepository;

    public PagoService(PagoRepository pagoRepository,
                       InfraccionRepository infraccionRepository,
                       ReglaDescuentoRepository reglaDescuentoRepository) {
        this.pagoRepository = pagoRepository;
        this.infraccionRepository = infraccionRepository;
        this.reglaDescuentoRepository = reglaDescuentoRepository;
    }

    public List<Pago> listAll() {
        return pagoRepository.findAll();
    }

    public Pago getByNumeroInfraccion(String numeroInfraccion) {
        return pagoRepository.findByNumeroInfraccion(numeroInfraccion)
                .orElseThrow(() -> new PagoNoEncontradoException(
                        "No existe un pago para la infracción: " + numeroInfraccion));
    }

    // Simula el pago sin guardar nada: sirve para mostrar el monto antes de confirmar.
    public Pago calcularMontoAPagar(String numeroInfraccion) {
        InfraccionDetalle infraccion = getInfraccionPagable(numeroInfraccion);
        return calcular(infraccion, LocalDate.now());
    }

    // El pago y el cambio de estado van en una sola transacción: o se hacen ambos o ninguno.
    @Transactional
    public Pago aplicarPago(String numeroInfraccion) {
        InfraccionDetalle infraccion = getInfraccionPagable(numeroInfraccion);

        Integer idEstadoPagada = infraccionRepository.findIdEstadoByNombre("Pagada")
                .orElseThrow(() -> new IllegalStateException("El estado 'Pagada' no está configurado en la BD."));

        Pago pago = calcular(infraccion, LocalDate.now());

        Integer idGenerado = pagoRepository.insert(pago);
        infraccionRepository.updateEstado(infraccion.getIdInfraccion(), idEstadoPagada);

        pago.setIdPago(idGenerado);
        return pago;
    }

    private InfraccionDetalle getInfraccionPagable(String numeroInfraccion) {
        if (numeroInfraccion == null || numeroInfraccion.isBlank()) {
            throw new IllegalArgumentException("El número de infracción es obligatorio.");
        }

        InfraccionDetalle infraccion = infraccionRepository.findByNumero(numeroInfraccion)
                .orElseThrow(() -> new InfraccionNoEncontradaException(
                        "No existe una infracción con número: " + numeroInfraccion));

        if ("Anulada".equalsIgnoreCase(infraccion.getEstado())) {
            throw new IllegalStateException(
                    "La infracción " + numeroInfraccion + " está anulada y no puede pagarse.");
        }
        if ("Pagada".equalsIgnoreCase(infraccion.getEstado())
                || pagoRepository.existsByIdInfraccion(infraccion.getIdInfraccion())) {
            throw new IllegalStateException("La infracción " + numeroInfraccion + " ya fue pagada.");
        }
        return infraccion;
    }

    private Pago calcular(InfraccionDetalle infraccion, LocalDate fechaPago) {
        BigDecimal montoInicial = infraccion.getMontoOriginal();
        BigDecimal porcentaje = BigDecimal.ZERO;

        // Una infracción vencida se paga completa; las demás dependen de la regla activa.
        if (!"Vencida".equalsIgnoreCase(infraccion.getEstado())) {
            ReglaDescuento regla = reglaDescuentoRepository.findActiva()
                    .orElseThrow(() -> new ReglaDescuentoActivaException(
                            "No hay una regla de descuento activa configurada."));

            long diasTranscurridos = ChronoUnit.DAYS.between(
                    LocalDate.parse(infraccion.getFechaInfraccion()), fechaPago);

            if (diasTranscurridos <= regla.getDiasLimite()) {
                porcentaje = regla.getPorcentaje();
            }
        }

        BigDecimal montoDescuento = montoInicial
                .multiply(porcentaje)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        Pago pago = new Pago();
        pago.setIdInfraccion(infraccion.getIdInfraccion());
        pago.setNumeroInfraccion(infraccion.getNumeroInfraccion());
        pago.setMontoInicial(montoInicial);
        pago.setPorcentajeDescuento(porcentaje);
        pago.setMontoDescuento(montoDescuento);
        pago.setMontoPagado(montoInicial.subtract(montoDescuento));
        pago.setFechaPago(fechaPago);
        return pago;
    }
}
