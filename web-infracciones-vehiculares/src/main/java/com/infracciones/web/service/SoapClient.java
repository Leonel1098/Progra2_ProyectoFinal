package com.infracciones.web.service;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente SOAP único del frontend. Arma el envelope, agrega el token (Bearer) cuando hay sesión,
 * traduce los faults del backend a excepciones y devuelve los nodos {@code <return>}.
 */
@Component
public class SoapClient {

    private static final String NAMESPACE =
            "https://soap.apiinfraccionesvehiculares.infracciones.com/";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /**
     * @param parametros pares nombre, valor en el orden que define el WSDL
     * @param token      token de sesión del backend, o null para llamadas anónimas
     */
    public List<Element> invocar(String url, String operacion, String token, String... parametros) {
        if (parametros.length % 2 != 0) {
            throw new IllegalArgumentException("Los parámetros deben venir en pares nombre/valor");
        }

        StringBuilder cuerpo = new StringBuilder();
        for (int i = 0; i < parametros.length; i += 2) {
            cuerpo.append('<').append(parametros[i]).append('>')
                    .append(escapar(parametros[i + 1]))
                    .append("</").append(parametros[i]).append('>');
        }

        String envelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tns="%s">
                   <soapenv:Body>
                      <tns:%s>%s</tns:%s>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(NAMESPACE, operacion, cuerpo, operacion);

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(envelope, StandardCharsets.UTF_8));
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }

        try {
            HttpResponse<byte[]> respuesta =
                    httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());

            Document documento = parsear(respuesta.body());

            NodeList fallo = documento.getElementsByTagName("faultstring");
            if (fallo.getLength() > 0) {
                throw traducirFallo(fallo.item(0).getTextContent().trim());
            }
            if (respuesta.statusCode() != 200) {
                throw new ServicioInfraccionesException(
                        "El servicio respondió con el código " + respuesta.statusCode());
            }

            NodeList nodos = documento.getElementsByTagName("return");
            List<Element> resultado = new ArrayList<>();
            for (int i = 0; i < nodos.getLength(); i++) {
                resultado.add((Element) nodos.item(i));
            }
            return resultado;

        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new ServicioInfraccionesException("No fue posible comunicarse con el servicio", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServicioInfraccionesException("La operación fue interrumpida", e);
        }
    }

    public static String texto(Element padre, String etiqueta) {
        NodeList lista = padre.getElementsByTagName(etiqueta);
        return lista.getLength() == 0 ? "" : lista.item(0).getTextContent().trim();
    }

    // El backend responde todo como soap:Server; el tipo de error solo se distingue por el mensaje.
    private SoapFaultException traducirFallo(String mensaje) {
        if (mensaje.startsWith("Debe iniciar sesión")) {
            return new SesionInvalidaException(mensaje);
        }
        if (mensaje.startsWith("Su rol no tiene permiso") || mensaje.startsWith("Solo puede acceder")) {
            return new AccesoDenegadoException(mensaje);
        }
        return new SoapFaultException(mensaje);
    }

    private Document parsear(byte[] xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        fabrica.setXIncludeAware(false);
        fabrica.setExpandEntityReferences(false);
        return fabrica.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
    }

    private String escapar(String valor) {
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
