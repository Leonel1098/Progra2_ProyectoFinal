package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.ReglaDescuento;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public class ReglaDescuentoRepository {
    private final JdbcTemplate jdbcTemplate;

    public ReglaDescuentoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<ReglaDescuento> ROW_MAPPER = (rs, rowNum) -> {
        ReglaDescuento regla = new ReglaDescuento();
        regla.setIdRegla(rs.getInt("id_regla"));
        regla.setPorcentaje(rs.getBigDecimal("porcentaje"));
        regla.setDiasLimite(rs.getInt("dias_limite"));
        regla.setEstado(rs.getInt("estado"));
        return regla;
    };

    public Optional<ReglaDescuento> findActiva() {
        String sql = "SELECT id_regla, porcentaje, dias_limite, estado FROM regla_descuento WHERE estado = 1 LIMIT 1";
        List<ReglaDescuento> resultado = jdbcTemplate.query(sql, ROW_MAPPER);
        return resultado.stream().findFirst();
    }
}
