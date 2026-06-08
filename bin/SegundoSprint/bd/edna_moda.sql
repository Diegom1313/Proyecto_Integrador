-- =============================================================
-- BASE DE DATOS: TALLER EDNA MODA
-- Sprint #2 - [BD3] Modelo Relacional + [BD4] Creación BBDD
-- =============================================================

CREATE DATABASE IF NOT EXISTS edna_moda CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE edna_moda;

-- -------------------------------------------------------------
-- TABLA: empleados
-- Almacena a los empleados del taller (Aprendiz, Oficial, Maestro)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS empleados (
    id_empleado   INT          PRIMARY KEY AUTO_INCREMENT,
    nombre        VARCHAR(50)  NOT NULL,
    apellidos     VARCHAR(100) NOT NULL,
    apodo         VARCHAR(50)  NOT NULL UNIQUE,   -- nombre en clave / usuario de login
    categoria     ENUM('Aprendiz','Oficial','Maestro') NOT NULL,
    password      VARCHAR(255) NOT NULL            -- almacenado con hash (ej. SHA-256)
);

CREATE INDEX idx_empleados_apodo ON empleados(apodo);

-- -------------------------------------------------------------
-- TABLA: clientes
-- Superhéroes y supervillanos clientes del taller
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente       INT          PRIMARY KEY AUTO_INCREMENT,
    nombre_superheroe VARCHAR(100) NOT NULL,
    superpoder       VARCHAR(200),
    colores          VARCHAR(200),
    tipo             ENUM('Heroe','Villano') DEFAULT NULL  -- BONUS: distinguir héroe/villano
);

CREATE INDEX idx_clientes_nombre ON clientes(nombre_superheroe);

-- -------------------------------------------------------------
-- TABLA: trajes
-- Cada cliente puede tener uno o varios trajes en distintos estados
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trajes (
    id_traje   INT          PRIMARY KEY AUTO_INCREMENT,
    id_cliente INT          NOT NULL,
    nombre     VARCHAR(100) NOT NULL,   -- ej. "Traje principal", "Traje submarino"
    estado     ENUM('Diseño','Costura','Taller') NOT NULL DEFAULT 'Diseño',
    CONSTRAINT fk_traje_cliente FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente) ON DELETE CASCADE
);

CREATE INDEX idx_trajes_cliente ON trajes(id_cliente);

-- -------------------------------------------------------------
-- TABLA: talleres
-- Salas del estudio, identificadas por ciudad y tipo
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS talleres (
    id_taller   INT          PRIMARY KEY AUTO_INCREMENT,
    nombre_sala VARCHAR(100) NOT NULL,                         -- ej. París, Milán, Madrid
    tipo_sala   ENUM('Diseño','Costura','Pruebas') NOT NULL
);

-- -------------------------------------------------------------
-- TABLA: citas
-- Reservas de sala para atender a un cliente
-- Restricciones de negocio:
--   · duracion en horas completas (mínimo 1)
--   · id_empleado_responsable debe ser Oficial o Maestro (validar en app)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS citas (
    id_cita                  INT  PRIMARY KEY AUTO_INCREMENT,
    id_cliente               INT  NOT NULL,
    id_traje                 INT  NOT NULL,
    id_empleado_responsable  INT  NOT NULL,   -- Oficial o Maestro
    id_taller                INT  NOT NULL,
    dia                      DATE NOT NULL,
    hora                     TIME NOT NULL,
    duracion                 INT  NOT NULL DEFAULT 1 CHECK (duracion >= 1),
    -- Al borrar un cliente / traje / taller, se eliminan sus citas en cascada.
    -- Para el empleado responsable se usa RESTRICT (no se permite eliminar empleados con citas activas).
    CONSTRAINT fk_cita_cliente   FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)  ON DELETE CASCADE  ON UPDATE CASCADE,
    CONSTRAINT fk_cita_traje     FOREIGN KEY (id_traje)
        REFERENCES trajes(id_traje)      ON DELETE CASCADE  ON UPDATE CASCADE,
    CONSTRAINT fk_cita_empleado  FOREIGN KEY (id_empleado_responsable)
        REFERENCES empleados(id_empleado) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_cita_taller    FOREIGN KEY (id_taller)
        REFERENCES talleres(id_taller)   ON DELETE CASCADE  ON UPDATE CASCADE
);

