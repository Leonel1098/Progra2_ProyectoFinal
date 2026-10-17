package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.TipoInfraccion;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class TipoInfraccionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TipoInfraccionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<TipoInfraccion> ROW_MAPPER = (rs, rowNum) -> {
        TipoInfraccion tipo = new TipoInfraccion();
        tipo.setIdTipoInfraccion(rs.getInt("id_tipo_infraccion"));
        tipo.setCodigo(rs.getString("codigo"));
        tipo.setNombre(rs.getString("nombre"));
        tipo.setDescripcion(rs.getString("descripcion"));
        tipo.setMonto(rs.getBigDecimal("monto"));
        return tipo;
    };

    public List<TipoInfraccion> findAll() {
        String sql = "SELECT id_tipo_infraccion, codigo, nombre, descripcion, monto " +
                "FROM tipo_infraccion ORDER BY id_tipo_infraccion";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public Optional<TipoInfraccion> findById(Integer id) {
        String sql = "SELECT id_tipo_infraccion, codigo, nombre, descripcion, monto " +
                "FROM tipo_infraccion WHERE id_tipo_infraccion = ?";
        List<TipoInfraccion> resultado = jdbcTemplate.query(sql, ROW_MAPPER, id);
        return resultado.stream().findFirst();
    }

    public Optional<TipoInfraccion> findByCodigo(String codigo) {
        String sql = "SELECT id_tipo_infraccion, codigo, nombre, descripcion, monto " +
                "FROM tipo_infraccion WHERE codigo = ?";
        List<TipoInfraccion> resultado = jdbcTemplate.query(sql, ROW_MAPPER, codigo);
        return resultado.stream().findFirst();
    }

    public boolean existsByCodigo(String codigo) {
        String sql = "SELECT COUNT(*) FROM tipo_infraccion WHERE codigo = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, codigo);
        return total != null && total > 0;
    }

    public boolean associatedViolations(Integer idTipoInfraccion) {
        String sql = "SELECT COUNT(*) FROM infraccion WHERE id_tipo_infraccion = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, idTipoInfraccion);
        return total != null && total > 0;
    }

    public Integer insert(TipoInfraccion tipo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO tipo_infraccion (codigo, nombre, descripcion, monto) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, tipo.getCodigo());
            ps.setString(2, tipo.getNombre());
            ps.setString(3, tipo.getDescripcion());
            ps.setBigDecimal(4, tipo.getMonto());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int update(TipoInfraccion tipo) {
        String sql = "UPDATE tipo_infraccion SET codigo = ?, nombre = ?, descripcion = ?, monto = ? " +
                "WHERE id_tipo_infraccion = ?";
        return jdbcTemplate.update(sql,
                tipo.getCodigo(),
                tipo.getNombre(),
                tipo.getDescripcion(),
                tipo.getMonto(),
                tipo.getIdTipoInfraccion());
    }

    public int deleteById(Integer id) {
        String sql = "DELETE FROM tipo_infraccion WHERE id_tipo_infraccion = ?";
        return jdbcTemplate.update(sql, id);
    }
}