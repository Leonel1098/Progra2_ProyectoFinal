package com.infracciones.apiinfraccionesvehiculares.Model;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
public class LocalDateAdapter extends XmlAdapter<String, LocalDate>{
    @Override
    public LocalDate unmarshal(String value) {
        return (value == null || value.isBlank()) ? null : LocalDate.parse(value);
    }

    @Override
    public String marshal(LocalDate value) {
        return value == null ? null : value.toString();
    }
}
