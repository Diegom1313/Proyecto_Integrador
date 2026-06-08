package TercerSprint.control;

import TercerSprint.modelo.Cliente;
import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Traje;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de clientes y sus trajes.
 *
 * <h2>Permisos por rol</h2>
 * <ul>
 *   <li>Aprendiz / Oficial: solo lectura sobre clientes y trajes.</li>
 *   <li>Maestro: crear / modificar / borrar clientes y crear / borrar trajes.</li>
 *   <li>Cambiar el estado de un traje: Oficiales y Maestros.</li>
 * </ul>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del manejo de la aplicación.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class ClientesControlador {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TrajeDAO trajeDAO = new TrajeDAO();

    /**
     * Devuelve todos los clientes.
     *
     * @return lista de clientes.
     * @throws SQLException si falla la consulta.
     */
    public List<Cliente> listarClientes() throws SQLException {
        return clienteDAO.listarTodos();
    }

    /**
     * Devuelve los trajes de un cliente concreto.
     *
     * @param idCliente identificador del cliente.
     * @return lista de trajes.
     * @throws SQLException si falla la consulta.
     */
    public List<Traje> listarTrajesDe(int idCliente) throws SQLException {
        return trajeDAO.listarPorCliente(idCliente);
    }

    /**
     * Crea un nuevo cliente. Solo accesible para el Maestro.
     *
     * @param cliente datos del cliente.
     * @return id generado.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla la inserción.
     */
    public int crearCliente(Cliente cliente) throws SQLException {
        exigirMaestro();
        return clienteDAO.crear(cliente);
    }

    /**
     * Modifica un cliente existente. Solo accesible para el Maestro.
     *
     * @param cliente datos actualizados.
     * @return <code>true</code> si se modificó.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla la actualización.
     */
    public boolean modificarCliente(Cliente cliente) throws SQLException {
        exigirMaestro();
        return clienteDAO.modificar(cliente);
    }

    /**
     * Borra un cliente. Solo accesible para el Maestro.
     *
     * @param idCliente identificador.
     * @return <code>true</code> si se eliminó.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla el borrado.
     */
    public boolean borrarCliente(int idCliente) throws SQLException {
        exigirMaestro();
        return clienteDAO.borrar(idCliente);
    }

    /**
     * Crea un nuevo traje. Solo accesible para el Maestro.
     *
     * @param traje datos del traje.
     * @return id generado.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla la inserción.
     */
    public int crearTraje(Traje traje) throws SQLException {
        exigirMaestro();
        return trajeDAO.crear(traje);
    }

    /**
     * Modifica los datos de un traje (nombre y estado). Accesible para
     * Oficiales y Maestros.
     *
     * @param traje traje con id válido y datos actualizados.
     * @return <code>true</code> si se modificó.
     * @throws IllegalStateException si el usuario no tiene permisos.
     * @throws SQLException          si falla la actualización.
     */
    public boolean modificarTraje(Traje traje) throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.puedeCrearCitas()) {
            throw new IllegalStateException("Solo Oficiales o Maestros pueden modificar trajes.");
        }
        return trajeDAO.modificar(traje);
    }

    /**
     * Cambia el estado de un traje. Accesible para Oficiales y Maestros.
     *
     * @param idTraje identificador del traje.
     * @param estado  nuevo estado.
     * @return <code>true</code> si se actualizó.
     * @throws IllegalStateException si el usuario no tiene permisos.
     * @throws SQLException          si falla la actualización.
     */
    public boolean cambiarEstadoTraje(int idTraje, Traje.Estado estado) throws SQLException {
        // Oficial y Maestro pueden cambiar el estado del trabajo
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.puedeCrearCitas()) {
            throw new IllegalStateException("Solo Oficiales o Maestros pueden cambiar el estado del traje.");
        }
        return trajeDAO.cambiarEstado(idTraje, estado);
    }

    /**
     * Borra un traje. Solo accesible para el Maestro.
     *
     * @param idTraje identificador del traje.
     * @return <code>true</code> si se eliminó.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla el borrado.
     */
    public boolean borrarTraje(int idTraje) throws SQLException {
        exigirMaestro();
        return trajeDAO.borrar(idTraje);
    }

    // --------------------------------------------------- helpers

    /**
     * Comprueba que el empleado autenticado es Maestro.
     *
     * @throws IllegalStateException si no hay sesión o no es Maestro.
     */
    private void exigirMaestro() {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.esMaestro()) {
            throw new IllegalStateException("Operación reservada al Maestro.");
        }
    }
}
