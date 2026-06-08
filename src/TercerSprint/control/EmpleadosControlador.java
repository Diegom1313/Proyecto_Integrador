package TercerSprint.control;

import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Empleado.Categoria;

import java.sql.SQLException;
import java.util.List;

/**
 * Controlador de la gestión de empleados del taller.
 *
 * <h2>Permisos</h2>
 * <ul>
 *   <li>Todas las operaciones requieren que el usuario autenticado sea <b>Maestro</b>.</li>
 * </ul>
 *
 * <h2>Reglas de negocio</h2>
 * <ul>
 *   <li>El apodo debe ser único en toda la plantilla.</li>
 *   <li>Un Maestro no puede borrarse ni rebajarse de categoría a sí mismo
 *       (medida de seguridad para evitar quedarse sin acceso).</li>
 *   <li>Debe quedar siempre al menos un Maestro en la plantilla.</li>
 *   <li>La contraseña por defecto al crear o restablecer es <code>1234</code>.</li>
 *   <li>No se permite eliminar empleados que sean responsables de alguna cita
 *       (FK <code>fk_cita_empleado</code> con <code>ON DELETE RESTRICT</code>);
 *       el {@link MensajesError} traduce el error a un mensaje legible.</li>
 * </ul>
 *
 * <p>Sprint #4 — [PR5] Gestión de empleados desde la aplicación.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #4
 */
public class EmpleadosControlador {

    /** Contraseña por defecto al crear o restablecer un empleado. */
    public static final String PASSWORD_POR_DEFECTO = "1234";

    /** Longitud mínima exigida a las contraseñas establecidas manualmente. */
    public static final int PASSWORD_MIN_LENGTH = 4;

    /**
     * Apodo del empleado EdnaM <b>blindado</b> del sistema. Edna Moda es la dueña
     * del taller y debe estar siempre presente: no se puede borrar, ni rebajar
     * de categoría, ni cambiarle el apodo. Sí se puede editar su nombre / apellidos
     * y restablecerle la contraseña.
     */
    public static final String APODO_BLINDADO = "EdnaM";

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

    /** @return <code>true</code> si el apodo corresponde al empleado blindado. */
    private boolean esEmpleadoBlindado(String apodo) {
        return apodo != null && APODO_BLINDADO.equalsIgnoreCase(apodo.trim());
    }

    /**
     * Devuelve todos los empleados.
     *
     * @return lista de empleados (sin exponer hash de contraseña en la vista).
     * @throws SQLException          si falla la consulta.
     * @throws IllegalStateException si el usuario no es Maestro.
     */
    public List<Empleado> listarEmpleados() throws SQLException {
        exigirMaestro();
        return empleadoDAO.listarTodos();
    }

    /**
     * Crea un nuevo empleado. La contraseña se cifra con SHA-256.
     *
     * @param nombre          nombre.
     * @param apellidos       apellidos.
     * @param apodo           apodo único.
     * @param categoria       categoría profesional.
     * @param passwordEnClaro contraseña en claro (mínimo {@link #PASSWORD_MIN_LENGTH} caracteres).
     * @return el id generado.
     * @throws IllegalArgumentException si algún dato es inválido o el apodo ya existe.
     * @throws IllegalStateException    si el usuario no es Maestro.
     * @throws SQLException             si falla la inserción.
     */
    public int crearEmpleado(String nombre, String apellidos, String apodo,
                             Categoria categoria, String passwordEnClaro) throws SQLException {
        exigirMaestro();
        validarDatos(nombre, apellidos, apodo, categoria);
        validarPassword(passwordEnClaro);
        // Impide suplantar el apodo blindado con variaciones de mayúsculas.
        if (esEmpleadoBlindado(apodo)) {
            throw new IllegalArgumentException(
                    "El apodo \"" + APODO_BLINDADO + "\" está reservado a la "
                  + "propietaria del taller. Elige otro apodo distinto.");
        }
        if (empleadoDAO.existeApodo(apodo, 0)) {
            throw new IllegalArgumentException(
                    "Ya existe un empleado con el apodo \"" + apodo + "\". "
                  + "Elige otro distinto.");
        }
        Empleado e = new Empleado();
        e.setApodo(apodo.trim());
        e.setCategoria(categoria);
        return empleadoDAO.crear(e, nombre.trim(), apellidos.trim(), passwordEnClaro);
    }

