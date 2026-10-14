package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.InfraccionDetalle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class InfraccionRepository {

    private final JdbcTemplate jdbcTemplate;

    public InfraccionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE =
            "SELECT i.id_infraccion, i.numero_infraccion, i.fecha_infraccion, i.direccion_infraccion, i.monto_original, " +
                    "       p.dpi_persona, CONCAT(p.nombre_persona, ' ', p.apellido_persona) AS nombre_persona, " +
                    "       v.placa_vehiculo, ti.nombre AS tipo_infraccion, ei.nombre_estado AS estado, " +
                    "       u.usuario AS usuario_agente " +
                    "FROM infraccion i " +
                    "INNER JOIN persona p ON i.id_persona = p.id_persona " +
                    "INNER JOIN vehiculo v ON i.id_vehiculo = v.id_vehiculo " +
                    "INNER JOIN tipo_infraccion ti ON i.id_tipo_infraccion = ti.id_tipo_infraccion " +
                    "INNER JOIN estado_infraccion ei ON i.id_estado = ei.id_estado " +
                    "INNER JOIN usuario u ON i.id_usuario = u.id_usuario ";

    private static final RowMapper<InfraccionDetalle> ROW_MAPPER = (rs, rowNum) -> {
        InfraccionDetalle inf = new InfraccionDetalle();
        inf.setIdInfraccion(rs.getInt("id_infraccion"));
        inf.setNumeroInfraccion(rs.getString("numero_infraccion"));
        Date fInf = rs.getDate("fecha_infraccion");
        inf.setFechaInfraccion(fInf != null ? fInf.toString() : null);
        inf.setDireccionInfraccion(rs.getString("direccion_infraccion"));
        inf.setMontoOriginal(rs.getBigDecimal("monto_original"));
        inf.setDpiPersona(rs.getString("dpi_persona"));
        inf.setNombrePersona(rs.getString("nombre_persona"));
        inf.setPlacaVehiculo(rs.getString("placa_vehiculo"));
        inf.setTipoInfraccion(rs.getString("tipo_infraccion"));
        inf.setEstado(rs.getString("estado"));
        inf.setUsuarioAgente(rs.getString("usuario_agente"));
        return inf;
    };

    public List<InfraccionDetalle> findAll() {
        return jdbcTemplate.query(SELECT_BASE + "ORDER BY i.id_infraccion DESC", ROW_MAPPER);
    }

    public Optional<InfraccionDetalle> findByNumero(String numeroInfraccion) {
        List<InfraccionDetalle> r = jdbcTemplate.query(
                SELECT_BASE + "WHERE i.numero_infraccion = ?", ROW_MAPPER, numeroInfraccion);
        return r.stream().findFirst();
    }

    public List<InfraccionDetalle> findByPlaca(String placa) {
        return jdbcTemplate.query(
                SELECT_BASE + "WHERE v.placa_vehiculo = ? ORDER BY i.id_infraccion DESC",
                ROW_MAPPER, placa);
    }

    public List<InfraccionDetalle> findByDpi(String dpi) {
        return jdbcTemplate.query(
                SELECT_BASE + "WHERE p.dpi_persona = ? ORDER BY i.id_infraccion DESC",
                ROW_MAPPER, dpi);
    }

    public boolean existsByNumero(String numeroInfraccion) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM infraccion WHERE numero_infraccion = ?",
                Integer.class, numeroInfraccion);
        return total != null && total > 0;
    }

    public Optional<Integer> findIdEstadoByNombre(String nombreEstado) {
        List<Integer> r = jdbcTemplate.query(
                "SELECT id_estado FROM estado_infraccion WHERE nombre_estado = ?",
                (rs, rowNum) -> rs.getInt("id_estado"),
                nombreEstado);
        return r.stream().findFirst();
    }

    public Integer insert(String numero, LocalDate fecha, String direccion, BigDecimal montoOriginal,
                          int idPersona, int idVehiculo, int idUsuario, int idTipo, int idEstado) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO infraccion (numero_infraccion, fecha_infraccion, direccion_infraccion, " +
                            "monto_original, id_persona, id_vehiculo, id_usuario, id_tipo_infraccion, id_estado) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, numero);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setString(3, direccion);
            ps.setBigDecimal(4, montoOriginal);
            ps.setInt(5, idPersona);
            ps.setInt(6, idVehiculo);
            ps.setInt(7, idUsuario);
            ps.setInt(8, idTipo);
            ps.setInt(9, idEstado);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int updateEstado(int idInfraccion, int nuevoIdEstado) {
        return jdbcTemplate.update(
                "UPDATE infraccion SET id_estado = ? WHERE id_infraccion = ?",
                nuevoIdEstado, idInfraccion);
    }
}