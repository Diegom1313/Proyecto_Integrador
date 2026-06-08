package TercerSprint.control;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Traductor de excepciones técnicas a mensajes legibles para el usuario.
 * <p>
 * Centraliza la conversión de {@link SQLException}, {@link IllegalStateException}
 * y otras excepciones de bajo nivel en frases naturales en español, ocultando
 * detalles internos (constraints, drivers JDBC, stack traces) que carecen de
 * sentido para un usuario final.
 * </p>
 *
 * <p>Sprint #4 — [PR5] Refinamiento del programa: mejora de la experiencia
 * de usuario en los mensajes de error.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #4
 */
public final class MensajesError {

    private MensajesError() { /* utilidad estática */ }

    /**
     * Convierte una excepción SQL en un mensaje legible y orientado al usuario.
     *
     * @param ex excepción capturada en la capa de control o vista.
     * @return texto en castellano comprensible para el usuario final.
     */
    public static String traducir(SQLException ex) {
        if (ex == null) return "Se ha producido un error desconocido.";

        String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
        int code = ex.getErrorCode();
        String estado = ex.getSQLState() == null ? "" : ex.getSQLState();

        // --- Violación de integridad referencial (FK) ---
        // MySQL 1451 / SQLState 23000
        if (ex instanceof SQLIntegrityConstraintViolationException
                || code == 1451 || code == 1452
                || msg.contains("foreign key constraint fails")
                || "23000".equals(estado)) {
            // Intentamos afinar el mensaje en función de la tabla/constraint citada
            if (msg.contains("fk_cita_cliente") || msg.contains("`citas`") && msg.contains("clientes")) {
                return "No se puede borrar este cliente porque tiene citas asignadas.\n"
                     + "Elimina primero las citas del cliente o utiliza la opción\n"
                     + "de borrado en cascada.";
            }
            if (msg.contains("fk_cita_traje")) {
                return "No se puede eliminar este traje porque está asociado a una o\n"
                     + "varias citas. Elimina antes esas citas.";
            }
            if (msg.contains("fk_cita_empleado")) {
                return "No se puede eliminar este empleado porque es responsable\n"
                     + "de una o más citas. Reasigna primero esas citas a otro\n"
                     + "empleado y vuelve a intentarlo.";
            }
            if (msg.contains("fk_ca_aprendiz")) {
                return "No se puede eliminar este empleado porque está asignado\n"
                     + "como aprendiz en una o más citas. Quítalo primero de esas\n"
                     + "citas.";
            }
            if (msg.contains("fk_cita_taller")) {
                return "No se puede eliminar este taller porque hay citas reservadas\n"
                     + "en él. Cancela esas citas primero.";
            }
            if (msg.contains("fk_traje_cliente")) {
                return "No se puede borrar el cliente porque tiene trajes asignados.\n"
                     + "Bórralos primero o utiliza el borrado en cascada.";
            }
            if (msg.contains("fk_ca_cita") || msg.contains("fk_ca_aprendiz")) {
                return "No se puede eliminar este registro porque hay aprendices\n"
                     + "asignados a la cita.";
            }
            // Mensaje genérico para cualquier otra violación de FK
            return "No se puede completar la operación porque este registro está\n"
                 + "siendo utilizado en otra parte de la aplicación.";
        }

        // --- Valor duplicado (clave única) ---
        if (code == 1062 || msg.contains("duplicate entry")) {
            if (msg.contains("apodo") || msg.contains("idx_empleados_apodo")) {
                return "Ya existe un empleado con ese apodo.\n"
                     + "Elige un apodo distinto.";
            }
            return "Ya existe un registro con esos datos. Revisa los campos únicos.";
        }

        // --- Valor que no respeta una restricción CHECK / fuera de rango ---
        if (code == 3819 || msg.contains("check constraint")) {
            return "Alguno de los valores introducidos no cumple las reglas del\n"
                 + "sistema (por ejemplo, duración mínima de 1 hora).";
        }

        // --- Datos demasiado largos para una columna ---
        if (code == 1406 || msg.contains("data too long")) {
            return "Has introducido un texto demasiado largo en uno de los campos.";
        }

        // --- Valor NULL en columna NOT NULL ---
        if (code == 1048 || msg.contains("cannot be null")) {
            return "Faltan datos obligatorios. Revisa que todos los campos estén\n"
                 + "rellenados correctamente.";
        }

        // --- Errores de conexión ---
        if (msg.contains("communications link failure")
                || msg.contains("connection refused")
                || msg.contains("connect timed out")) {
            return "No se ha podido conectar con la base de datos.\n"
                 + "Comprueba que el servidor MySQL está arrancado.";
        }
        if (msg.contains("access denied")) {
            return "Acceso denegado a la base de datos.\n"
                 + "Revisa el usuario y la contraseña de la conexión.";
        }
        if (msg.contains("unknown database")) {
            return "La base de datos 'edna_moda' no existe.\n"
                 + "Ejecuta el script src/SegundoSprint/bd/edna_moda.sql.";
        }

        // --- Mensaje por defecto ---
        return "Se ha producido un problema al acceder a la base de datos.\n"
             + "Por favor, inténtalo de nuevo en unos instantes.";
    }

    /**
     * Convierte una excepción de regla de negocio en un mensaje legible.
     * Las {@link IllegalStateException} ya suelen llevar texto en castellano,
     * por lo que normalmente se devuelve tal cual.
     *
     * @param ex excepción capturada.
     * @return texto para mostrar al usuario.
     */
    public static String traducir(IllegalStateException ex) {
        if (ex == null || ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "No tienes permisos para realizar esta operación.";
        }
        return ex.getMessage();
    }

    /**
     * Convierte una excepción de argumento inválido en un mensaje legible.
     *
     * @param ex excepción capturada.
     * @return texto para mostrar al usuario.
     */
    public static String traducir(IllegalArgumentException ex) {
        if (ex == null || ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Alguno de los datos introducidos no es válido.";
        }
        return ex.getMessage();
    }
}