CREATE INDEX idx_citas_dia        ON citas(dia);
CREATE INDEX idx_citas_empleado   ON citas(id_empleado_responsable);
CREATE INDEX idx_citas_cliente    ON citas(id_cliente);

-- -------------------------------------------------------------
-- TABLA: cita_aprendices
-- Relación N:M entre citas y aprendices asignados (máximo 2)
-- La restricción de máximo 2 se valida en la capa de negocio (Controlador)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cita_aprendices (
    id_cita     INT NOT NULL,
    id_aprendiz INT NOT NULL,
    PRIMARY KEY (id_cita, id_aprendiz),
    CONSTRAINT fk_ca_cita      FOREIGN KEY (id_cita)
        REFERENCES citas(id_cita) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ca_aprendiz  FOREIGN KEY (id_aprendiz)
        REFERENCES empleados(id_empleado) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- =============================================================
-- [BD5] INSERCIÓN DE DATOS DE PRUEBA
-- =============================================================

-- Empleados (password = SHA2('1234',256) para pruebas)
INSERT INTO empleados (nombre, apellidos, apodo, categoria, password) VALUES
('Edna',     'Moda',          'EdnaM',    'Maestro',   SHA2('1234',256)),
('Tony',     'Stark',         'IronTailor','Oficial',   SHA2('1234',256)),
('Peter',    'Parker',        'SpiderHilo','Oficial',   SHA2('1234',256)),
('Wanda',    'Maximoff',      'ScarletNeedle','Oficial',SHA2('1234',256)),
('Luis',     'García',        'AgujaRápida','Aprendiz', SHA2('1234',256)),
('Sara',     'López',         'HiloPerdido','Aprendiz', SHA2('1234',256)),
('Ana',      'Martínez',      'DedalDeOro','Aprendiz',  SHA2('1234',256));

-- Talleres
INSERT INTO talleres (nombre_sala, tipo_sala) VALUES
('París',      'Diseño'),
('Milán',      'Diseño'),
('Madrid',     'Costura'),
('Nueva York', 'Costura'),
('Berlín',     'Pruebas'),
('Tokio',      'Pruebas');

-- Clientes
INSERT INTO clientes (nombre_superheroe, superpoder, colores, tipo) VALUES
('Iron Man',       'Armadura tecnológica', 'Rojo y dorado',   'Heroe'),
('Spider-Man',     'Sentido arácnido',     'Rojo y azul',     'Heroe'),
('Viuda Negra',    'Agente de élite',      'Negro',           'Heroe'),
('Thanos',         'Control de gemas',     'Púrpura y dorado','Villano'),
('Loki',           'Ilusiones mágicas',    'Verde y dorado',  'Villano'),
('Electro',        'Control eléctrico',    'Azul y amarillo', 'Villano');

-- Trajes
INSERT INTO trajes (id_cliente, nombre, estado) VALUES
(1, 'Traje Mark L principal',    'Taller'),
(1, 'Traje espacial Mark L',     'Diseño'),
(2, 'Traje principal Spider-Man','Costura'),
(2, 'Traje simbionte',           'Diseño'),
(3, 'Traje principal Viuda',     'Taller'),
(4, 'Armadura del infinito',     'Diseño'),
(5, 'Capa asgardiana',           'Costura'),
(6, 'Traje de combate Electro',  'Diseño');

-- Citas de ejemplo
INSERT INTO citas (id_cliente, id_traje, id_empleado_responsable, id_taller, dia, hora, duracion) VALUES
(1, 1, 2, 3, '2026-03-18', '09:00:00', 2),
(2, 3, 3, 1, '2026-03-19', '10:00:00', 1),
(3, 5, 2, 5, '2026-03-20', '11:00:00', 3),
(4, 6, 1, 2, '2026-03-21', '09:00:00', 2),
(5, 7, 4, 4, '2026-03-22', '12:00:00', 1);

-- Aprendices asignados a citas
INSERT INTO cita_aprendices (id_cita, id_aprendiz) VALUES
(1, 5),
(1, 6),
(2, 5),
(4, 6);
