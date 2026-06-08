package TercerSprint.control;

import TercerSprint.modelo.Cita;
import TercerSprint.modelo.Cliente;
import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Empleado.Categoria;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador de la gestión de citas.
 * Aplica las reglas de negocio 
 * <ul>
 *   <li>Aprendiz: solo ve sus citas, no crea ni modifica.</li>
 *   <li>Oficial:  ve todas, solo modifica las suyas, puede crear.</li>
 *   <li>Maestro:  acceso total.</li>
 *   <li>Máximo 2 aprendices por cita.</li>
 *   <li>BONUS: 1 h de margen entre citas de héroe y villano en el mismo taller.</li>
 * </ul>
 *
 * Sprint #3 - Clase del Control encargada del manejo de la aplicación.
 *
 * @author Equipo Proyecto Integrador hijos Del Compilador 1ºDAW
 */
public class CitasControlador {

    private final CitaDAO citaDAO = new CitaDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    // =====================================================================
    // Consultas
    // =====================================================================
    /**
     * Devuelve las citas visibles para el empleado logueado:
     * - Aprendiz: solo las suyas.
     * - Oficial/Maestro: todas.
     */
    public List<Cita> listarCitasVisibles() throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null) throw new IllegalStateException("No hay sesión iniciada.");

        if (emp.getCategoria() == Categoria.APRENDIZ) {
            return citaDAO.listarPorEmpleado(emp.getIdentificador());
        }
        return citaDAO.listarTodas();
    }

    public List<Cita> listarMisCitas() throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        return citaDAO.listarPorEmpleado(emp.getIdentificador());
    }

    // =====================================================================
    // Creación de citas
    // =====================================================================
    /**
     * Crea una cita validando permisos y reglas de negocio.
     * @throws IllegalStateException si alguna regla no se cumple.
     */
    public int crearCita(Cita cita) throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.puedeCrearCitas()) {
            throw new IllegalStateException("Solo Oficiales y Maestros pueden crear citas.");
        }

        if (cita.getDuracion() < 1) {
            throw new IllegalStateException("La duración mínima es 1 hora.");
        }
        if (cita.getIdsAprendices() != null && cita.getIdsAprendices().size() > Cita.MAX_APRENDICES) {
            throw new IllegalStateException("Máximo " + Cita.MAX_APRENDICES + " aprendices por cita.");
        }
        if (haySolape(cita, -1)) {
            throw new IllegalStateException("El taller ya está ocupado en ese horario.");
        }
        if (violaReglaHeroeVillano(cita, -1)) {
            throw new IllegalStateException(
                "No se puede agendar: héroe y villano requieren 1 h de margen en el mismo taller.");
        }

        // Un Oficial se asigna a sí mismo como responsable (no puede asignar a otro)
        if (emp.getCategoria() == Categoria.OFICIAL) {
            cita.setIdEmpleadoResponsable(emp.getIdentificador());
        }

        return citaDAO.crear(cita);
    }

    // =====================================================================
    // Modificación
    // =====================================================================

    /**
     * Modifica una cita existente.
     *
     * <h3>Reglas aplicadas</h3>
     * <ul>
     *   <li>Aprendiz: no puede modificar citas.</li>
     *   <li>Oficial: puede modificar las citas en las que figura como responsable.</li>
     *   <li>Maestro: puede modificar cualquier cita (las suyas y las del resto).</li>
     * </ul>
     *
     * @param cita cita con id válido y datos actualizados.
     * @return <code>true</code> si se modificó al menos una fila.
     * @throws IllegalStateException si no se cumple alguna de las reglas.
     * @throws SQLException          si falla la operación contra la BBDD.
     */
    public boolean modificarCita(Cita cita) throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null) throw new IllegalStateException("Sin sesión.");

        Cita existente = citaDAO.buscarPorId(cita.getIdentificador());
        if (existente == null) throw new IllegalStateException("La cita no existe.");

        if (emp.getCategoria() == Categoria.APRENDIZ) {
            throw new IllegalStateException("Los aprendices no pueden modificar citas.");
        }
        // Oficial solo puede modificar las suyas
        if (emp.getCategoria() == Categoria.OFICIAL
                && existente.getIdEmpleadoResponsable() != emp.getIdentificador()) {
            throw new IllegalStateException("Como Oficial solo puedes modificar tus propias citas.");
        }
        if (cita.getDuracion() < 1) {
            throw new IllegalStateException("La duración mínima es 1 hora.");
        }
        if (cita.getIdsAprendices() != null && cita.getIdsAprendices().size() > Cita.MAX_APRENDICES) {
            throw new IllegalStateException("Máximo " + Cita.MAX_APRENDICES + " aprendices por cita.");
        }
        if (haySolape(cita, cita.getIdentificador())) {
            throw new IllegalStateException("El taller ya está ocupado en ese horario.");
        }
        if (violaReglaHeroeVillano(cita, cita.getIdentificador())) {
            throw new IllegalStateException(
                "Héroe y villano necesitan 1 h de margen en el mismo taller.");
        }
        // Un Oficial no puede transferir la cita a otro empleado
        if (emp.getCategoria() == Categoria.OFICIAL) {
            cita.setIdEmpleadoResponsable(emp.getIdentificador());
        }
        return citaDAO.modificar(cita);
    }

    // =====================================================================
    // Borrado
    // =====================================================================
    public boolean borrarCita(int idCita) throws SQLException {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.esMaestro()) {
            throw new IllegalStateException("Solo el Maestro puede borrar citas.");
        }
        return citaDAO.borrar(idCita);
    }

    // =====================================================================
    // Reglas de negocio auxiliares
    // =====================================================================
    /**
     * ¿Hay solape horario con otra cita en el mismo taller y día?
     * @param idCitaIgnorar id a ignorar (para modificaciones), -1 si no aplica.
     */
    private boolean haySolape(Cita nueva, int idCitaIgnorar) throws SQLException {
        List<Cita> mismas = citaDAO.listarPorTallerYDia(nueva.getIdTaller(), nueva.getDia());
        LocalTime ini = nueva.getHora();
        LocalTime fin = nueva.getHoraFin();
        for (Cita otra : mismas) {
            if (otra.getIdentificador() == idCitaIgnorar) continue;
            LocalTime ini2 = otra.getHora();
            LocalTime fin2 = otra.getHoraFin();
            // Solape si (ini < fin2) && (ini2 < fin)
            if (ini.isBefore(fin2) && ini2.isBefore(fin)) return true;
        }
        return false;
    }

    /**
     * Regla BONUS: héroe y villano en el mismo taller necesitan &gt;= 1 h
     * de separación entre la cita anterior y la nueva.
     */
    private boolean violaReglaHeroeVillano(Cita nueva, int idCitaIgnorar) throws SQLException {
        Cliente clienteNueva = clienteDAO.buscarPorId(nueva.getIdCliente());
        if (clienteNueva == null || clienteNueva.getTipo() == null) return false;

        List<Cita> mismas = citaDAO.listarPorTallerYDia(nueva.getIdTaller(), nueva.getDia());
        for (Cita otra : mismas) {
            if (otra.getIdentificador() == idCitaIgnorar) continue;
            Cliente c2 = clienteDAO.buscarPorId(otra.getIdCliente());
            if (c2 == null || c2.getTipo() == null) continue;
            // Solo nos importa si son de tipos opuestos
            if (c2.getTipo() == clienteNueva.getTipo()) continue;

            // Margen entre fin de otra y comienzo de nueva (y viceversa)
            LocalTime ini = nueva.getHora();
            LocalTime fin = nueva.getHoraFin();
            LocalTime ini2 = otra.getHora();
            LocalTime fin2 = otra.getHoraFin();

            // Caso 1: otra termina y después empieza la nueva
            if (!ini.isBefore(fin2)) {
                long gap = java.time.Duration.between(fin2, ini).toMinutes();
                if (gap < 60) return true;
            }
            // Caso 2: nueva termina y después empieza otra
            else if (!ini2.isBefore(fin)) {
                long gap = java.time.Duration.between(fin, ini2).toMinutes();
                if (gap < 60) return true;
            }
            else {
                // Solapadas - ya detectado por haySolape, pero por seguridad
                return true;
            }
        }
        return false;
    }

    /**
     * Factoría de conveniencia para crear un objeto Cita a partir de los
     * datos básicos.
     */
    public static Cita construir(int idCliente, int idTraje, int idEmpleadoResponsable,
                                 int idTaller, LocalDate dia, LocalTime hora, int duracion) {
        return new Cita(0, dia, hora, duracion,
                idCliente, idTraje, idEmpleadoResponsable, idTaller);
    }
}
