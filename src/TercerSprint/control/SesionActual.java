package TercerSprint.control;

import TercerSprint.modelo.Empleado;

/**
 * Mantiene la referencia al empleado autenticado en la aplicación.
 * <p>
 * Implementa un singleton estático sencillo accesible desde cualquier
 * controlador o vista. Permite consultar permisos sin propagar el
 * objeto {@link Empleado} a través de toda la jerarquía MVC.
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del manejo de la aplicación.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public final class SesionActual {

    private static Empleado empleadoLogueado;

    /** Constructor privado: no instanciable. */
    private SesionActual() {
    }

    /**
     * Marca al empleado como autenticado en la sesión actual.
     *
     * @param e empleado autenticado.
     */
    public static void iniciar(Empleado e) {
        empleadoLogueado = e;
    }

    /** Cierra la sesión actual borrando la referencia al empleado. */
    public static void cerrar() {
        empleadoLogueado = null;
    }

    /**
     * @return el empleado actualmente autenticado, o <code>null</code> si no hay sesión.
     */
    public static Empleado getEmpleado() {
        return empleadoLogueado;
    }

    /**
     * @return <code>true</code> si hay un empleado autenticado.
     */
    public static boolean haySesion() {
        return empleadoLogueado != null;
    }
}
