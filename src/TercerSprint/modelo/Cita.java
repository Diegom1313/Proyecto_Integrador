package TercerSprint.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una cita en el taller de Edna Moda.
 * <p>
 * Una cita reserva un taller para atender a un cliente, trabajando sobre
 * uno de sus trajes, con un empleado responsable (Oficial o Maestro)
 * y, opcionalmente, hasta {@value #MAX_APRENDICES} aprendices.
 * </p>
 *
 * <h2>Reglas de negocio (validadas en el controlador)</h2>
 * <ul>
 *   <li>La duración mínima es {@value #DURACION_POR_DEFECTO} hora.</li>
 *   <li>Como mucho {@value #MAX_APRENDICES} aprendices por cita.</li>
 *   <li>Sin solapes en el mismo taller.</li>
 *   <li>BONUS: 1 h de margen entre héroe y villano en el mismo taller.</li>
 * </ul>
 *
 * <p>Sprint #3 — [PR4] Clase del Modelo generada a partir del diagrama de clases.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class Cita {

    /** Número máximo de aprendices que pueden asignarse a una cita. */
    public static final int MAX_APRENDICES = 2;

    /** Duración mínima de una cita en horas (también valor por defecto). */
    public static final int DURACION_POR_DEFECTO = 1;

    private int identificador;
    private LocalDate dia;
    private LocalTime hora;
    private int duracion;

    private int idCliente;
    private int idTraje;
    private int idEmpleadoResponsable;
    private int idTaller;

    private List<Integer> idsAprendices = new ArrayList<>();

    /** Constructor vacío requerido por los DAOs. */
    public Cita() {
    }

    /**
     * Crea una nueva cita con todos sus atributos principales.
     *
     * @param identificador         identificador (0 si todavía no se ha persistido).
     * @param dia                   día de la cita.
     * @param hora                  hora de inicio.
     * @param duracion              duración en horas (mínimo 1).
     * @param idCliente             identificador del cliente atendido.
     * @param idTraje               identificador del traje sobre el que se trabaja.
     * @param idEmpleadoResponsable identificador del Oficial o Maestro responsable.
     * @param idTaller              identificador del taller (sala) reservado.
     */
    public Cita(int identificador, LocalDate dia, LocalTime hora, int duracion,
                int idCliente, int idTraje, int idEmpleadoResponsable, int idTaller) {
        this.identificador = identificador;
        this.dia = dia;
        this.hora = hora;
        this.duracion = duracion <= 0 ? DURACION_POR_DEFECTO : duracion;
        this.idCliente = idCliente;
        this.idTraje = idTraje;
        this.idEmpleadoResponsable = idEmpleadoResponsable;
        this.idTaller = idTaller;
    }

    /**
     * Cambia el empleado responsable de la cita.
     * Operación del diagrama: <code>asignarEmpleado()</code>.
     *
     * @param idEmpleado identificador del nuevo responsable.
     */
    public void asignarEmpleado(int idEmpleado) {
        this.idEmpleadoResponsable = idEmpleado;
    }

    /**
     * Cambia el horario completo de la cita.
     * Operación del diagrama: <code>cambiarHorario()</code>.
     *
     * @param nuevoDia      nuevo día.
     * @param nuevaHora     nueva hora de inicio.
     * @param nuevaDuracion nueva duración en horas (mínimo 1).
     */
    public void cambiarHorario(LocalDate nuevoDia, LocalTime nuevaHora, int nuevaDuracion) {
        this.dia = nuevoDia;
        this.hora = nuevaHora;
        this.duracion = nuevaDuracion <= 0 ? DURACION_POR_DEFECTO : nuevaDuracion;
    }

    /**
     * Asigna un aprendiz adicional a la cita.
     *
     * @param idAprendiz identificador del aprendiz a añadir.
     * @return <code>true</code> si se añadió, <code>false</code> si ya estaba o si
     *         se ha alcanzado el máximo de {@link #MAX_APRENDICES}.
     */
    public boolean anadirAprendiz(int idAprendiz) {
        if (idsAprendices.size() >= MAX_APRENDICES) return false;
        if (idsAprendices.contains(idAprendiz)) return false;
        return idsAprendices.add(idAprendiz);
    }

    /**
     * Quita un aprendiz de la cita.
     *
     * @param idAprendiz identificador del aprendiz a eliminar.
     * @return <code>true</code> si se eliminó.
     */
    public boolean quitarAprendiz(int idAprendiz) {
        return idsAprendices.remove(Integer.valueOf(idAprendiz));
    }

    /**
     * Hora de finalización de la cita.
     * Útil para detectar solapes.
     *
     * @return hora de fin (hora + duración).
     */
    public LocalTime getHoraFin() {
        return hora.plusHours(duracion);
    }

    /**
     * Indica si esta cita se solapa con otra en el mismo día y taller.
     *
     * @param otra otra cita.
     * @return <code>true</code> si los intervalos horarios se solapan.
     */
    public boolean seSolapaCon(Cita otra) {
        if (otra == null) return false;
        if (this.idTaller != otra.idTaller) return false;
        if (this.dia == null || !this.dia.equals(otra.dia)) return false;
        return this.hora.isBefore(otra.getHoraFin())
                && otra.hora.isBefore(this.getHoraFin());
    }

    // ---------------------------------------------------------- getters/setters

    /** @return el identificador de la cita. */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Establece el identificador.
     *
     * @param id identificador.
     */
    public void setIdentificador(int id) {
        this.identificador = id;
    }

    /** @return el día de la cita. */
    public LocalDate getDia() {
        return dia;
    }

    /**
     * Establece el día de la cita.
     *
     * @param dia día.
     */
    public void setDia(LocalDate dia) {
        this.dia = dia;
    }

    /** @return la hora de inicio. */
    public LocalTime getHora() {
        return hora;
    }

    /**
     * Establece la hora de inicio.
     *
     * @param hora hora.
     */
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    /** @return la duración en horas. */
    public int getDuracion() {
        return duracion;
    }

    /**
     * Establece la duración. Si es menor o igual que 0 se aplica
     * {@link #DURACION_POR_DEFECTO}.
     *
     * @param duracion duración en horas.
     */
    public void setDuracion(int duracion) {
        this.duracion = duracion <= 0 ? DURACION_POR_DEFECTO : duracion;
    }

    /** @return el identificador del cliente. */
    public int getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el cliente de la cita.
     *
     * @param id identificador del cliente.
     */
    public void setIdCliente(int id) {
        this.idCliente = id;
    }

    /** @return el identificador del traje. */
    public int getIdTraje() {
        return idTraje;
    }

    /**
     * Establece el traje de la cita.
     *
     * @param id identificador del traje.
     */
    public void setIdTraje(int id) {
        this.idTraje = id;
    }

    /** @return el identificador del empleado responsable. */
    public int getIdEmpleadoResponsable() {
        return idEmpleadoResponsable;
    }

    /**
     * Establece el empleado responsable.
     *
     * @param id identificador del empleado.
     */
    public void setIdEmpleadoResponsable(int id) {
        this.idEmpleadoResponsable = id;
    }

    /** @return el identificador del taller (sala). */
    public int getIdTaller() {
        return idTaller;
    }

    /**
     * Establece el taller asociado.
     *
     * @param id identificador del taller.
     */
    public void setIdTaller(int id) {
        this.idTaller = id;
    }

    /**
     * Devuelve la lista de identificadores de aprendices asignados.
     * La lista que se devuelve es la real, no una copia.
     *
     * @return lista interna de aprendices.
     */
    public List<Integer> getIdsAprendices() {
        return idsAprendices;
    }

    /**
     * Devuelve una copia inmodificable de los aprendices asignados.
     *
     * @return lista no modificable.
     */
    public List<Integer> getIdsAprendicesInmutable() {
        return Collections.unmodifiableList(idsAprendices);
    }

    /**
     * Reemplaza la lista de aprendices.
     *
     * @param l nueva lista (si es <code>null</code> se aplica una lista vacía).
     */
    public void setIdsAprendices(List<Integer> l) {
        this.idsAprendices = (l != null ? l : new ArrayList<>());
    }

    // ---------------------------------------------------------- equals / hashCode / toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cita)) return false;
        Cita that = (Cita) o;
        return identificador == that.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    @Override
    public String toString() {
        return "Cita #" + identificador + " - " + dia + " " + hora
                + " (" + duracion + "h) cliente#" + idCliente + " taller#" + idTaller;
    }
}
