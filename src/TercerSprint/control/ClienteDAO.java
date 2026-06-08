package TercerSprint.control;

import TercerSprint.modelo.Cliente;
import TercerSprint.modelo.Cliente.Tipo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad {@link Cliente}.
 * <p>
 * Encapsula todas las operaciones CRUD sobre la tabla <code>clientes</code>
 * de la base de datos <code>edna_moda</code>.
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del acceso a BBDD.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class ClienteDAO {

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Cliente}.
     *
     * @param rs cursor posicionado sobre una fila válida.
     * @return el cliente construido a partir de la fila.
     * @throws SQLException si falla la lectura de alguna columna.
     */
    private Cliente map(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setNumeroCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre_superheroe"));
        c.setSuperpoder(rs.getString("superpoder"));
        c.setColores(rs.getString("colores"));
        c.setTipo(Tipo.fromTexto(rs.getString("tipo")));
        return c;
    }

    /**
     * Recupera todos los clientes ordenados alfabéticamente por nombre.
     *
     * @return lista (posiblemente vacía) de clientes.
     * @throws SQLException si falla la consulta.
     */
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes ORDER BY nombre_superheroe";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente.
     * @return el cliente encontrado o <code>null</code> si no existe.
     * @throws SQLException si falla la consulta.
     */
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id_cliente = ?";
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
     * Inserta un nuevo cliente en la base de datos.
     *
     * @param cliente cliente a persistir; tras la inserción se actualiza su id.
     * @return el identificador generado por la BBDD, o -1 si falló.
     * @throws SQLException si falla la inserción.
     */
    public int crear(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes(nombre_superheroe, superpoder, colores, tipo) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getSuperpoder());
            ps.setString(3, cliente.getColores());
            ps.setString(4, cliente.getTipo() != null ? cliente.getTipo().getTexto() : null);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    cliente.setNumeroCliente(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param cliente cliente con id válido y campos actualizados.
     * @return <code>true</code> si se modificó al menos una fila.
     * @throws SQLException si falla la actualización.
     */
    public boolean modificar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre_superheroe = ?, superpoder = ?, "
                   + "colores = ?, tipo = ? WHERE id_cliente = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getSuperpoder());
            ps.setString(3, cliente.getColores());
            ps.setString(4, cliente.getTipo() != null ? cliente.getTipo().getTexto() : null);
            ps.setInt(5, cliente.getNumeroCliente());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un cliente por su identificador.
     *
     * @param idCliente identificador del cliente a borrar.
     * @return <code>true</code> si se eliminó alguna fila.
     * @throws SQLException si falla el borrado o existen referencias activas.
     */
    public boolean borrar(int idCliente) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id_cliente = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        }
    }
}
