package TercerSprint.modelo;

import java.util.Objects;

/**
 * Representa a un cliente del taller de Edna Moda.
 * <p>
 * En el universo del proyecto, los clientes son superhéroes o supervillanos
 * que acuden al taller a encargar o reparar sus trajes. La clase se mapea
 * directamente con la tabla <code>clientes</code> de la base de datos.
 * </p>
 *
 * <h2>Atributos principales</h2>
 * <ul>
 *   <li><b>numeroCliente</b>: identificador único (PK).</li>
 *   <li><b>nombre</b>: nombre de superhéroe / supervillano.</li>
 *   <li><b>superpoder</b>: descripción del poder principal.</li>
 *   <li><b>colores</b>: paleta de colores característica del traje.</li>
 *   <li><b>tipo</b>: HEROE / VILLANO (BONUS — separación obligatoria entre tipos opuestos).</li>
 * </ul>
 *
 * <p>Sprint #3 — [PR4] Clase del Modelo generada a partir del diagrama de clases.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class Cliente {

    /**
     * Tipo del cliente. Se utiliza en la regla BONUS para impedir que dos
     * clientes de tipos opuestos compartan taller con menos de una hora
     * de margen entre sus citas.
     */
    public enum Tipo {
        /** Cliente con valores positivos (héroe). */
        HEROE("Heroe"),
        /** Cliente con intenciones malvadas (villano). */
        VILLANO("Villano");

        private final String texto;

        Tipo(String t) {
            this.texto = t;
        }

        /**
         * Devuelve la representación textual del tipo tal y como se guarda en BBDD.
         *
         * @return cadena "Heroe" o "Villano".
         */
        public String getTexto() {
            return texto;
        }

        /**
         * Convierte la cadena guardada en BBDD al valor correspondiente del enum.
         *
         * @param texto cadena a convertir (puede ser <code>null</code>).
         * @return el valor del enum o <code>null</code> si <code>texto</code> es nulo o no coincide.
         */
        public static Tipo fromTexto(String texto) {
            if (texto == null) return null;
            for (Tipo t : values()) {
                if (t.texto.equalsIgnoreCase(texto)) return t;
            }
            return null;
        }
    }

    private int numeroCliente;
    private String nombre;
    private String superpoder;
    private String colores;
    private Tipo tipo;

    /** Constructor por defecto. Necesario para los DAOs. */
    public Cliente() {
    }

    /**
     * Crea un nuevo cliente con todos sus atributos.
     *
     * @param numeroCliente identificador único en BBDD (0 si todavía no se ha persistido).
     * @param nombre        nombre de superhéroe / supervillano (no puede ser nulo ni vacío).
     * @param superpoder    descripción del poder principal.
     * @param colores       paleta cromática del traje.
     * @param tipo          {@link Tipo#HEROE} o {@link Tipo#VILLANO}; admite <code>null</code>.
     * @throws IllegalArgumentException si el nombre es nulo o vacío.
     */
    public Cliente(int numeroCliente, String nombre, String superpoder,
                   String colores, Tipo tipo) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        this.numeroCliente = numeroCliente;
        this.nombre = nombre.trim();
        this.superpoder = superpoder;
        this.colores = colores;
        this.tipo = tipo;
    }

    // ------------------------------------------------------------------ getters/setters

    /** @return el identificador único del cliente. */
    public int getNumeroCliente() {
        return numeroCliente;
    }

    /**
     * Establece el identificador único del cliente.
     *
     * @param n nuevo identificador.
     */
    public void setNumeroCliente(int n) {
        this.numeroCliente = n;
    }

    /** @return el nombre de superhéroe / supervillano. */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del cliente.
     *
     * @param nombre nombre no vacío.
     * @throws IllegalArgumentException si <code>nombre</code> es nulo o vacío.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    /** @return el superpoder del cliente. */
    public String getSuperpoder() {
        return superpoder;
    }

    /**
     * Establece el superpoder del cliente.
     *
     * @param s superpoder.
     */
    public void setSuperpoder(String s) {
        this.superpoder = s;
    }

    /** @return los colores característicos del traje. */
    public String getColores() {
        return colores;
    }

    /**
     * Establece la paleta cromática del traje.
     *
     * @param c colores.
     */
    public void setColores(String c) {
        this.colores = c;
    }

    /** @return el tipo del cliente o <code>null</code> si no consta. */
    public Tipo getTipo() {
        return tipo;
    }

    /**
     * Establece el tipo del cliente.
     *
     * @param tipo {@link Tipo#HEROE}, {@link Tipo#VILLANO} o <code>null</code>.
     */
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    // ------------------------------------------------------------------ equals / hashCode / toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente that = (Cliente) o;
        return numeroCliente == that.numeroCliente;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroCliente);
    }

    @Override
    public String toString() {
        return nombre + (tipo != null ? " [" + tipo.getTexto() + "]" : "");
    }
}
