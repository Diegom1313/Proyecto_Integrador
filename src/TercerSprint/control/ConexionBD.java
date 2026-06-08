package TercerSprint.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria (Singleton) que gestiona la conexión con la base de datos
 * MySQL <code>edna_moda</code>.
 *
 * Sprint #3 -  Clase del Control (acceso a BBDD).
 *
 * <p><b>Uso:</b><pre>
 *     try (Connection c = ConexionBD.getConnection()) { ... }
 * </pre>
 * La conexión se cierra con try-with-resources en cada operación; esto
 * evita problemas con sesiones colgadas y simplifica los DAOs.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW
 */
public final class ConexionBD {

    // --- Configuración ---------------------------------------------------
    private static String URL      = "jdbc:mysql://localhost:3306/edna_moda"
                                   + "?useSSL=false&serverTimezone=Europe/Madrid"
                                   + "&allowPublicKeyRetrieval=true&useUnicode=true"
                                   + "&characterEncoding=UTF-8";
    private static String USUARIO  = "root";
    private static String PASSWORD = "1234";

    private ConexionBD() { /* no instanciable */ }

    /**
     * Permite sobreescribir las credenciales en tiempo de ejecución
     * (útil para tests o para cargar desde un fichero de configuración).
     */
    public static void configurar(String url, String usuario, String password) {
        URL = url;
        USUARIO = usuario;
        PASSWORD = password;
    }

    /**
     * Devuelve una nueva {@link Connection} lista para usar.
     * Llamar siempre con try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carga explícita del driver (obligatorio en algunos entornos Eclipse).
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encuentra el driver de MySQL en el classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }

    /**
     * Prueba rápida de conexión — útil en un Main de arranque.
     * @return true si la conexión se establece correctamente.
     */
    public static boolean probarConexion() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) {
            System.err.println("[ConexionBD] No se pudo conectar: " + e.getMessage());
            return false;
        }
    }
}
