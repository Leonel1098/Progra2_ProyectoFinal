package com.infracciones.web.service;

import com.infracciones.web.dto.response.InfraccionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

@Service
public class ConsultaInfraccionService {

    private static final String NAMESPACE =
            "https://soap.apiinfraccionesvehiculares.infracciones.com/";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final String soapUrl;

    public ConsultaInfraccionService(@Value("${soap.infracciones.url}") String soapUrl) {
        this.soapUrl = soapUrl;
    }

    public List<InfraccionResponse> consultarPorPlaca(String placa) {
        return invocar("consultarPorPlaca", "placa", placa);
    }

    public List<InfraccionResponse> consultarPorNumero(String numeroInfraccion) {
        return invocar("consultarPorNumero", "numeroInfraccion", numeroInfraccion);
    }

    private List<InfraccionResponse> invocar(String operacion, String parametro, String valor) {
        String envelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                                  xmlns:tns="%s">
                   <soapenv:Body>
                      <tns:%s>
                         <%s>%s</%s>
                      </tns:%s>
                   </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(NAMESPACE, operacion, parametro, escapar(valor), parametro, operacion);

        HttpRequest request = HttpRequest.newBuilder(URI.create(soapUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(envelope, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<byte[]> respuesta =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (respuesta.statusCode() != 200) {
                throw new ServicioInfraccionesException(
                        "El servicio de infracciones respondió con el código " + respuesta.statusCode());
            }
            return leerRespuesta(respuesta.body());

        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new ServicioInfraccionesException(
                    "No fue posible comunicarse con el servicio de infracciones", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServicioInfraccionesException("La consulta fue interrumpida", e);
        }
    }

    private List<InfraccionResponse> leerRespuesta(byte[] xml)
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        fabrica.setXIncludeAware(false);
        fabrica.setExpandEntityReferences(false);

        Document documento = fabrica.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        NodeList nodos = documento.getElementsByTagName("return");

        List<InfraccionResponse> resultado = new ArrayList<>();
        for (int i = 0; i < nodos.getLength(); i++) {
            Element e = (Element) nodos.item(i);
            resultado.add(new InfraccionResponse(
                    texto(e, "idInfraccion"),
                    texto(e, "numeroInfraccion"),
                    texto(e, "fechaInfraccion"),
                    texto(e, "direccionInfraccion"),
                    texto(e, "tipoInfraccion"),
                    texto(e, "montoOriginal"),
                    texto(e, "estado"),
                    texto(e, "placaVehiculo"),
                    texto(e, "dpiPersona"),
                    texto(e, "nombrePersona"),
                    texto(e, "usuarioAgente")));
        }
        return resultado;
    }

    private String texto(Element padre, String etiqueta) {
        NodeList lista = padre.getElementsByTagName(etiqueta);
        return lista.getLength() == 0 ? "" : lista.item(0).getTextContent().trim();
    }

    private String escapar(String valor) {
        return valor.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}