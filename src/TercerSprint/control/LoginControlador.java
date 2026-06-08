package TercerSprint.control;

import SegundoSprint.vista.LoginVista;
import SegundoSprint.vista.VentanaPrincipal;
import TercerSprint.modelo.Empleado;

import java.sql.SQLException;

/**
 * Controlador de la ventana de login.
 * <p>
 * Une la {@link LoginVista} (Sprint 2) con el {@link EmpleadoDAO} (Sprint 3),
 * gestiona la autenticación con SHA-256 y, en caso de éxito, abre la
 * ventana principal con el rol del empleado.
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Control encargada del manejo de la aplicación.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class LoginControlador {

    private final LoginVista vista;
    private final EmpleadoDAO dao;

    /**
     * Crea el controlador y registra el listener del botón de login.
     *
     * @param vista ventana de login a controlar.
     */
    public LoginControlador(LoginVista vista) {
        this.vista = vista;
        this.dao = new EmpleadoDAO();
        this.vista.getBtnLogin().addActionListener(e -> intentarLogin());
    }

    /**
     * Intenta autenticar al empleado con las credenciales introducidas.
     * Si son válidas, abre la {@link VentanaPrincipal} y cierra la vista login.
     */
    private void intentarLogin() {
        String apodo = vista.getApodo();
        String pass  = vista.getPassword();

        if (apodo.isEmpty() || pass.isEmpty()) {
            vista.mostrarError("Por favor, rellena todos los campos.");
            return;
        }

        try {
            Empleado emp = dao.autenticar(apodo, pass);
            if (emp == null) {
                vista.mostrarError("Credenciales no válidas.");
                return;
            }
            // Sesión iniciada
            SesionActual.iniciar(emp);
            vista.limpiarError();
            vista.dispose();
            new VentanaPrincipal(emp.getApodo(), emp.getCategoria().getTexto());
        } catch (SQLException ex) {
            vista.mostrarError("Error de conexión a la BBDD.");
            ex.printStackTrace();
        }
    }
}
