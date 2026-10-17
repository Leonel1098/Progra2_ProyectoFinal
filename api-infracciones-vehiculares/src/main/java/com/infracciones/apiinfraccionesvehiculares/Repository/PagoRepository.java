package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.Pago;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class PagoRepository {

    private final JdbcTemplate jdbcTemplate;

    public PagoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE =
            "SELECT pg.id_pago, pg.id_infraccion, i.numero_infraccion, pg.monto_inicial, " +
                    "pg.porcentaje_descuento, pg.monto_descuento, pg.monto_pagado, pg.fecha_pago " +
                    "FROM pago pg " +
                    "INNER JOIN infraccion i ON pg.id_infraccion = i.id_infraccion ";

    private static final RowMapper<Pago> ROW_MAPPER = (rs, rowNum) -> {
        Pago pago = new Pago();
        pago.setIdPago(rs.getInt("id_pago"));
        pago.setIdInfraccion(rs.getInt("id_infraccion"));
        pago.setNumeroInfraccion(rs.getString("numero_infraccion"));
        pago.setMontoInicial(rs.getBigDecimal("monto_inicial"));
        pago.setPorcentajeDescuento(rs.getBigDecimal("porcentaje_descuento"));
        pago.setMontoDescuento(rs.getBigDecimal("monto_descuento"));
        pago.setMontoPagado(rs.getBigDecimal("monto_pagado"));
        pago.setFechaPago(rs.getObject("fecha_pago", LocalDate.class));
        return pago;
    };

    public List<Pago> findAll() {
        return jdbcTemplate.query(SELECT_BASE + "ORDER BY pg.id_pago DESC", ROW_MAPPER);
    }

    public Optional<Pago> findByNumeroInfraccion(String numeroInfraccion) {
        List<Pago> resultado = jdbcTemplate.query(
                SELECT_BASE + "WHERE i.numero_infraccion = ?", ROW_MAPPER, numeroInfraccion);
        return resultado.stream().findFirst();
    }

    public boolean existsByIdInfraccion(Integer idInfraccion) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pago WHERE id_infraccion = ?", Integer.class, idInfraccion);
        return total != null && total > 0;
    }

    public Integer insert(Pago pago) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO pago (id_infraccion, monto_inicial, porcentaje_descuento, " +
                            "monto_descuento, monto_pagado, fecha_pago) VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, pago.getIdInfraccion());
            ps.setBigDecimal(2, pago.getMontoInicial());
            ps.setBigDecimal(3, pago.getPorcentajeDescuento());
            ps.setBigDecimal(4, pago.getMontoDescuento());
            ps.setBigDecimal(5, pago.getMontoPagado());
            ps.setObject(6, pago.getFechaPago());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }
}
