package TercerSprint.modelo;

import java.util.Objects;

/**
 * Representa a un empleado del taller de Edna Moda.
 * <p>
 * Cada empleado pertenece a una de las tres categorías permitidas
 * ({@link Categoria#APRENDIZ}, {@link Categoria#OFICIAL} o
 * {@link Categoria#MAESTRO}) y tiene un apodo único que actúa como
 * identificador de inicio de sesión.
 * </p>
 *
 * <p>La contraseña nunca se expone en claro: la clase guarda únicamente
 * el hash SHA-256 que se compara con el almacenado en la base de datos.</p>
 *
 * <p>Sprint #3 — [PR4] Clase del Modelo generada a partir del diagrama de clases.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #3
 */
public class Empleado {

    /**
     * Categorías permitidas para un empleado del taller.
     * El texto que devuelve {@link #getTexto()} coincide con el almacenado
     * en la columna <code>categoria</code> de la BBDD.
     */
    public enum Categoria {
        /** Categoría de menor rango. Sin permisos de creación ni modificación. */
        APRENDIZ("Aprendiz"),
        /** Categoría intermedia. Puede crear citas y modificar las propias. */
        OFICIAL("Oficial"),
        /** Categoría máxima. Acceso completo a todas las operaciones. */
        MAESTRO("Maestro");

        private final String texto;

        Categoria(String texto) {
            this.texto = texto;
        }

        /** @return la representación textual de la categoría. */
        public String getTexto() {
            return texto;
        }

        /**
         * Convierte la cadena almacenada en BBDD al valor del enum.
         *
         * @param texto cadena ("Aprendiz", "Oficial", "Maestro").
         * @return el valor del enum.
         * @throws IllegalArgumentException si <code>texto</code> no es una categoría válida.
         */
        public static Categoria fromTexto(String texto) {
            if (texto == null) return null;
            for (Categoria c : values()) {
                if (c.texto.equalsIgnoreCase(texto)) return c;
            }
            throw new IllegalArgumentException("Categoría desconocida: " + texto);
        }
    }

    private int identificador;
    private String nombreApellidos;
    private String apodo;
    private Categoria categoria;
    private String passwordHash;

    /** Constructor vacío necesario para los DAOs. */
    public Empleado() {
    }

    /**
     * Crea un empleado sin contraseña (para uso en listados).
     *
     * @param identificador   identificador único.
     * @param nombreApellidos nombre y apellidos completos.
     * @param apodo           apodo / usuario de login.
     * @param categoria       categoría profesional.
     */
    public Empleado(int identificador, String nombreApellidos, String apodo,
                    Categoria categoria) {
        this.identificador = identificador;
        this.nombreApellidos = nombreApellidos;
        this.apodo = apodo;
        this.categoria = categoria;
    }

    /**
     * Crea un empleado con todos sus atributos, incluido el hash de contraseña.
     *
     * @param identificador   identificador único.
     * @param nombreApellidos nombre y apellidos completos.
     * @param apodo           apodo / usuario de login.
     * @param categoria       categoría profesional.
     * @param passwordHash    hash SHA-256 de la contraseña.
     */
    public Empleado(int identificador, String nombreApellidos, String apodo,
                    Categoria categoria, String passwordHash) {
        this(identificador, nombreApellidos, apodo, categoria);
        this.passwordHash = passwordHash;
    }

    // ----------------------------------------------------------------- métodos de negocio

    /**
     * Comprueba si el empleado tiene permisos para crear citas.
     * Los aprendices NO pueden crear citas nuevas.
     *
     * @return <code>true</code> si es Oficial o Maestro.
     */
    public boolean puedeCrearCitas() {
        return categoria == Categoria.OFICIAL || categoria == Categoria.MAESTRO;
    }

    /**
     * Comprueba si el empleado es Maestro (acceso total).
     *
     * @return <code>true</code> si la categoría es {@link Categoria#MAESTRO}.
     */
    public boolean esMaestro() {
        return categoria == Categoria.MAESTRO;
    }

    // ---------------------------------------------------------- getters/setters

    /** @return el identificador del empleado. */
    public int getIdentificador() {
        return identificador;
    }

    /**
     * Establece el identificador del empleado.
     *
     * @param id identificador.
     */
    public void setIdentificador(int id) {
        this.identificador = id;
    }

    /** @return el nombre y apellidos completos. */
    public String getNombreApellidos() {
        return nombreApellidos;
    }

    /**
     * Establece el nombre y apellidos.
     *
     * @param n nombre y apellidos.
     */
    public void setNombreApellidos(String n) {
        this.nombreApellidos = n;
    }

    /** @return el apodo / nombre de usuario. */
    public String getApodo() {
        return apodo;
    }

    /**
     * Establece el apodo del empleado.
     *
     * @param apodo apodo único.
     */
    public void setApodo(String apodo) {
        this.apodo = apodo;
    }

    /** @return la categoría profesional. */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría profesional.
     *
     * @param c categoría.
     */
    public void setCategoria(Categoria c) {
        this.categoria = c;
    }

    /** @return el hash SHA-256 de la contraseña. */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Establece el hash SHA-256 de la contraseña.
     *
     * @param p hash de la contraseña.
     */
    public void setPasswordHash(String p) {
        this.passwordHash = p;
    }

    // ---------------------------------------------------------- equals / hashCode / toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empleado)) return false;
        Empleado that = (Empleado) o;
        return identificador == that.identificador;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identificador);
    }

    @Override
    public String toString() {
        return apodo + (categoria != null ? " (" + categoria.getTexto() + ")" : "")
                + (nombreApellidos != null ? " - " + nombreApellidos : "");
    }
}
