# Taller de Edna Moda

Aplicación de escritorio para gestionar las citas de un taller de costura ficticio. Es el Proyecto Integrador de 1.º de DAW (curso 2025/26), desarrollado en equipo por **Hijos del Compilador** y dividido en sprints.

Une tres módulos del ciclo: Programación, Bases de Datos y Entornos de Desarrollo.

## Qué incluye

- Inicio de sesión de empleados con contraseñas guardadas como SHA-256.
- Gestión de citas, clientes y talleres.
- Interfaz gráfica con Java Swing.
- Base de datos MySQL normalizada, con script de creación y datos de ejemplo.
- Diagramas E-R, de clases y de casos de uso, wireframes y memorias de cada sprint.

## Arquitectura

La aplicación sigue el patrón **Modelo-Vista-Controlador**, con clases **DAO** para el acceso a datos mediante JDBC.

| Paquete | Contenido |
|---|---|
| `src/TercerSprint/modelo` | Clases del dominio: `Cita`, `Cliente`, `Empleado`, `Taller`, `Traje` |
| `src/TercerSprint/control` | Controladores, DAO y conexión a la base de datos |
| `src/SegundoSprint/vista` | Ventanas Swing |
| `src/SegundoSprint/bd` | Script `edna_moda.sql` |

![Diagrama de clases](diagrama_final_sprint3.png)

## Puesta en marcha

Necesitas un JDK y MySQL.

1. Carga el script `src/SegundoSprint/bd/edna_moda.sql` en MySQL.
2. Ajusta el usuario y la contraseña de tu MySQL local en `src/TercerSprint/control/ConexionBD.java`.
3. Abre el proyecto en Eclipse; el conector de MySQL está en `lib/`.
4. Ejecuta `TercerSprint.MainApp`.

El [manual de usuario](docs/Manual-de-usuario.md) explica su uso. Los datos y las credenciales del script son de ejemplo, solo para uso local.

## Documentación

- [Memoria final](Memoria_Proyecto_Integrador_final.pdf)
- [Normalización de la base de datos](NORMALIZACIÓN.pdf)
- [Wireframes](WIREFRAMES.pdf)