    /**
     * Modifica los datos de un empleado (sin tocar la contraseña).
     *
     * @param idEmpleado id del empleado a modificar.
     * @param nombre     nombre.
     * @param apellidos  apellidos.
     * @param apodo      apodo único.
     * @param categoria  categoría profesional.
     * @return <code>true</code> si se modificó.
     * @throws IllegalArgumentException si algún dato es inválido o el apodo está duplicado.
     * @throws IllegalStateException    si el usuario no es Maestro o si intenta
     *                                  rebajar su propia categoría.
     * @throws SQLException             si falla la actualización.
     */
    public boolean modificarEmpleado(int idEmpleado, String nombre, String apellidos,
                                     String apodo, Categoria categoria) throws SQLException {
        exigirMaestro();
        validarDatos(nombre, apellidos, apodo, categoria);

        // Protección del empleado blindado (Edna Moda).
        Empleado actualBlindado = empleadoDAO.buscarPorId(idEmpleado);
        if (actualBlindado != null && esEmpleadoBlindado(actualBlindado.getApodo())) {
            if (!esEmpleadoBlindado(apodo)) {
                throw new IllegalStateException(
                        "El apodo de " + APODO_BLINDADO + " no se puede cambiar: "
                      + "es la propietaria del taller y debe permanecer inalterable.");
            }
            
            if (categoria != Categoria.MAESTRO) {
                throw new IllegalStateException(
                        APODO_BLINDADO + " no puede dejar de ser Maestro: "
                      + "es la dueña del taller.");
            }
        }

        Empleado sesion = SesionActual.getEmpleado();
        // Un Maestro no puede rebajar su propia categoría — perdería el acceso.
        if (sesion != null && sesion.getIdentificador() == idEmpleado
                && categoria != Categoria.MAESTRO) {
            throw new IllegalStateException(
                    "No puedes cambiar tu propia categoría: perderías el acceso "
                  + "como Maestro.");
        }
        // Si se está rebajando un Maestro y es el último, no se permite.
        if (categoria != Categoria.MAESTRO) {
            Empleado actual = empleadoDAO.buscarPorId(idEmpleado);
            if (actual != null && actual.getCategoria() == Categoria.MAESTRO
                    && empleadoDAO.contarPorCategoria(Categoria.MAESTRO) <= 1) {
                throw new IllegalStateException(
                        "Debe haber al menos un Maestro en la plantilla. "
                      + "No puedes rebajar al único Maestro existente.");
            }
        }
        if (empleadoDAO.existeApodo(apodo, idEmpleado)) {
            throw new IllegalArgumentException(
                    "Ya existe otro empleado con el apodo \"" + apodo + "\".");
        }
        Empleado e = new Empleado();
        e.setIdentificador(idEmpleado);
        e.setApodo(apodo.trim());
        e.setCategoria(categoria);
        return empleadoDAO.modificar(e, nombre.trim(), apellidos.trim());
    }

    /**
     * Borra un empleado.
     *
     * @param idEmpleado identificador.
     * @return <code>true</code> si se eliminó.
     * @throws IllegalStateException si el usuario no es Maestro, si intenta
     *                               borrarse a sí mismo, o si intenta borrar
     *                               al último Maestro.
     * @throws SQLException          si falla el borrado o el empleado es responsable
     *                               de alguna cita (FK RESTRICT).
     */
    public boolean borrarEmpleado(int idEmpleado) throws SQLException {
        exigirMaestro();

        // Protección absoluta del empleado blindado (Edna Moda):
        // no se puede borrar bajo ninguna circunstancia.
        Empleado emp = empleadoDAO.buscarPorId(idEmpleado);
        if (emp != null && esEmpleadoBlindado(emp.getApodo())) {
            throw new IllegalStateException(
                    APODO_BLINDADO + " es la propietaria del taller y no puede "
                  + "borrarse bajo ninguna circunstancia, ni siquiera aunque "
                  + "existan otros Maestros en la plantilla.");
        }

        Empleado sesion = SesionActual.getEmpleado();
        if (sesion != null && sesion.getIdentificador() == idEmpleado) {
            throw new IllegalStateException(
                    "No puedes borrarte a ti mismo mientras estás conectado.");
        }
        if (emp != null && emp.getCategoria() == Categoria.MAESTRO
                && empleadoDAO.contarPorCategoria(Categoria.MAESTRO) <= 1) {
            throw new IllegalStateException(
                    "No puedes borrar al único Maestro de la plantilla. "
                  + "Asciende antes a otro empleado a Maestro.");
        }
        return empleadoDAO.borrar(idEmpleado);
    }

