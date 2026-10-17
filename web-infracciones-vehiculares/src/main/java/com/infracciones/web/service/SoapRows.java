package com.infracciones.web.service;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SoapRows {
    private SoapRows() { }

    static List<Map<String, String>> mapear(List<Element> objetos) {
        List<Map<String, String>> filas = new ArrayList<>();
        for (Element objeto : objetos) {
            Map<String, String> fila = new LinkedHashMap<>();
            NodeList hijos = objeto.getChildNodes();
            for (int i = 0; i < hijos.getLength(); i++) {
                Node hijo = hijos.item(i);
                if (hijo instanceof Element campo) fila.put(campo.getTagName(), campo.getTextContent().trim());
            }
            if (!fila.isEmpty()) filas.add(fila);
        }
        return filas;
    }
}
