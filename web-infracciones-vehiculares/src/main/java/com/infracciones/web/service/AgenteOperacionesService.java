package com.infracciones.web.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgenteOperacionesService {

    private final SoapClient soap;
    private final String personasUrl;
    private final String vehiculosUrl;
    private final String infraccionesUrl;

    public AgenteOperacionesService(SoapClient soap,
                                    @Value("${soap.personas.url}") String personasUrl,
                                    @Value("${soap.vehiculos.url}") String vehiculosUrl,
                                    @Value("${soap.infracciones.url}") String infraccionesUrl) {
        this.soap = soap;
        this.personasUrl = personasUrl;
        this.vehiculosUrl = vehiculosUrl;
        this.infraccionesUrl = infraccionesUrl;
    }

    public List<Map<String, String>> listarPersonas(String token) {
        return invocar(personasUrl, "listarPersonas", token);
    }

    public List<Map<String, String>> consultarPersona(String dpi, String token) {
        return invocar(personasUrl, "consultarPersonaPorDpi", token, "dpi", dpi);
    }

    public List<Map<String, String>> crearPersona(String dpi, String nombre, String apellido,
                                                   String telefono, String direccion, String token) {
        return invocar(personasUrl, "crearPersona", token,
                "dpi", dpi, "nombre", nombre, "apellido", apellido,
                "telefono", telefono, "direccion", direccion);
    }

    public List<Map<String, String>> listarVehiculos(String token) {
        return invocar(vehiculosUrl, "listarVehiculos", token);
    }

    public List<Map<String, String>> consultarVehiculo(String placa, String token) {
        return invocar(vehiculosUrl, "consultarVehiculoPorPlaca", token, "placa", placa);
    }

    public List<Map<String, String>> crearVehiculo(String placa, String marca, String modelo,
                                                    String color, String token) {
        return invocar(vehiculosUrl, "crearVehiculo", token,
                "placa", placa, "marca", marca, "modelo", modelo, "color", color);
    }

    public List<Map<String, String>> consultarInfraccionPorNumero(String numero, String token) {
        return invocar(infraccionesUrl, "consultarPorNumero", token, "numeroInfraccion", numero);
    }

    public List<Map<String, String>> consultarInfraccionesPorPlaca(String placa, String token) {
        return invocar(infraccionesUrl, "consultarPorPlaca", token, "placa", placa);
    }

    public List<Map<String, String>> consultarInfraccionesPorDpi(String dpi, String token) {
        return invocar(infraccionesUrl, "consultarPorDpi", token, "dpi", dpi);
    }

    public List<Map<String, String>> listarNumerosInfraccion(String token) {
        Map<String, Map<String, String>> unicas = new LinkedHashMap<>();
        for (Map<String, String> vehiculo : listarVehiculos(token)) {
            String placa = vehiculo.get("placa");
            if (placa == null || placa.isBlank()) continue;
            for (Map<String, String> infraccion : consultarInfraccionesPorPlaca(placa, token)) {
                String numero = infraccion.get("numeroInfraccion");
                if (numero != null && !numero.isBlank()) {
                    unicas.putIfAbsent(numero, Map.of("numero", numero));
                }
            }
        }
        return List.copyOf(unicas.values());
    }

    public List<String> columnas(List<Map<String, String>> filas) {
        return filas.stream().flatMap(f -> f.keySet().stream()).distinct().toList();
    }

    public List<String> columnasPersonas(List<Map<String, String>> filas) {
        List<String> todas = columnas(filas);
        List<String> primero = List.of("nombrePersona", "apellidoPersona", "dpiPersona");
        return java.util.stream.Stream.concat(
                primero.stream().filter(todas::contains),
                todas.stream().filter(columna -> !primero.contains(columna))).toList();
    }

    public List<String> etiquetas(List<String> columnas) {
        return columnas.stream().map(columna -> switch (columna) {
            case "nombrePersona" -> "Nombre";
            case "apellidoPersona" -> "Apellido";
            case "dpiPersona" -> "DPI";
            case "numeroInfraccion" -> "Número de infracción";
            case "fechaInfraccion" -> "Fecha";
            case "direccionInfraccion" -> "Dirección";
            case "tipoInfraccion" -> "Tipo de infracción";
            case "montoOriginal" -> "Monto";
            case "placaVehiculo", "placa" -> "Placa";
            case "estado" -> "Estado";
            case "usuarioAgente" -> "Agente";
            case "idInfraccion" -> "ID";
            case "idPersona" -> "ID";
            case "idVehiculo" -> "ID";
            default -> columna.replaceAll("([a-z])([A-Z])", "$1 $2");
        }).toList();
    }

    private List<Map<String, String>> invocar(String url, String operacion,
                                               String token, String... parametros) {
        List<Map<String, String>> filas = new ArrayList<>();
        for (Element objeto : soap.invocar(url, operacion, token, parametros)) {
            Map<String, String> fila = new LinkedHashMap<>();
            NodeList hijos = objeto.getChildNodes();
            for (int i = 0; i < hijos.getLength(); i++) {
                Node hijo = hijos.item(i);
                if (hijo instanceof Element campo) {
                    fila.put(campo.getTagName(), campo.getTextContent().trim());
                }
            }
            if (!fila.isEmpty()) {
                filas.add(fila);
            }
        }
        return filas;
    }
}
