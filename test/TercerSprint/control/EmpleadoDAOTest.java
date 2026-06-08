package TercerSprint.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la utilidad {@link EmpleadoDAO#sha256(String)}.
 * <p>
 * No requieren conexión a BBDD: validan que la función produce el mismo
 * hash determinista que <code>SHA2(x, 256)</code> de MySQL para varios
 * valores conocidos.
 * </p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class EmpleadoDAOTest {

    @Test
    @DisplayName("SHA-256 de 'edna' coincide con el valor conocido (SHA2('edna',256) en MySQL)")
    void testSha256Edna() {
        String esperado = "b1ae64e29908e5f8cd8fc70df3ed92d83ae33debf74468e0565f352855913f41";
        assertEquals(esperado, EmpleadoDAO.sha256("edna"));
    }

    @Test
    @DisplayName("SHA-256 de cadena vacía es estable")
    void testSha256Vacio() {
        // SHA2('', 256) = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
        String esperado = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        assertEquals(esperado, EmpleadoDAO.sha256(""));
    }

    @Test
    @DisplayName("SHA-256 produce salidas distintas para entradas distintas")
    void testSha256Diferenciacion() {
        assertNotEquals(EmpleadoDAO.sha256("abcd"), EmpleadoDAO.sha256("abcD"));
    }

    @Test
    @DisplayName("SHA-256 es determinista para una misma entrada")
    void testSha256Determinismo() {
        assertEquals(EmpleadoDAO.sha256("hijos del compilador"),
                     EmpleadoDAO.sha256("hijos del compilador"));
    }
}
