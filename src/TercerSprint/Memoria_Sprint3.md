# Memoria — Sprint #3 (PR4)

**Proyecto Integrador 1º DAW — Curso 2025/26**
**Aplicación de Gestión de Citas del Taller de Edna Moda**
Sprint #3: 7 de abril – 20 de abril de 2026.

---

## 1. Introducción

Este documento recoge el trabajo realizado durante el tercer sprint del Proyecto Integrador. Tras haber definido en los sprints previos el análisis funcional del sistema (Sprint 1), la capa de Vista con las ventanas Swing, el modelo relacional y la creación física de la base de datos (Sprint 2), en este Sprint 3 se afronta la construcción de la capa de **Modelo** y la capa de **Control** que dan vida a la aplicación. Se trata por tanto del sprint en el que el proyecto deja de ser un conjunto de prototipos inconexos y empieza a funcionar como una aplicación integrada siguiendo el patrón **MVC**.

El objetivo general del sprint es doble. En primer lugar, traducir el diagrama de clases UML (tarea ED8) a código Java, creando las clases del modelo con sus atributos, operaciones y asociaciones. En segundo lugar, crear las clases del control que actúan como puente entre la vista y el modelo, encapsulando tanto el acceso a la base de datos MySQL mediante JDBC como las reglas de negocio descritas en el enunciado (permisos según la categoría del empleado, límite de aprendices por cita, regla de separación entre clientes héroes y villanos, etc.).

La tarea concreta dentro de este sprint es la **[PR4]**, que establece literalmente:

> *A partir del diagrama de clases:*
>   - *Creación de las clases pertenecientes al Modelo.*
>   - *Creación de las clases pertenecientes al Control encargadas del acceso a BBDD y del manejo de la aplicación.*

Paralelamente, la tarea **[ED9]** requiere redactar los apartados *Introducción* y *Desarrollo* de la memoria final del proyecto, que son los que se recogen a continuación.

Los módulos implicados en este sprint son los tres del ciclo (Programación, Bases de Datos y Entornos de Desarrollo), ya que se combina diseño orientado a objetos, consultas SQL y documentación/UML.

---

## 2. Desarrollo

### 2.1 Planificación del sprint

Siguiendo la metodología SCRUM adoptada por el equipo, el sprint se ha planificado a partir del tablero Trello dividiendo el trabajo en cinco *user stories* técnicas:

1. Traducir el diagrama de clases al Modelo Java.
2. Implementar la conexión JDBC única a la BBDD `edna_moda`.
3. Implementar los DAO (Data Access Object) con el CRUD completo.
4. Implementar los Controladores con la lógica de negocio y los permisos por rol.
5. Escribir una clase `MainApp` que integre la Vista del Sprint 2 con el Control del Sprint 3.

Cada historia se ha trabajado en una rama `feature/...` de GitHub y se ha integrado en `develop` al completarse, de manera que siempre queda una línea de código estable al final del sprint (práctica requerida por el enunciado).

### 2.2 Estructura del código

La nueva carpeta `src/TercerSprint` sigue el patrón **MVC** y queda organizada así:

```
src/
├── PrimerSprint/            (Sprint 1)
├── SegundoSprint/
│   ├── vista/               (Sprint 2 - Vistas Swing)
│   └── bd/edna_moda.sql     (Sprint 2 - Script SQL)
└── TercerSprint/
    ├── modelo/              (PR4 - Clases del Modelo)
    │   ├── Empleado.java
    │   ├── Cliente.java
    │   ├── Traje.java
    │   ├── Taller.java
    │   └── Cita.java
    ├── control/             (PR4 - Clases del Control)
    │   ├── ConexionBD.java
    │   ├── SesionActual.java
    │   ├── EmpleadoDAO.java
    │   ├── ClienteDAO.java
    │   ├── TrajeDAO.java
    │   ├── TallerDAO.java
    │   ├── CitaDAO.java
    │   ├── LoginControlador.java
    │   ├── CitasControlador.java
    │   ├── ClientesControlador.java
    │   └── TalleresControlador.java
    └── MainApp.java         (Punto de entrada integrador)
```

