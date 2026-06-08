package TercerSprint.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import TercerSprint.modelo.Cliente.Tipo;

/**
 * Pruebas unitarias para la clase {@link Cliente}.
 * <p>
 * Verifica el correcto funcionamiento de los constructores, getters/setters,
 * conversión de enums, validaciones y los métodos {@code equals},
 * {@code hashCode} y {@code toString}.
 * </p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class ClienteTest {

    @Test
    @DisplayName("Constructor completo crea un Cliente con todos sus atributos")
    void testConstructorCompleto() {
        Cliente c = new Cliente(1, "Mr. Increíble", "Súper fuerza", "Rojo y negro", Tipo.HEROE);
        assertEquals(1, c.getNumeroCliente());
        assertEquals("Mr. Increíble", c.getNombre());
        assertEquals("Súper fuerza", c.getSuperpoder());
        assertEquals("Rojo y negro", c.getColores());
        assertEquals(Tipo.HEROE, c.getTipo());
    }

    @Test
    @DisplayName("Constructor sin argumentos no falla y deja los valores por defecto")
    void testConstructorVacio() {
        Cliente c = new Cliente();
        assertEquals(0, c.getNumeroCliente());
        assertNull(c.getNombre());
        assertNull(c.getTipo());
    }

    @Test
    @DisplayName("Constructor lanza IllegalArgumentException si el nombre está vacío")
    void testNombreVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente(1, "", "p", "c", Tipo.HEROE));
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente(1, "   ", "p", "c", Tipo.HEROE));
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente(1, null, "p", "c", Tipo.HEROE));
    }

    @Test
    @DisplayName("setNombre lanza excepción ante valores vacíos")
    void testSetNombreVacio() {
        Cliente c = new Cliente(1, "Edna", "Diseño", "Negro", null);
        assertThrows(IllegalArgumentException.class, () -> c.setNombre(""));
        assertThrows(IllegalArgumentException.class, () -> c.setNombre(null));
    }

    @Test
    @DisplayName("Tipo.fromTexto reconoce 'Heroe' y 'Villano' (sin importar mayúsculas)")
    void testTipoFromTexto() {
        assertEquals(Tipo.HEROE, Tipo.fromTexto("Heroe"));
        assertEquals(Tipo.HEROE, Tipo.fromTexto("HEROE"));
        assertEquals(Tipo.VILLANO, Tipo.fromTexto("villano"));
        assertNull(Tipo.fromTexto(null));
        assertNull(Tipo.fromTexto("desconocido"));
    }

    @Test
    @DisplayName("equals/hashCode se basan en numeroCliente")
    void testEqualsHashCode() {
        Cliente a = new Cliente(5, "A", "p", "c", Tipo.HEROE);
        Cliente b = new Cliente(5, "B", "p2", "c2", Tipo.VILLANO);
        Cliente d = new Cliente(6, "A", "p", "c", Tipo.HEROE);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, d);
        assertNotEquals(a, null);
        assertNotEquals(a, "no-cliente");
    }

    @Test
    @DisplayName("toString incluye nombre y tipo")
    void testToString() {
        Cliente c = new Cliente(1, "Frozono", "Hielo", "Azul", Tipo.HEROE);
        String s = c.toString();
        assertTrue(s.contains("Frozono"));
        assertTrue(s.contains("Heroe"));
    }
}
