# Manual de Usuario — Taller de Edna Moda

> Aplicación de gestión del Taller de Edna Moda
> Equipo *Hijos Del Compilador* — 1º DAW
> Versión 1.0 (Sprint #3)

---

## Tabla de contenidos

1. [Introducción](#1-introducción)
2. [Requisitos del sistema](#2-requisitos-del-sistema)
3. [Instalación y arranque](#3-instalación-y-arranque)
4. [Inicio de sesión](#4-inicio-de-sesión)
5. [Ventana principal](#5-ventana-principal)
6. [Gestión de clientes](#6-gestión-de-clientes)
7. [Gestión de trajes](#7-gestión-de-trajes)
8. [Gestión de citas](#8-gestión-de-citas)
9. [Gestión de talleres](#9-gestión-de-talleres)
10. [Roles y permisos](#10-roles-y-permisos)
11. [Reglas de negocio](#11-reglas-de-negocio)
12. [Preguntas frecuentes y resolución de problemas](#12-preguntas-frecuentes-y-resolución-de-problemas)
13. [Soporte y contacto](#13-soporte-y-contacto)

---

## 1. Introducción

La aplicación **Taller de Edna Moda** permite gestionar de forma íntegra el día a día de un estudio de confección de trajes para superhéroes y supervillanos: clientes, sus trajes, los talleres (salas) donde se trabaja y las citas que se asignan a los empleados (Aprendices, Oficiales y Maestros).

El programa sigue el patrón **Modelo–Vista–Controlador (MVC)** y se conecta a una base de datos **MySQL** llamada `edna_moda`.

## 2. Requisitos del sistema

| Recurso | Versión recomendada |
| --- | --- |
| Java JDK | 11 o superior |
| MySQL Server | 8.0 o superior |
| Conector JDBC | `mysql-connector-j` 8.x |
| Sistema operativo | Windows 10/11, Linux o macOS |
| Resolución mínima | 1280 × 720 |

## 3. Instalación y arranque

### 3.1. Preparar la base de datos

1. Asegúrese de que MySQL está en ejecución.
2. Ejecute el script SQL `src/SegundoSprint/bd/edna_moda.sql` para crear la base de datos `edna_moda`, sus tablas y los datos iniciales.

```bash
mysql -u root -p < src/SegundoSprint/bd/edna_moda.sql
```

### 3.2. Configurar la conexión

Edite, si es necesario, las credenciales en la clase `TercerSprint.control.ConexionBD` o use el método `ConexionBD.configurar(url, usuario, password)` antes de arrancar la aplicación.

### 3.3. Lanzar la aplicación

Ejecute la clase `TercerSprint.MainApp`. Si la conexión es correcta verá en consola:

```
== Taller Edna Moda - Hijos Del Compilador ==
Probando conexión con la BBDD...
Conexión establecida.
```

Y aparecerá la ventana de **Login**.

## 4. Inicio de sesión

![Ventana de login — descripción] *(captura disponible en el Anexo II)*

1. Introduzca su **nombre en clave (apodo)**.
2. Introduzca su **contraseña**.
3. Pulse **Entrar** o la tecla `Intro`.

Si las credenciales son válidas se abrirá la **Ventana principal** y la sesión actual quedará registrada con su categoría (Aprendiz, Oficial o Maestro). Si son inválidas aparecerá el mensaje *"Credenciales no válidas."*.

> **Nota:** las contraseñas viajan a la BBDD ya cifradas mediante **SHA-256**, por lo que nunca se transmiten ni almacenan en claro.

## 5. Ventana principal

La ventana principal está dividida en tres áreas:

- **Cabecera:** muestra el apodo del empleado conectado y su categoría.
- **Menú lateral:** botones para cambiar entre las secciones *Clientes*, *Citas* y *Talleres*. Algunos botones aparecen deshabilitados según el rol.
- **Panel central:** carga la sección elegida (clientes / citas / talleres).

Para cerrar la sesión use la opción **Salir**.

## 6. Gestión de clientes

Sección accesible desde el botón **Clientes** del menú lateral.

La tabla muestra los siguientes datos por cada cliente: *nombre de superhéroe / villano*, *superpoder*, *colores* y *tipo* (Heroe / Villano).

### 6.1. Ver detalle

Seleccione una fila y pulse **Ver info** para abrir un panel con los detalles del cliente y la lista de sus trajes.

### 6.2. Añadir un cliente *(solo Maestro)*

1. Pulse **Nuevo cliente**.
2. Rellene el formulario con nombre, superpoder, colores y tipo.
3. Pulse **Guardar**. Se mostrará una confirmación y la lista se refrescará.

### 6.3. Modificar un cliente *(solo Maestro)*

1. Seleccione la fila a editar.
2. Pulse **Editar cliente**.
3. Modifique los campos necesarios y pulse **Guardar**.

### 6.4. Borrar un cliente *(solo Maestro)*

1. Seleccione la fila.
2. Pulse **Borrar cliente**.
3. Confirme la operación. Se eliminarán también, en cascada, sus trajes y citas asociadas.

## 7. Gestión de trajes

Los trajes se gestionan desde el detalle de un cliente. Cada traje tiene:

- **Nombre** (p. ej. *"Traje principal"*).
- **Estado**: Diseño / Costura / Taller.

### 7.1. Crear un traje *(solo Maestro)*

Desde el detalle del cliente, pulse **Nuevo traje**, rellene el nombre y elija el estado inicial.

### 7.2. Cambiar el estado de un traje *(Oficial o Maestro)*

Seleccione el traje y pulse **Cambiar estado**. Elija el nuevo valor (Diseño → Costura → Taller).

### 7.3. Borrar un traje *(solo Maestro)*

Seleccione el traje y pulse **Borrar traje**.

## 8. Gestión de citas

Sección accesible desde el botón **Citas**.

La tabla muestra: *Cliente*, *Traje*, *Taller*, *Empleado responsable*, *Día*, *Hora* y *Duración (horas)*.

### 8.1. Filtros

- **Aprendiz**: solo ve las citas en las que participa.
- **Oficial / Maestro**: ven todas, con un filtro opcional para ver únicamente las suyas.

### 8.2. Crear una cita *(Oficial o Maestro)*

1. Pulse **Nueva cita**.
2. Seleccione cliente, traje, taller, día, hora y duración.
3. Asigne, opcionalmente, hasta **2 aprendices**.
4. Pulse **Guardar**.

> Si se ocupa el taller en ese horario o se incumple la **regla de héroe / villano** la aplicación mostrará un aviso explicativo.

### 8.3. Editar una cita *(Oficial o Maestro)*

- **Oficial**: solo puede editar las citas en las que figura como responsable.
- **Maestro**: puede editar cualquier cita.

### 8.4. Borrar una cita *(solo Maestro)*

Seleccione la fila y pulse **Borrar cita**.

## 9. Gestión de talleres

Sección accesible desde el botón **Talleres**.

La tabla muestra el *nombre de sala* y el *tipo* (Diseño, Costura o Pruebas).

### 9.1. Permisos

| Operación | Aprendiz | Oficial | Maestro |
| --- | :---: | :---: | :---: |
| Ver listado | Sin acceso | OK | OK |
| Crear / Editar / Borrar | Sin acceso | Sin acceso | OK |

## 10. Roles y permisos

| Acción | Aprendiz | Oficial | Maestro |
| --- | :---: | :---: | :---: |
| Iniciar sesión | OK | OK | OK |
| Ver listado de clientes | OK | OK | OK |
| Crear / editar / borrar clientes | — | — | OK |
| Crear / borrar trajes | — | — | OK |
| Cambiar estado de un traje | — | OK | OK |
| Ver todas las citas | — | OK | OK |
| Ver mis citas | OK | OK | OK |
| Crear / editar citas | — | OK (las propias) | OK |
| Borrar citas | — | — | OK |
| Ver talleres | — | OK | OK |
| Crear / editar / borrar talleres | — | — | OK |

## 11. Reglas de negocio

1. **Duración mínima** de una cita: **1 hora**.
2. **Aprendices por cita**: máximo **2**.
3. **Sin solapes**: dos citas no pueden compartir taller, día y franja horaria.
4. **Regla héroe / villano (BONUS)**: en un mismo taller debe haber al menos **1 hora de margen** entre una cita de un héroe y una de un villano (en cualquier orden).
5. **Cambio de estado del traje**: sigue el flujo lógico Diseño → Costura → Taller (no impuesto técnicamente; queda a criterio del Oficial / Maestro).

## 12. Preguntas frecuentes y resolución de problemas

### *La aplicación dice "¡Atención! La BBDD no responde"*
Asegúrese de que MySQL está en ejecución y de que las credenciales en `ConexionBD` coinciden con las de su servidor. Compruebe también que ha cargado el script `edna_moda.sql`.

### *"Credenciales no válidas." aunque he creado un empleado en MySQL*
Las contraseñas se almacenan como SHA-256. Use `INSERT INTO empleados (..., password) VALUES (..., SHA2('miClave', 256))` o utilice la función `EmpleadoDAO.sha256(...)` desde Java.

### *No puedo crear citas*
Solo Oficiales y Maestros pueden hacerlo. Compruebe la categoría del empleado autenticado.

### *Aparece "El taller ya está ocupado en ese horario"*
Existe ya una cita que se solapa en el mismo taller y día. Cambie de horario o de taller.

### *Aparece "Héroe y villano necesitan 1 h de margen"*
Hay una cita de tipo opuesto demasiado cercana en el mismo taller. Adelante o retrase su cita al menos 1 hora respecto a la otra.

### *No me deja modificar una cita*
Si es Oficial, solo puede modificar las que tiene asignadas como responsable. Si es Aprendiz, no puede modificar citas.

## 13. Soporte y contacto

Para incidencias, sugerencias o aportaciones, abra una *issue* en el repositorio de GitHub del proyecto.

> Equipo **Hijos Del Compilador** — 1º DAW
> Curso 2025 / 2026
