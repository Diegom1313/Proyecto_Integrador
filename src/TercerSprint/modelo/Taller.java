package TercerSprint.modelo;

import java.util.Objects;

/**
 * Representa un taller (sala de trabajo) del estudio de Edna Moda.
 * <p>
 * Cada taller se identifica por un número único, tiene un nombre de sala
 * (típicamente una ciudad) y un tipo (Diseño, Costura o Pruebas).
 * Se mapea con la tabla <code>talleres</code> de la base de datos.
 * </p>
 *
 * <p>Sprint #3 — [PR4] Clase del Modelo generada a partir del diagrama de clases.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class Taller {

    /** Tipos de sala permitidos. */
    public enum TipoSala {
        /** Sala destinada a tareas de diseño. */
        DISENIO("Diseño"),
        /** Sala destinada a tareas de costura. */
        COSTURA("Costura"),
        /** Sala destinada a pruebas finales. */
        PRUEBAS("Pruebas");

        private final String texto;

        TipoSala(String t) {
            this.texto = t;
        }

        /** @return la representación textual del tipo. */
        public String getTexto() {
            return texto;
        }

        /**
         * Convierte la cadena guardada en BBDD al valor correspondiente del enum.
         *
         * @param texto cadena a convertir.
         * @return el valor del enum.
         * @throws IllegalArgumentException si <code>texto</code> no es válido.
         */
        public static TipoSala fromTexto(String texto) {
            if (texto == null) return null;
            for (TipoSala t : values()) {
                if (t.texto.equalsIgnoreCase(texto)) return t;
            }
            throw new IllegalArgumentException("Tipo de sala desconocido: " + texto);
        }
    }

    private int numeroTaller;
    private String nombreSala;
    private TipoSala tipoSala;

    /** Constructor vacío requerido por los DAOs. */
    public Taller() {
    }

    /**
     * Crea un nuevo taller.
     *
     * @param numeroTaller identificador único.
     * @param nombreSala   nombre de la sala (p.&nbsp;ej. "París", no vacío).
     * @param tipoSala     tipo de la sala (no nulo).
     * @throws IllegalArgumentException si el nombre es nulo/vacío o el tipo es nulo.
     */
    public Taller(int numeroTaller, String nombreSala, TipoSala tipoSala) {
        if (nombreSala == null || nombreSala.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de sala no puede estar vacío.");
        }
        if (tipoSala == null) {
            throw new IllegalArgumentException("El tipo de sala no puede ser nulo.");
        }
        this.numeroTaller = numeroTaller;
        this.nombreSala = nombreSala.trim();
        this.tipoSala = tipoSala;
    }

    /**
     * Operación del diagrama: <code>modificarTaller()</code>.
     * Actualiza los datos del taller en memoria.
     *
     * @param nuevoNombre nuevo nombre de la sala.
     * @param nuevoTipo   nuevo tipo de la sala.
     */
    public void modificarTaller(String nuevoNombre, TipoSala nuevoTipo) {
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            this.nombreSala = nuevoNombre.trim();
        }
        if (nuevoTipo != null) {
            this.tipoSala = nuevoTipo;
        }
    }

    // ---------------------------------------------------------- getters/setters

    /** @return el identificador del taller. */
    public int getNumeroTaller() {
        return numeroTaller;
    }

    /**
     * Establece el identificador del taller.
     *
     * @param n identificador.
     */
    public void setNumeroTaller(int n) {
        this.numeroTaller = n;
    }

    /** @return el nombre de la sala. */
    public String getNombreSala() {
        return nombreSala;
    }

    /**
     * Establece el nombre de la sala.
     *
     * @param n nombre.
     */
    public void setNombreSala(String n) {
        this.nombreSala = n;
    }

    /** @return el tipo de la sala. */
    public TipoSala getTipoSala() {
        return tipoSala;
    }

    /**
     * Establece el tipo de la sala.
     *
     * @param t tipo.
     */
    public void setTipoSala(TipoSala t) {
        this.tipoSala = t;
    }

    // ---------------------------------------------------------- equals / hashCode / toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Taller)) return false;
        Taller that = (Taller) o;
        return numeroTaller == that.numeroTaller;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroTaller);
    }

    @Override
    public String toString() {
        return (tipoSala != null ? tipoSala.getTexto() : "?") + " - " + nombreSala;
    }
}
