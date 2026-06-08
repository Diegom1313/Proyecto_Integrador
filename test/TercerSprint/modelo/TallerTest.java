package TercerSprint.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import TercerSprint.modelo.Taller.TipoSala;

/**
 * Pruebas unitarias para la clase {@link Taller}.
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class TallerTest {

    @Test
    @DisplayName("Constructor crea un taller con los datos esperados")
    void testConstructor() {
        Taller t = new Taller(1, "París", TipoSala.DISENIO);
        assertEquals(1, t.getNumeroTaller());
        assertEquals("París", t.getNombreSala());
        assertEquals(TipoSala.DISENIO, t.getTipoSala());
    }

    @Test
    @DisplayName("Constructor con datos inválidos lanza IllegalArgumentException")
    void testConstructorInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Taller(1, "", TipoSala.DISENIO));
        assertThrows(IllegalArgumentException.class,
                () -> new Taller(1, null, TipoSala.DISENIO));
        assertThrows(IllegalArgumentException.class,
                () -> new Taller(1, "Madrid", null));
    }

    @Test
    @DisplayName("modificarTaller actualiza nombre y tipo")
    void testModificar() {
        Taller t = new Taller(1, "París", TipoSala.DISENIO);
        t.modificarTaller("Roma", TipoSala.PRUEBAS);
        assertEquals("Roma", t.getNombreSala());
        assertEquals(TipoSala.PRUEBAS, t.getTipoSala());
    }

    @Test
    @DisplayName("modificarTaller ignora valores nulos o vacíos")
    void testModificarConValoresInvalidos() {
        Taller t = new Taller(1, "París", TipoSala.DISENIO);
        t.modificarTaller(null, null);
        assertEquals("París", t.getNombreSala());
        assertEquals(TipoSala.DISENIO, t.getTipoSala());

        t.modificarTaller("   ", null);
        assertEquals("París", t.getNombreSala());
    }

    @Test
    @DisplayName("TipoSala.fromTexto reconoce los tres tipos y rechaza desconocidos")
    void testTipoSalaFromTexto() {
        assertEquals(TipoSala.DISENIO, TipoSala.fromTexto("Diseño"));
        assertEquals(TipoSala.COSTURA, TipoSala.fromTexto("Costura"));
        assertEquals(TipoSala.PRUEBAS, TipoSala.fromTexto("PRUEBAS"));
        assertNull(TipoSala.fromTexto(null));
        assertThrows(IllegalArgumentException.class, () -> TipoSala.fromTexto("xxx"));
    }
}
