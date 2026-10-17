package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.Vehiculo;
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
public class VehiculoRepository {
    private final JdbcTemplate jdbcTemplate;

    public VehiculoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Vehiculo> ROW_MAPPER =(result,rowNum)->{
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(result.getInt("id_vehiculo"));
        vehiculo.setPlaca(result.getString("placa_vehiculo"));
        vehiculo.setMarca(result.getString("marca"));
        vehiculo.setModelo(result.getString("modelo"));
        vehiculo.setColor(result.getString("color"));
        return vehiculo;
    };

    public List<Vehiculo> findAll() {
        String sql = "SELECT id_vehiculo, placa_vehiculo, marca,modelo, color " +
                "FROM vehiculo ORDER BY id_vehiculo";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    public Optional<Vehiculo> findByPlaca(String placa) {
        String sql = "SELECT id_vehiculo, placa_vehiculo, marca,modelo, color " +
                "FROM vehiculo WHERE placa_vehiculo = ?";
        List<Vehiculo> resultado = jdbcTemplate.query(sql, ROW_MAPPER, placa);
        return resultado.stream().findFirst();
    }

    public boolean existsByPlaca(String placa) {
        String sql = "SELECT COUNT(*) FROM vehiculo WHERE placa_vehiculo = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, placa);;
        return total != null && total > 0;
    }

    public boolean tieneInfraccionesAsociadas(String placa) {
        String sql = "SELECT COUNT(*) FROM infraccion i " +
                "INNER JOIN vehiculo v ON i.id_vehiculo = v.id_vehiculo " +
                "WHERE v.placa_vehiculo = ?";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, placa);
        return total != null && total > 0;
    }

    public Integer insert(Vehiculo vehiculo) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO vehiculo (placa_vehiculo, modelo, marca, color) " +
                            "VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, vehiculo.getPlaca());
            ps.setString(2, vehiculo.getModelo());
            ps.setString(3, vehiculo.getMarca());
            ps.setString(4, vehiculo.getColor());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int updateByPlaca(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculo SET modelo = ?, marca = ?, color = ? " +
                "WHERE placa_vehiculo = ?";
        return jdbcTemplate.update(sql,
                vehiculo.getModelo(),
                vehiculo.getMarca(),
                vehiculo.getColor(),
                vehiculo.getPlaca());
    }

    public int deleteByPlaca(String placa) {
        String sql = "DELETE FROM vehiculo WHERE placa_vehiculo = ?";
        return jdbcTemplate.update(sql, placa);
    }
}
