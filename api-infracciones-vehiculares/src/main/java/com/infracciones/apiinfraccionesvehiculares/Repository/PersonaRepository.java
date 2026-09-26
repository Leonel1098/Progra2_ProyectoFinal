package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.Persona;
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
public class PersonaRepository {

    private final JdbcTemplate jdbcTemplate;

    public PersonaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Persona> ROW_MAPPER = (rs, rowNum) -> {
        Persona persona = new Persona();
        persona.setIdPersona(rs.getInt("id_persona"));
        persona.setDpiPersona(rs.getString("dpi_persona"));
        persona.setNombrePersona(rs.getString("nombre_persona"));
        persona.setApellidoPersona(rs.getString("apellido_persona"));
        persona.setTelefonoPersona(rs.getString("telefono"));
        persona.setDireccionPersona(rs.getString("direccion"));
        return persona;
    };

    public List<Persona> findAll() {
        String sql = "SELECT id_persona, dpi_persona, nombre_persona, apellido_persona, telefono, direccion " +
                "FROM persona ORDER BY id_persona";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public Optional<Persona> findByDpi(String dpi) {
        String sql = "SELECT id_persona, dpi_persona, nombre_persona, apellido_persona, telefono, direccion " +
                "FROM persona WHERE dpi_persona = ?";
        List<Persona> resultado = jdbcTemplate.query(sql, ROW_MAPPER, dpi);
        return resultado.stream().findFirst();
    }

    public boolean existsByDpi(String dpi) {
        String sql = "SELECT COUNT(*) FROM persona WHERE dpi_persona = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, dpi);
        return total != null && total > 0;
    }

    public boolean tieneUsuarioAsociado(String dpi) {
        String sql = "SELECT COUNT(*) FROM usuario u " +
                "INNER JOIN persona p ON u.id_persona = p.id_persona " +
                "WHERE p.dpi_persona = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, dpi);
        return total != null && total > 0;
    }

    public boolean tieneInfraccionesAsociadas(String dpi) {
        String sql = "SELECT COUNT(*) FROM infraccion i " +
                "INNER JOIN persona p ON i.id_persona = p.id_persona " +
                "WHERE p.dpi_persona = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, dpi);
        return total != null && total > 0;
    }

    public Integer insert(Persona persona) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO persona (dpi_persona, nombre_persona, apellido_persona, telefono, direccion) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, persona.getDpiPersona());
            ps.setString(2, persona.getNombrePersona());
            ps.setString(3, persona.getApellidoPersona());
            ps.setString(4, persona.getTelefonoPersona());
            ps.setString(5, persona.getDireccionPersona());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int updateByDpi(Persona persona) {
        String sql = "UPDATE persona SET nombre_persona = ?, apellido_persona = ?, telefono = ?, direccion = ? " +
                "WHERE dpi_persona = ?";
        return jdbcTemplate.update(sql,
                persona.getNombrePersona(),
                persona.getApellidoPersona(),
                persona.getTelefonoPersona(),
                persona.getDireccionPersona(),
                persona.getDpiPersona());
    }

    public int deleteByDpi(String dpi) {
        String sql = "DELETE FROM persona WHERE dpi_persona = ?";
        return jdbcTemplate.update(sql, dpi);
    }
}