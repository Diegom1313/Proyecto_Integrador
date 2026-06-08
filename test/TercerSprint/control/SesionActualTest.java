package TercerSprint.control;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Empleado.Categoria;

/**
 * Pruebas unitarias para {@link SesionActual}.
 * Verifica el ciclo iniciar / cerrar y la consulta de la sesión activa.
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class SesionActualTest {

    @AfterEach
    void limpiarSesion() {
        SesionActual.cerrar();
    }

    @Test
    @DisplayName("haySesion es false antes de iniciar")
    void testInicialmenteSinSesion() {
        SesionActual.cerrar();
        assertFalse(SesionActual.haySesion());
        assertNull(SesionActual.getEmpleado());
    }

    @Test
    @DisplayName("iniciar guarda el empleado y haySesion devuelve true")
    void testIniciar() {
        Empleado e = new Empleado(1, "Edna Moda", "edna", Categoria.MAESTRO);
        SesionActual.iniciar(e);
        assertTrue(SesionActual.haySesion());
        assertSame(e, SesionActual.getEmpleado());
    }

    @Test
    @DisplayName("cerrar borra la sesión activa")
    void testCerrar() {
        Empleado e = new Empleado(1, "Edna Moda", "edna", Categoria.MAESTRO);
        SesionActual.iniciar(e);
        SesionActual.cerrar();
        assertFalse(SesionActual.haySesion());
        assertNull(SesionActual.getEmpleado());
    }
}
