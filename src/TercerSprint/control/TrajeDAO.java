package TercerSprint.control;

import TercerSprint.modelo.Traje;
import TercerSprint.modelo.Traje.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad {@link Traje}.
 * <p>
 * Encapsula las operaciones CRUD sobre la tabla <code>trajes</code>.
 * Cada traje pertenece a un único cliente (FK <code>id_cliente</code>).
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del acceso a BBDD.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class TrajeDAO {

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Traje}.
     *
     * @param rs cursor posicionado sobre una fila válida.
     * @return el traje construido.
     * @throws SQLException si falla la lectura.
     */
    private Traje map(ResultSet rs) throws SQLException {
        Traje t = new Traje();
        t.setIdentificador(rs.getInt("id_traje"));
        t.setIdCliente(rs.getInt("id_cliente"));
        t.setNombre(rs.getString("nombre"));
        t.setEstado(Estado.fromTexto(rs.getString("estado")));
        return t;
    }

    /**
     * Recupera todos los trajes ordenados por cliente y nombre.
     *
     * @return lista (posiblemente vacía) de trajes.
     * @throws SQLException si falla la consulta.
     */
    public List<Traje> listarTodos() throws SQLException {
        List<Traje> lista = new ArrayList<>();
        String sql = "SELECT * FROM trajes ORDER BY id_cliente, nombre";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    /**
     * Devuelve todos los trajes asociados a un cliente concreto.
     *
     * @param idCliente identificador del cliente propietario.
     * @return lista (posiblemente vacía) de trajes del cliente.
     * @throws SQLException si falla la consulta.
     */
    public List<Traje> listarPorCliente(int idCliente) throws SQLException {
        List<Traje> lista = new ArrayList<>();
        String sql = "SELECT * FROM trajes WHERE id_cliente = ? ORDER BY nombre";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    /**
     * Busca un traje por su identificador.
     *
     * @param id identificador del traje.
     * @return el traje encontrado o <code>null</code> si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Traje buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM trajes WHERE id_traje = ?";
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
     * Inserta un nuevo traje en la base de datos.
     *
     * @param traje traje a persistir; tras la inserción se actualiza su id.
     * @return el identificador generado por la BBDD, o -1 si falló.
     * @throws SQLException si falla la inserción.
     */
    public int crear(Traje traje) throws SQLException {
        String sql = "INSERT INTO trajes(id_cliente, nombre, estado) VALUES (?, ?, ?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, traje.getIdCliente());
            ps.setString(2, traje.getNombre());
            ps.setString(3, traje.getEstado().getTexto());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    traje.setIdentificador(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Modifica los datos principales del traje (nombre y estado).
     *
     * @param traje traje con identificador válido y campos actualizados.
     * @return <code>true</code> si se modificó alguna fila.
     * @throws SQLException si falla la actualización.
     */
    public boolean modificar(Traje traje) throws SQLException {
        String sql = "UPDATE trajes SET nombre = ?, estado = ? WHERE id_traje = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, traje.getNombre());
            ps.setString(2, traje.getEstado().getTexto());
            ps.setInt(3, traje.getIdentificador());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Cambia el estado de un traje (Diseño / Costura / Taller).
     *
     * @param idTraje      identificador del traje.
     * @param nuevoEstado  nuevo estado.
     * @return <code>true</code> si se actualizó alguna fila.
     * @throws SQLException si falla la operación.
     */
    public boolean cambiarEstado(int idTraje, Estado nuevoEstado) throws SQLException {
        String sql = "UPDATE trajes SET estado = ? WHERE id_traje = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.getTexto());
            ps.setInt(2, idTraje);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un traje de la base de datos.
     *
     * @param idTraje identificador del traje.
     * @return <code>true</code> si se eliminó alguna fila.
     * @throws SQLException si falla el borrado.
     */
    public boolean borrar(int idTraje) throws SQLException {
        String sql = "DELETE FROM trajes WHERE id_traje = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTraje);
            return ps.executeUpdate() > 0;
        }
    }
}
