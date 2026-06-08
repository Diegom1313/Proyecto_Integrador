package TercerSprint;

import SegundoSprint.vista.LoginVista;
import TercerSprint.control.ConexionBD;
import TercerSprint.control.LoginControlador;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación del Taller de Edna Moda (Sprint #3).
 * Integra Vista (Sprint 2) + Control (Sprint 3) + Modelo (Sprint 3).
 *
 * <p>Pasos:</p>
 * <ol>
 *   <li>Aplica el Look &amp; Feel del sistema.</li>
 *   <li>Comprueba la conexión con la BBDD <code>edna_moda</code>.</li>
 *   <li>Abre la ventana de login conectada con su controlador.</li>
 * </ol>
 *
 * @author Equipo Proyecto Integrador ¨HIJOS DEL COMPILADOR¨1ºDAW
 */
public class MainApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) { /* LAF por defecto */ }

        System.out.println("== Taller Edna Moda - Hijos Del Compilador ==");
        System.out.println("Probando conexión con la BBDD...");
        if (!ConexionBD.probarConexion()) {
            System.err.println("¡Atención! La BBDD no responde. Arranca MySQL y carga"
                    + " el script src/SegundoSprint/bd/edna_moda.sql antes de usar la app.");
        } else {
            System.out.println("Conexión establecida.");
        }

        SwingUtilities.invokeLater(() -> {
            LoginVista vista = new LoginVista();
            new LoginControlador(vista);
        });
    }
}
