package TercerSprint.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import TercerSprint.modelo.Traje.Estado;

/**
 * Pruebas unitarias para la clase {@link Traje}.
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class TrajeTest {

    @Test
    @DisplayName("Constructor completo crea un traje correctamente")
    void testConstructorCompleto() {
        Traje t = new Traje(10, 1, "Traje principal", Estado.DISENIO);
        assertEquals(10, t.getIdentificador());
        assertEquals(1, t.getIdCliente());
        assertEquals("Traje principal", t.getNombre());
        assertEquals(Estado.DISENIO, t.getEstado());
    }

    @Test
    @DisplayName("cambiarEstado actualiza el estado en memoria")
    void testCambiarEstado() {
        Traje t = new Traje(10, 1, "Principal", Estado.DISENIO);
        t.cambiarEstado(Estado.COSTURA);
        assertEquals(Estado.COSTURA, t.getEstado());
    }

    @Test
    @DisplayName("cambiarEstado con null lanza IllegalArgumentException")
    void testCambiarEstadoNull() {
        Traje t = new Traje(10, 1, "Principal", Estado.DISENIO);
        assertThrows(IllegalArgumentException.class, () -> t.cambiarEstado(null));
    }

    @Test
    @DisplayName("Estado.fromTexto resuelve los tres estados y rechaza desconocidos")
    void testEstadoFromTexto() {
        assertEquals(Estado.DISENIO, Estado.fromTexto("Diseño"));
        assertEquals(Estado.COSTURA, Estado.fromTexto("costura"));
        assertEquals(Estado.TALLER, Estado.fromTexto("Taller"));
        assertNull(Estado.fromTexto(null));
        assertThrows(IllegalArgumentException.class, () -> Estado.fromTexto("inventado"));
    }

    @Test
    @DisplayName("Constructor con nombre vacío lanza IllegalArgumentException")
    void testNombreVacio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Traje(1, 1, "", Estado.DISENIO));
        assertThrows(IllegalArgumentException.class,
                () -> new Traje(1, 1, null, Estado.DISENIO));
    }

    @Test
    @DisplayName("equals/hashCode se basan en identificador")
    void testEquals() {
        Traje a = new Traje(7, 1, "X", Estado.DISENIO);
        Traje b = new Traje(7, 99, "Otro", Estado.TALLER);
        Traje c = new Traje(8, 1, "X", Estado.DISENIO);
        assertEquals(a, b);
        assertNotEquals(a, c);
    }
}
