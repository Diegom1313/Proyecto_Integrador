package TercerSprint.control;

import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Empleado.Categoria;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad {@link Empleado}.
 * <p>
 * Encapsula el acceso a la tabla <code>empleados</code> de la base de datos
 * <code>edna_moda</code>, incluyendo la autenticación con SHA-256.
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del acceso a BBDD.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class EmpleadoDAO {

    /** Convierte una fila del ResultSet en un objeto Empleado. */
    private Empleado map(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setIdentificador(rs.getInt("id_empleado"));
        e.setNombreApellidos(rs.getString("nombre") + " " + rs.getString("apellidos"));
        e.setApodo(rs.getString("apodo"));
        e.setCategoria(Categoria.fromTexto(rs.getString("categoria")));
        e.setPasswordHash(rs.getString("password"));
        return e;
    }

    /**
     * Autentica al empleado por apodo + contraseña en claro.
     * La contraseña se cifra en SHA-256 para compararla con la almacenada.
     *
     * @return el {@link Empleado} si las credenciales son válidas, null en caso contrario.
     */
    public Empleado autenticar(String apodo, String passwordEnClaro) throws SQLException {
        String hash = sha256(passwordEnClaro);
        String sql = "SELECT * FROM empleados WHERE apodo = ? AND password = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, apodo);
            ps.setString(2, hash);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
                return null;
            }
        }
    }

    /**
     * Devuelve todos los empleados ordenados por categoría y apodo.
     *
     * @return lista (posiblemente vacía) de empleados.
     * @throws SQLException si falla la consulta.
     */
    public List<Empleado> listarTodos() throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados ORDER BY categoria, apodo";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    /**
     * Devuelve los empleados filtrados por categoría.
     *
     * @param categoria categoría a buscar.
     * @return lista (posiblemente vacía) de empleados de esa categoría.
     * @throws SQLException si falla la consulta.
     */
    public List<Empleado> listarPorCategoria(Categoria categoria) throws SQLException {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados WHERE categoria = ? ORDER BY apodo";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categoria.getTexto());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un empleado por su identificador.
     *
     * @param id identificador del empleado.
     * @return el empleado encontrado o <code>null</code>.
     * @throws SQLException si falla la consulta.
     */
    public Empleado buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM empleados WHERE id_empleado = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /**
     * Comprueba si existe ya un empleado con el apodo indicado.
     *
     * @param apodo            apodo a comprobar.
     * @param idEmpleadoExcluir id de un empleado a excluir de la comprobación
     *                          (útil al editar para no chocar consigo mismo);
     *                          pasar 0 si no aplica.
     * @return <code>true</code> si existe otro empleado con ese apodo.
     * @throws SQLException si falla la consulta.
     */
    public boolean existeApodo(String apodo, int idEmpleadoExcluir) throws SQLException {
        String sql = "SELECT 1 FROM empleados WHERE apodo = ? AND id_empleado <> ? LIMIT 1";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, apodo);
            ps.setInt(2, idEmpleadoExcluir);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Cuenta cuántos empleados hay con la categoría indicada.
     * Utilizado por el controlador para impedir borrar al último Maestro.
     *
     * @param categoria categoría a contar.
     * @return número de empleados de esa categoría.
     * @throws SQLException si falla la consulta.
     */
    public int contarPorCategoria(Categoria categoria) throws SQLException {
        String sql = "SELECT COUNT(*) FROM empleados WHERE categoria = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categoria.getTexto());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Inserta un nuevo empleado en la base de datos. La contraseña se cifra
     * con SHA-256 antes de almacenarse.
     *
     * @param empleado        datos del empleado (sin id; se rellena tras la inserción).
     * @param nombre          nombre.
     * @param apellidos       apellidos.
     * @param passwordEnClaro contraseña en claro (se hashea).
     * @return el identificador generado por la BBDD, o -1 si falló.
     * @throws SQLException si falla la inserción.
     */
    public int crear(Empleado empleado, String nombre, String apellidos,
                     String passwordEnClaro) throws SQLException {
        String sql = "INSERT INTO empleados(nombre, apellidos, apodo, categoria, password) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setString(3, empleado.getApodo());
            ps.setString(4, empleado.getCategoria().getTexto());
            ps.setString(5, sha256(passwordEnClaro));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    empleado.setIdentificador(id);
                    empleado.setNombreApellidos(nombre + " " + apellidos);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Modifica los datos del empleado salvo la contraseña.
     * Para cambiar la contraseña usar {@link #cambiarPassword(int, String)}.
     *
     * @param empleado  empleado con id válido, apodo y categoría actualizados.
     * @param nombre    nombre actualizado.
     * @param apellidos apellidos actualizados.
     * @return <code>true</code> si se modificó alguna fila.
     * @throws SQLException si falla la actualización.
     */
    public boolean modificar(Empleado empleado, String nombre, String apellidos) throws SQLException {
        String sql = "UPDATE empleados SET nombre = ?, apellidos = ?, apodo = ?, "
                   + "categoria = ? WHERE id_empleado = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellidos);
            ps.setString(3, empleado.getApodo());
            ps.setString(4, empleado.getCategoria().getTexto());
            ps.setInt(5, empleado.getIdentificador());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cambia la contraseña de un empleado. La contraseña se cifra con SHA-256
     * antes de guardarse.
     *
     * @param idEmpleado      identificador del empleado.
     * @param passwordEnClaro nueva contraseña en claro.
     * @return <code>true</code> si se actualizó la fila.
     * @throws SQLException si falla la operación.
     */
    public boolean cambiarPassword(int idEmpleado, String passwordEnClaro) throws SQLException {
        String sql = "UPDATE empleados SET password = ? WHERE id_empleado = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sha256(passwordEnClaro));
            ps.setInt(2, idEmpleado);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un empleado por su identificador.
     * Si el empleado es responsable de alguna cita, MySQL lanzará una
     * {@link SQLException} por violación de la FK fk_cita_empleado
     * (configurada como ON DELETE RESTRICT).
     *
     * @param idEmpleado identificador del empleado a borrar.
     * @return <code>true</code> si se eliminó alguna fila.
     * @throws SQLException si falla el borrado o existen citas referenciando al empleado.
     */
    public boolean borrar(int idEmpleado) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id_empleado = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        }
    }

    // =====================================================================
    // Utilidad: SHA-256 (la misma función que SHA2('x',256) de MySQL)
    // =====================================================================

    /**
     * Devuelve el hash SHA-256 (en hexadecimal minúsculas) de una cadena UTF-8.
     * Equivalente a la función <code>SHA2(x, 256)</code> de MySQL, lo que
     * permite comparar contraseñas almacenadas en la BBDD sin conversión adicional.
     *
     * @param texto cadena de la que calcular el hash.
     * @return hash SHA-256 en formato hexadecimal.
     */
    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException ex) {
            throw new RuntimeException("Error al calcular SHA-256", ex);
        }
    }
}