    /**
     * Restablece la contraseña del empleado al valor por defecto
     * {@link #PASSWORD_POR_DEFECTO} ("1234").
     *
     * @param idEmpleado identificador del empleado.
     * @return <code>true</code> si se actualizó la fila.
     * @throws IllegalStateException si el usuario no es Maestro.
     * @throws SQLException          si falla la operación.
     */
    public boolean restablecerPassword(int idEmpleado) throws SQLException {
        exigirMaestro();
        return empleadoDAO.cambiarPassword(idEmpleado, PASSWORD_POR_DEFECTO);
    }

    /**
     * Cambia la contraseña del empleado a un valor concreto.
     *
     * @param idEmpleado      identificador del empleado.
     * @param passwordEnClaro nueva contraseña en claro.
     * @return <code>true</code> si se actualizó la fila.
     * @throws IllegalArgumentException si la contraseña no cumple las reglas.
     * @throws IllegalStateException    si el usuario no es Maestro.
     * @throws SQLException             si falla la operación.
     */
    public boolean cambiarPassword(int idEmpleado, String passwordEnClaro) throws SQLException {
        exigirMaestro();
        validarPassword(passwordEnClaro);
        return empleadoDAO.cambiarPassword(idEmpleado, passwordEnClaro);
    }

    /**
     * Cambia la contraseña del usuario que tiene la sesión abierta.
     * <p>
     * <b>No exige rol Maestro</b>: cualquier empleado (Aprendiz, Oficial o Maestro)
     * puede cambiar <i>su propia</i> contraseña, siempre que demuestre conocer la
     * contraseña actual. De este modo, si alguien se deja la sesión abierta,
     * otro usuario no puede secuestrarle la cuenta cambiándole la contraseña.
     * </p>
     *
     * @param passwordActual contraseña actual del usuario en sesión.
     * @param passwordNueva  nueva contraseña en claro.
     * @return <code>true</code> si se actualizó la fila.
     * @throws IllegalArgumentException si la nueva contraseña no cumple las reglas
     *                                  o la actual es incorrecta.
     * @throws IllegalStateException    si no hay sesión activa.
     * @throws SQLException             si falla la operación.
     */
    public boolean cambiarMiPassword(String passwordActual, String passwordNueva) throws SQLException {
        Empleado sesion = SesionActual.getEmpleado();
        if (sesion == null) {
            throw new IllegalStateException(
                    "No hay ningún usuario con la sesión iniciada.");
        }
        validarPassword(passwordNueva);

        // Comprobar la contraseña actual: re-autenticamos por apodo + hash
        // y rechazamos si no coincide con el del empleado en sesión.
        Empleado verificado = empleadoDAO.autenticar(sesion.getApodo(),
                passwordActual == null ? "" : passwordActual);
        if (verificado == null || verificado.getIdentificador() != sesion.getIdentificador()) {
            throw new IllegalArgumentException(
                    "La contraseña actual no es correcta.\n"
                  + "Si no la recuerdas, contacta con un Maestro del taller "
                  + "para que te la restablezca.");
        }
        if (passwordActual != null && passwordActual.equals(passwordNueva)) {
            throw new IllegalArgumentException(
                    "La nueva contraseña debe ser distinta de la actual.");
        }
        return empleadoDAO.cambiarPassword(sesion.getIdentificador(), passwordNueva);
    }

    // ----------------------------------------------------------- validaciones

    /** Lanza IllegalArgumentException si algún dato básico no es válido. */
    private void validarDatos(String nombre, String apellidos, String apodo,
                              Categoria categoria) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios.");
        }
        if (apodo == null || apodo.trim().isEmpty()) {
            throw new IllegalArgumentException("El apodo es obligatorio.");
        }
        if (apodo.contains(" ")) {
            throw new IllegalArgumentException("El apodo no puede contener espacios.");
        }
        if (apodo.length() > 50) {
            throw new IllegalArgumentException(
                    "El apodo no puede tener más de 50 caracteres.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría es obligatoria.");
        }
    }

    /** Lanza IllegalArgumentException si la contraseña no cumple las reglas. */
    private void validarPassword(String pass) {
        if (pass == null || pass.length() < PASSWORD_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos " + PASSWORD_MIN_LENGTH
                  + " caracteres.");
        }
        if (pass.contains(" ")) {
            throw new IllegalArgumentException(
                    "La contraseña no puede contener espacios en blanco.");
        }
    }

    /** Comprueba que el empleado autenticado es Maestro. */
    private void exigirMaestro() {
        Empleado emp = SesionActual.getEmpleado();
        if (emp == null || !emp.esMaestro()) {
            throw new IllegalStateException(
                    "Solo el Maestro puede gestionar la plantilla de empleados.");
        }
    }
}