### 2.3 Capa Modelo

Se ha creado una clase por cada entidad del diagrama de clases, y los atributos se han tipado usando las clases estándar de Java cuando procedía (`LocalDate`, `LocalTime` para la cita, enumeraciones para los estados y tipos cerrados). Los valores restringidos del diagrama —categoría del empleado, estado del traje, tipo de la sala y tipo del cliente— se han implementado como `enum` anidados, con métodos `fromTexto()` que permiten convertir de forma segura las cadenas provenientes de MySQL a los valores del enum y viceversa.

Las operaciones que aparecen en el diagrama (`asignarEmpleado()`, `cambiarHorario()`, `cambiarEstado()`, `modificarTaller()`) se han incluido en las clases correspondientes. Además se han añadido pequeños métodos de ayuda que facilitan el código del control, como `Empleado.puedeCrearCitas()`, `Empleado.esMaestro()` y `Cita.getHoraFin()`.

En la clase `Cita` se guarda la lista de aprendices como una `List<Integer>`, en coherencia con la tabla N:M `cita_aprendices` de la BBDD. La propia clase conoce la regla “máximo 2 aprendices” (constante `MAX_APRENDICES = 2`) y la valida en el método `anadirAprendiz()`.

### 2.4 Capa Control — Acceso a BBDD

El acceso a la base de datos se ha centralizado en la clase `ConexionBD`, que actúa como fábrica de conexiones JDBC al servidor MySQL. La URL y las credenciales están parametrizadas y pueden sobreescribirse con `ConexionBD.configurar(...)` para los equipos que tengan configuraciones distintas en local. Todas las conexiones se abren y cierran dentro de bloques `try-with-resources`, garantizando que no quedan recursos colgados aunque se produzcan excepciones SQL.

Sobre esta conexión se han construido los DAO (`EmpleadoDAO`, `ClienteDAO`, `TrajeDAO`, `TallerDAO`, `CitaDAO`). Son los únicos objetos que contienen sentencias SQL, y todas ellas usan `PreparedStatement` para evitar inyección SQL y mejorar el rendimiento. Cada DAO incluye un método privado `map(ResultSet)` que convierte una fila SQL en su objeto del modelo, lo que hace el resto del CRUD más legible.

Hay tres puntos del acceso a datos que merecen mención especial:

- **Autenticación.** `EmpleadoDAO.autenticar(apodo, pass)` calcula el SHA-256 de la contraseña introducida (igual que hace `SHA2('...',256)` en el script SQL) y busca coincidencia exacta en la tabla `empleados`. Devuelve el `Empleado` completo, o `null` si falla.
- **Transacciones.** La creación y modificación de una cita afectan a dos tablas (`citas` y `cita_aprendices`), por lo que `CitaDAO.crear()` y `CitaDAO.modificar()` desactivan el autocommit y envuelven las dos operaciones en una transacción: o se aplican ambas, o no se aplica ninguna.
- **Consulta para reglas de negocio.** Se ha añadido `CitaDAO.listarPorTallerYDia(idTaller, dia)` porque el controlador necesita comprobar solapes de horario y la regla de héroe/villano antes de permitir crear o modificar una cita.

### 2.5 Capa Control — Lógica de la aplicación

Por encima de los DAO están los controladores, que son los que usan las vistas. Esta separación (DAO = datos / Controlador = negocio) facilita el testing y cumple el objetivo del módulo de Programación de implementar correctamente el patrón MVC.

