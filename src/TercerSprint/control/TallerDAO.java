package TercerSprint.control;

import TercerSprint.modelo.Taller;
import TercerSprint.modelo.Taller.TipoSala;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO (Data Access Object) para la entidad {@link Taller}.
 * <p>
 * Encapsula las operaciones CRUD sobre la tabla <code>talleres</code>
 * (salas físicas del estudio).
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del acceso a BBDD.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class TallerDAO {

    /**
     * Convierte una fila del {@link ResultSet} en un objeto {@link Taller}.
     *
     * @param rs cursor posicionado sobre una fila válida.
     * @return el taller construido.
     * @throws SQLException si falla la lectura.
     */
    private Taller map(ResultSet rs) throws SQLException {
        Taller t = new Taller();
        t.setNumeroTaller(rs.getInt("id_taller"));
        t.setNombreSala(rs.getString("nombre_sala"));
        t.setTipoSala(TipoSala.fromTexto(rs.getString("tipo_sala")));
        return t;
    }

    /**
     * Recupera todos los talleres ordenados por tipo y nombre.
     *
     * @return lista (posiblemente vacía) de talleres.
     * @throws SQLException si falla la consulta.
     */
    public List<Taller> listarTodos() throws SQLException {
        List<Taller> lista = new ArrayList<>();
        String sql = "SELECT * FROM talleres ORDER BY tipo_sala, nombre_sala";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(map(rs));
        }
        return lista;
    }

    /**
     * Busca un taller por su identificador.
     *
     * @param id identificador del taller.
     * @return el taller encontrado o <code>null</code>.
     * @throws SQLException si falla la consulta.
     */
    public Taller buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM talleres WHERE id_taller = ?";
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
     * Inserta un nuevo taller en la base de datos.
     *
     * @param taller taller a persistir; tras la inserción se actualiza su id.
     * @return el identificador generado por la BBDD, o -1 si falló.
     * @throws SQLException si falla la inserción.
     */
    public int crear(Taller taller) throws SQLException {
        String sql = "INSERT INTO talleres(nombre_sala, tipo_sala) VALUES (?, ?)";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, taller.getNombreSala());
            ps.setString(2, taller.getTipoSala().getTexto());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    taller.setNumeroTaller(id);
                    return id;
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza los datos de un taller existente.
     *
     * @param taller taller con id válido y datos actualizados.
     * @return <code>true</code> si se modificó alguna fila.
     * @throws SQLException si falla la actualización.
     */
    public boolean modificar(Taller taller) throws SQLException {
        String sql = "UPDATE talleres SET nombre_sala = ?, tipo_sala = ? WHERE id_taller = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, taller.getNombreSala());
            ps.setString(2, taller.getTipoSala().getTexto());
            ps.setInt(3, taller.getNumeroTaller());
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Elimina un taller por su identificador.
     *
     * @param idTaller identificador del taller.
     * @return <code>true</code> si se eliminó alguna fila.
     * @throws SQLException si falla el borrado.
     */
    public boolean borrar(int idTaller) throws SQLException {
        String sql = "DELETE FROM talleres WHERE id_taller = ?";
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idTaller);
            return ps.executeUpdate() > 0;
        }
    }
}
