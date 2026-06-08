package TercerSprint.control;

import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Empleado.Categoria;
import TercerSprint.modelo.Taller;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de talleres (salas).
 *
 * <h2>Permisos por rol</h2>
 * <ul>
 *   <li>Aprendiz: sin acceso.</li>
 *   <li>Oficial: solo puede consultar la lista.</li>
 *   <li>Maestro: añadir, modificar y borrar talleres.</li>
 * </ul>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del manejo de la aplicación.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class TalleresControlador {

    private final TallerDAO tallerDAO = new TallerDAO();

    /**
     * Devuelve la lista de talleres disponibles para el rol actual.
     *
     * @return lista de talleres.
     * @throws IllegalStateException si no hay sesión o el rol es Aprendiz.
     * @throws SQLException          si falla la consulta.
     */
    public List<Taller> listarTalleres() throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || emp.getCategoria() == Categoria.APRENDIZ) {
            throw new IllegalStateException("Sin permisos para ver talleres.");
        }
        return tallerDAO.listarTodos();
    }

    /**
     * Crea un nuevo taller. Solo accesible para el Maestro.
     *
     * @param taller datos del taller.
     * @return id generado.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla la inserción.
     */
    public int crearTaller(Taller taller) throws SQLException {
        exigirMaestro();
        return tallerDAO.crear(taller);
    }

    /**
     * Modifica un taller existente. Solo accesible para el Maestro.
     *
     * @param taller datos actualizados.
     * @return <code>true</code> si se modificó la fila.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla la actualización.
     */
    public boolean modificarTaller(Taller taller) throws SQLException {
        exigirMaestro();
        return tallerDAO.modificar(taller);
    }

    /**
     * Elimina un taller. Solo accesible para el Maestro.
     *
     * @param idTaller identificador del taller.
     * @return <code>true</code> si se eliminó la fila.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla el borrado.
     */
    public boolean borrarTaller(int idTaller) throws SQLException {
        exigirMaestro();
        return tallerDAO.borrar(idTaller);
    }

    /**
     * Lanza una excepción si la sesión actual no corresponde a un Maestro.
     *
     * @throws IllegalStateException si no hay sesión o el empleado no es Maestro.
     */
    private void exigirMaestro() {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.esMaestro()) {
            throw new IllegalStateException("Operación reservada al Maestro.");
        }
    }
}