- `SesionActual` mantiene el empleado autenticado como singleton, de modo que cualquier controlador puede consultar el rol activo.
- `LoginControlador` sustituye al “TODO” que quedaba en la `LoginVista` del Sprint 2. Conecta el botón **Entrar** con `EmpleadoDAO.autenticar(...)`, inicia la `SesionActual`, cierra la ventana de login y abre la `VentanaPrincipal` pasándole el apodo y la categoría (lo que activa los permisos de menú ya preparados en la vista).
- `CitasControlador` concentra las reglas más complejas del enunciado:
    - Solo Oficiales y Maestros pueden crear citas.
    - Un Oficial únicamente puede modificar las citas de las que es responsable (lo comprueba contra la BBDD antes de disparar el `UPDATE`).
    - Se valida la duración mínima (1 h) y el número máximo de aprendices (2).
    - Se comprueba el **solape horario** en el mismo taller y día.
    - Se aplica el **BONUS** descrito en el enunciado: si el cliente de la nueva cita es héroe (o villano) y ya hay una cita en ese taller y día para un cliente del tipo contrario, debe haber al menos 60 minutos de margen entre ambas.
- `ClientesControlador` y `TalleresControlador` implementan los permisos descritos en el documento: el Aprendiz no accede, el Oficial solo consulta talleres, y el Maestro tiene acceso total. Estas reglas se centralizan en métodos privados `exigirMaestro()`, que lanzan `IllegalStateException` con un mensaje claro cuando no se cumplen — las vistas lo convierten en un `JOptionPane` de error.

### 2.6 Integración con la Vista del Sprint 2

Para que lo desarrollado sea ejecutable de extremo a extremo, la nueva clase `MainApp`:

1. Aplica el *Look &amp; Feel* del sistema operativo (para que la aplicación se vea nativa).
2. Llama a `ConexionBD.probarConexion()` y avisa por consola si MySQL no está arrancado.
3. Crea la `LoginVista` del Sprint 2 y la envuelve con el nuevo `LoginControlador`.

A partir de ese momento, si el usuario introduce credenciales correctas (por ejemplo `EdnaM` / `1234`, ya insertadas como datos de prueba en el script del Sprint 2), se abre la `VentanaPrincipal` con los menús habilitados o deshabilitados según su rol — tal y como estaba previsto en el Sprint 2. Las ventanas específicas de Citas, Clientes y Talleres ya están preparadas para engancharse a los respectivos controladores mediante los getters que expone cada vista.

### 2.7 Dificultades encontradas y decisiones tomadas

Durante el sprint han 
 varias decisiones de diseño que merece la pena comentar:

- **Conexión por operación vs. conexión única.** Se ha optado por abrir y cerrar una conexión por operación (try-with-resources) en lugar de mantener una conexión global. Es ligeramente menos eficiente, pero mucho más robusto y evita tener que reconectar manualmente cuando MySQL cierra sesiones ociosas.
- **Contraseñas hasheadas.** Al usar SHA-256 en la aplicación y `SHA2(...,256)` en la BBDD conseguimos un esquema simple pero realista, evitando guardar contraseñas en claro. Para un entorno de producción real habría que añadir sal y un algoritmo adaptativo (bcrypt), pero queda fuera del alcance del proyecto.
- **Validación de reglas en el Controlador y no en el DAO.** Esto mantiene los DAO limpios y reutilizables, y permite testear las reglas de negocio sin necesidad de BBDD.
- **Sin cambios en la Vista.** Todas las vistas del Sprint 2 se siguen usando tal cual, siguiendo el principio del MVC: un cambio en la capa de control no debería afectar a la interfaz. Las vistas solo se tocarán en el Sprint 4 para refinar la interacción con el usuario.

### 2.8 Trabajo pendiente para el Sprint 4

Al cerrar el Sprint 3 queda:

- Conectar los listeners de las ventanas `CitasVista`, `ClientesVista` y `TalleresVista` con los controladores correspondientes ([PR5] Refinamiento del programa).
- Redactar las pruebas JUnit de los DAO y controladores ([ED10]).
- Documentar todas las clases con JavaDoc ([ED10]).
- Escribir el manual de usuario en la wiki de GitHub ([ED11]).

Con esto, al finalizar el sprint el equipo dispone de una aplicación que ya se puede arrancar, autenticar un empleado real contra la BBDD, y que deja toda la lógica de negocio lista para que la interfaz simplemente la invoque en el siguiente sprint.
