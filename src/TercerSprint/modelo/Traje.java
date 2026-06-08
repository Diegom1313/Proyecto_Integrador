package TercerSprint.modelo;

import java.util.Objects;

/**
 * Representa un traje perteneciente a un cliente del taller.
 * <p>
 * Cada cliente puede tener varios trajes en distintos estados de elaboración.
 * La clase se corresponde con la tabla <code>trajes</code> de la base de datos.
 * </p>
 *
 * <h2>Atributos principales</h2>
 * <ul>
 *   <li><b>identificador</b>: PK del traje.</li>
 *   <li><b>idCliente</b>: FK al cliente propietario.</li>
 *   <li><b>nombre</b>: descripción libre (p.&nbsp;ej. "Traje principal").</li>
 *   <li><b>estado</b>: DISENIO / COSTURA / TALLER, según el ciclo de vida.</li>
 * </ul>
 *
 * <p>Sprint #3 — [PR4] Clase del Modelo generada a partir del diagrama de clases.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class Traje {

    /**
     * Estados posibles del traje según el diagrama de clases del proyecto.
     * El texto que devuelve {@link #getTexto()} coincide con el valor
     * almacenado en la columna <code>estado</code> de la BBDD.
     */
    public enum Estado {
        /** Fase inicial de bocetos y diseño. */
        DISENIO("Diseño"),
        /** Fase de confección y costura. */
        COSTURA("Costura"),
        /** Fase de pruebas y ajustes finales. */
        TALLER("Taller");

        private final String texto;

        Estado(String t) {
            this.texto = t;
        }

        /** @return la representación textual del estado. */
        public String getTexto() {
            return texto;
        }

        /**
         * Convierte la cadena almacenada en BBDD al valor del enum.
         *
         * @param texto cadena a convertir.
         * @return el valor del enum equivalente.
         * @throws IllegalArgumentException si <code>texto</code> no es un estado válido.
         */
        public static Estado fromTexto(String texto) {
            if (texto == null) return null;
            for (Estado e : values()) {
                if (e.texto.equalsIgnoreCase(texto)) return e;
            }
            throw new IllegalArgumentException("Estado desconocido: " + texto);
        }
    }

    private int identificador;
    private String nombre;
    private Estado estado;
    private int idCliente;

    /** Constructor por defecto requerido por los DAOs. */
    public Traje() {
    }

    /**
     * Crea un nuevo traje con todos sus atributos.
     *
     * @param identificador identificador del traje (0 si todavía no se ha persistido).
     * @param idCliente     identificador del cliente propietario.
     * @param nombre        descripción del traje (no nulo ni vacío).
     * @param estado        estado actual del traje.
     * @throws IllegalArgumentException si el nombre es nulo o vacío.
     */
    public Traje(int identificador, int idCliente, String nombre, Estado estado) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del traje no puede estar vacío.");
        }
        this.identificador = identificador;
        this.idCliente = idCliente;
        this.nombre = nombre.trim();
        this.estado = estado;
    }

    /**
     * Cambia el estado del traje en memoria.
     * Operación del diagrama: <code>cambiarEstado()</code>.
     *
     * @param nuevoEstado nuevo estado (no nulo).
     * @throws IllegalArgumentException si <code>nuevoEstado</code> es <code>null</code>.
     */
    public void cambiarEstado(Estado nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = nuevoEstado;
    }

    // ---------------------------------------------------------- getters/setters

    /** @return el identificador del traje. */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Establece el identificador del traje.
     *
     * @param id nuevo identificador.
     */
    public void setIdentificador(int id) {
        this.identificador = id;
    }

    /** @return el identificador del cliente propietario. */
    public int getIdCliente() {
        return idCliente;
    }

    /**
     * Establece el cliente propietario del traje.
     *
     * @param id identificador del cliente.
     */
    public void setIdCliente(int id) {
        this.idCliente = id;
    }

    /** @return el nombre / descripción del traje. */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del traje.
     *
     * @param nombre nombre no vacío.
     * @throws IllegalArgumentException si <code>nombre</code> es nulo o vacío.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del traje no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    /** @return el estado actual del traje. */
    public Estado getEstado() {
        return estado;
    }

    /**
     * Establece directamente el estado del traje.
     *
     * @param estado nuevo estado.
     */
    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    // ---------------------------------------------------------- equals / hashCode / toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Traje)) return false;
        Traje that = (Traje) o;
        return identificador == that.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    @Override
    public String toString() {
        return nombre + (estado != null ? " (" + estado.getTexto() + ")" : "");
    }
}
