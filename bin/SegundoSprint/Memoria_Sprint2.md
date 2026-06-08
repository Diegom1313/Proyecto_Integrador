# Memoria del Proyecto Integrador — Sprint #2
## Taller de Edna Moda | 1º DAW | Curso 2025-26

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| Java (JDK) | 17+ | Lenguaje principal de desarrollo |
| Java Swing | (incluido en JDK) | Construcción de la interfaz gráfica (Vista) |
| MySQL | 8.0+ | Sistema de gestión de base de datos relacional |
| Eclipse IDE | 2024+ | Entorno de desarrollo integrado |
| Git / GitHub | — | Control de versiones y trabajo colaborativo |
| PlantUML | — | Generación del diagrama de casos de uso |

---

## Metodología

El proyecto sigue la metodología ágil **SCRUM**:

- **Sprints de 2 semanas** con objetivos definidos al inicio de cada uno.
- **Sprint Review** al final de cada sprint para evaluar el trabajo realizado.
- **Retrospectiva**: identificación de mejoras en comunicación y desarrollo.
- Repositorio en **GitHub** para control de versiones y visibilidad del progreso.
- Planificación de tareas con **Trello**.

### Patrón de diseño: MVC

La aplicación sigue el patrón **Modelo-Vista-Controlador**:

- **Modelo**: clases que representan las entidades del dominio (Empleado, Cliente, Traje, Taller, Cita).
- **Vista**: clases Swing que forman la interfaz gráfica (LoginVista, VentanaPrincipal, CitasVista, TalleresVista, ClientesVista).
- **Controlador**: clases encargadas de la lógica de negocio y el acceso a la base de datos.

---

## Anexo 1 — Listado de requisitos de la aplicación

### Requisitos funcionales

| ID | Requisito |
|---|---|
| RF-01 | El sistema dispondrá de una pantalla de login con usuario (apodo) y contraseña. |
| RF-02 | Un empleado de categoría **Aprendiz** solo puede ver las citas en las que participa. No puede crear ni modificar citas. |
| RF-03 | Un empleado de categoría **Oficial** puede ver todas las citas, crear nuevas citas (diseño, costura, pruebas) y modificar únicamente las citas que él ha creado. |
| RF-04 | Un **Oficial** puede asignar hasta 2 aprendices a cada cita. |
| RF-05 | Un empleado de categoría **Maestro** puede ver y modificar todas las citas, crearlas y asignarlas a cualquier oficial. |
| RF-06 | El **Maestro** puede añadir, modificar y borrar clientes. |
| RF-07 | El **Maestro** puede añadir, modificar y borrar talleres. |
| RF-08 | Cada cita tiene: cliente, traje, empleado responsable (Oficial/Maestro), taller reservado, día, hora y duración (horas completas, por defecto 1h). |
| RF-09 | Cada cliente tiene: ID único, nombre de superhéroe/villano, superpoder y colores. |
| RF-10 | Cada traje tiene: ID, cliente al que pertenece, nombre y estado (Diseño, Costura, Taller). |
| RF-11 | Cada taller tiene: ID único, nombre de sala (ciudad de moda) y tipo (Diseño, Costura, Pruebas). |
| RF-12 | Cada empleado tiene: ID único, nombre, apellidos, apodo (login), categoría y contraseña. |
| RF-13 | **(BONUS)** Los clientes pueden clasificarse como Héroe o Villano. Un héroe y un villano no pueden tener citas en el mismo taller con menos de 1 hora de margen entre ellas. |

### Requisitos no funcionales

| ID | Requisito |
|---|---|
| RNF-01 | La interfaz se desarrolla con Java Swing siguiendo el patrón MVC. |
| RNF-02 | Los datos se persisten en una base de datos relacional MySQL. |
| RNF-03 | Las contraseñas se almacenan usando hash SHA-256. |
| RNF-04 | El proyecto se versiona en GitHub con commits regulares. |
| RNF-05 | El código se documenta con JavaDoc. |
| RNF-06 | El proyecto no debe tener errores de compilación. |
