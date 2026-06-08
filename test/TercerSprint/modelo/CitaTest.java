package TercerSprint.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase {@link Cita}.
 * Cubre constructores, validación de duración, gestión de aprendices,
 * cálculo de hora de fin y la detección de solapes.
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 */
public class CitaTest {

    private Cita ejemplo() {
        return new Cita(1, LocalDate.of(2025, 4, 12), LocalTime.of(10, 0), 2,
                10, 20, 30, 40);
    }

    @Test
    @DisplayName("Constructor aplica DURACION_POR_DEFECTO si la duración es <= 0")
    void testDuracionMinima() {
        Cita c = new Cita(1, LocalDate.now(), LocalTime.NOON, 0, 1, 1, 1, 1);
        assertEquals(Cita.DURACION_POR_DEFECTO, c.getDuracion());

        c = new Cita(1, LocalDate.now(), LocalTime.NOON, -2, 1, 1, 1, 1);
        assertEquals(Cita.DURACION_POR_DEFECTO, c.getDuracion());
    }

    @Test
    @DisplayName("setDuracion también aplica el valor por defecto si es <= 0")
    void testSetDuracion() {
        Cita c = ejemplo();
        c.setDuracion(0);
        assertEquals(Cita.DURACION_POR_DEFECTO, c.getDuracion());
        c.setDuracion(3);
        assertEquals(3, c.getDuracion());
    }

    @Test
    @DisplayName("getHoraFin devuelve hora + duración")
    void testHoraFin() {
        Cita c = ejemplo(); // 10:00 + 2h
        assertEquals(LocalTime.of(12, 0), c.getHoraFin());
    }

    @Test
    @DisplayName("anadirAprendiz respeta el máximo permitido y evita duplicados")
    void testAnadirAprendiz() {
        Cita c = ejemplo();
        assertTrue(c.anadirAprendiz(100));
        assertTrue(c.anadirAprendiz(101));
        // Tercero -> falla
        assertFalse(c.anadirAprendiz(102));
        // Repetido -> falla
        assertFalse(c.anadirAprendiz(100));
        assertEquals(2, c.getIdsAprendices().size());
    }

    @Test
    @DisplayName("quitarAprendiz elimina la asociación")
    void testQuitarAprendiz() {
        Cita c = ejemplo();
        c.anadirAprendiz(100);
        assertTrue(c.quitarAprendiz(100));
        assertFalse(c.quitarAprendiz(100)); // ya no está
    }

    @Test
    @DisplayName("seSolapaCon detecta solapes solo en el mismo taller y día")
    void testSeSolapaCon() {
        Cita base = ejemplo(); // taller 40, día 12-04-2025, 10:00 - 12:00
        Cita solapada = new Cita(2, LocalDate.of(2025, 4, 12),
                LocalTime.of(11, 0), 2, 11, 21, 31, 40);
        Cita despues = new Cita(3, LocalDate.of(2025, 4, 12),
                LocalTime.of(13, 0), 1, 11, 21, 31, 40);
        Cita otroTaller = new Cita(4, LocalDate.of(2025, 4, 12),
                LocalTime.of(11, 0), 1, 11, 21, 31, 41);
        Cita otroDia = new Cita(5, LocalDate.of(2025, 4, 13),
                LocalTime.of(11, 0), 1, 11, 21, 31, 40);

        assertTrue(base.seSolapaCon(solapada));
        assertFalse(base.seSolapaCon(despues));
        assertFalse(base.seSolapaCon(otroTaller));
        assertFalse(base.seSolapaCon(otroDia));
        assertFalse(base.seSolapaCon(null));
    }

    @Test
    @DisplayName("cambiarHorario actualiza día, hora y duración")
    void testCambiarHorario() {
        Cita c = ejemplo();
        c.cambiarHorario(LocalDate.of(2025, 5, 1), LocalTime.of(8, 30), 3);
        assertEquals(LocalDate.of(2025, 5, 1), c.getDia());
        assertEquals(LocalTime.of(8, 30), c.getHora());
        assertEquals(3, c.getDuracion());
    }

    @Test
    @DisplayName("equals/hashCode se basan solo en el identificador")
    void testEquals() {
        Cita a = new Cita(7, LocalDate.now(), LocalTime.NOON, 1, 1, 1, 1, 1);
        Cita b = new Cita(7, LocalDate.now(), LocalTime.MIDNIGHT, 5, 9, 9, 9, 9);
        Cita d = new Cita(8, LocalDate.now(), LocalTime.NOON, 1, 1, 1, 1, 1);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, d);
    }

    @Test
    @DisplayName("Constantes MAX_APRENDICES y DURACION_POR_DEFECTO tienen valores fijos")
    void testConstantes() {
        assertEquals(2, Cita.MAX_APRENDICES);
        assertEquals(1, Cita.DURACION_POR_DEFECTO);
    }
}
