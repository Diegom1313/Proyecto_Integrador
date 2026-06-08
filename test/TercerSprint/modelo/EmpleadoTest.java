package TercerSprint.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import TercerSprint.modelo.Empleado.Categoria;

/**
 * Pruebas unitarias para la clase {@link Empleado}.
 * Verifica permisos por rol y conversión de la enumeración Categoría.
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class EmpleadoTest {

    @Test
    @DisplayName("Maestro puede crear citas y es Maestro")
    void testPermisosMaestro() {
        Empleado m = new Empleado(1, "Edna Moda", "edna", Categoria.MAESTRO);
        assertTrue(m.puedeCrearCitas());
        assertTrue(m.esMaestro());
    }

    @Test
    @DisplayName("Oficial puede crear citas pero no es Maestro")
    void testPermisosOficial() {
        Empleado o = new Empleado(2, "Bob Parr", "mrincreible", Categoria.OFICIAL);
        assertTrue(o.puedeCrearCitas());
        assertFalse(o.esMaestro());
    }

    @Test
    @DisplayName("Aprendiz no puede crear citas ni es Maestro")
    void testPermisosAprendiz() {
        Empleado a = new Empleado(3, "Violet Parr", "violet", Categoria.APRENDIZ);
        assertFalse(a.puedeCrearCitas());
        assertFalse(a.esMaestro());
    }

    @Test
    @DisplayName("Categoria.fromTexto reconoce las tres categorías")
    void testCategoriaFromTexto() {
        assertEquals(Categoria.APRENDIZ, Categoria.fromTexto("Aprendiz"));
        assertEquals(Categoria.OFICIAL, Categoria.fromTexto("oficial"));
        assertEquals(Categoria.MAESTRO, Categoria.fromTexto("MAESTRO"));
        assertNull(Categoria.fromTexto(null));
        assertThrows(IllegalArgumentException.class,
                () -> Categoria.fromTexto("Director"));
    }

    @Test
    @DisplayName("Constructor con hash guarda el password sin exponerlo en toString")
    void testConstructorConHash() {
        Empleado e = new Empleado(1, "X Y", "xy", Categoria.OFICIAL, "hashabc123");
        assertEquals("hashabc123", e.getPasswordHash());
        assertFalse(e.toString().contains("hashabc123"));
    }
}
