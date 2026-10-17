package com.infracciones.apiinfraccionesvehiculares.Repository;

import com.infracciones.apiinfraccionesvehiculares.Model.Usuario;
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
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SELECT_BASE =
            "SELECT u.id_usuario, u.usuario, u.contraseña, u.estado, u.id_persona, u.id_rol, " +
                    "p.dpi_persona, r.nombre_rol " +
                    "FROM usuario u " +
                    "INNER JOIN persona p ON u.id_persona = p.id_persona " +
                    "INNER JOIN rol r ON u.id_rol = r.id_rol ";

    private static final RowMapper<Usuario> ROW_MAPPER = (rs, rowNum) -> {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setUsuario(rs.getString("usuario"));
        usuario.setContrasenaHash(rs.getString("contraseña"));
        usuario.setEstado(rs.getBoolean("estado"));
        usuario.setIdPersona(rs.getInt("id_persona"));
        usuario.setIdRol(rs.getInt("id_rol"));
        usuario.setDpiPersona(rs.getString("dpi_persona"));
        usuario.setNombreRol(rs.getString("nombre_rol"));
        return usuario;
    };

    public List<Usuario> findAll() {
        return jdbcTemplate.query(SELECT_BASE + "ORDER BY u.id_usuario", ROW_MAPPER);
    }

    public Optional<Usuario> findByUsername(String usuario) {
        List<Usuario> resultado = jdbcTemplate.query(
                SELECT_BASE + "WHERE u.usuario = ?", ROW_MAPPER, usuario);
        return resultado.stream().findFirst();
    }

    public boolean existsByUsername(String usuario) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE usuario = ?", Integer.class, usuario);
        return total != null && total > 0;
    }

    public boolean existsByIdPersona(int idPersona) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM usuario WHERE id_persona = ?", Integer.class, idPersona);
        return total != null && total > 0;
    }

    public Optional<Integer> findIdRolByNombre(String nombreRol) {
        List<Integer> resultado = jdbcTemplate.query(
                "SELECT id_rol FROM rol WHERE nombre_rol = ?",
                (rs, rowNum) -> rs.getInt("id_rol"),
                nombreRol);
        return resultado.stream().findFirst();
    }

    public boolean tieneInfraccionesAsociadas(int idUsuario) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM infraccion WHERE id_usuario = ?", Integer.class, idUsuario);
        return total != null && total > 0;
    }

    public Integer createUser(Usuario usuario) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO usuario (usuario, contraseña, estado, id_persona, id_rol) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, usuario.getUsuario());
            ps.setString(2, usuario.getContrasenaHash());
            ps.setBoolean(3, usuario.getEstado());
            ps.setInt(4, usuario.getIdPersona());
            ps.setInt(5, usuario.getIdRol());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    public int updateByUsername(String usuario, Integer idRol, Boolean estado) {
        String sql = "UPDATE usuario SET id_rol = ?, estado = ? WHERE usuario = ?";
        return jdbcTemplate.update(sql, idRol, estado, usuario);
    }

    public int updatePasswordByUsername(String usuario, String nuevoHash) {
        String sql = "UPDATE usuario SET contraseña = ? WHERE usuario = ?";
        return jdbcTemplate.update(sql, nuevoHash, usuario);
    }

    public int deleteByUsername(String usuario) {
        return jdbcTemplate.update("DELETE FROM usuario WHERE usuario = ?", usuario);
    }
}