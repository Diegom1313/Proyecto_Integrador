-- =============================================================
-- MIGRACIÓN: Añadir ON DELETE CASCADE a las FKs de la tabla `citas`
--           y a la tabla `cita_aprendices`
--
-- Aplica este script SOLO si ya tienes la BBDD `edna_moda` creada
-- con la versión anterior (sin CASCADE). Resuelve el error:
--   "Cannot delete or update a parent row: a foreign key
--    constraint fails ('edna_moda'.'citas', CONSTRAINT 'fk_cita_traje'...)"
-- =============================================================

USE edna_moda;

-- -------------------------------------------------------------
-- 1) Tabla `citas`
-- -------------------------------------------------------------

-- Eliminar las FKs antiguas (no tenían CASCADE)
ALTER TABLE citas DROP FOREIGN KEY fk_cita_cliente;
ALTER TABLE citas DROP FOREIGN KEY fk_cita_traje;
ALTER TABLE citas DROP FOREIGN KEY fk_cita_empleado;
ALTER TABLE citas DROP FOREIGN KEY fk_cita_taller;

-- Recrearlas con ON DELETE CASCADE / RESTRICT, según corresponda
ALTER TABLE citas
    ADD CONSTRAINT fk_cita_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_cita_traje
        FOREIGN KEY (id_traje)
        REFERENCES trajes(id_traje)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_cita_empleado
        FOREIGN KEY (id_empleado_responsable)
        REFERENCES empleados(id_empleado)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    ADD CONSTRAINT fk_cita_taller
        FOREIGN KEY (id_taller)
        REFERENCES talleres(id_taller)
        ON DELETE CASCADE ON UPDATE CASCADE;

-- -------------------------------------------------------------
-- 2) Tabla `cita_aprendices`
-- -------------------------------------------------------------
ALTER TABLE cita_aprendices DROP FOREIGN KEY fk_ca_cita;
ALTER TABLE cita_aprendices DROP FOREIGN KEY fk_ca_aprendiz;

ALTER TABLE cita_aprendices
    ADD CONSTRAINT fk_ca_cita
        FOREIGN KEY (id_cita)
        REFERENCES citas(id_cita)
        ON DELETE CASCADE ON UPDATE CASCADE,
    ADD CONSTRAINT fk_ca_aprendiz
        FOREIGN KEY (id_aprendiz)
        REFERENCES empleados(id_empleado)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- -------------------------------------------------------------
-- Comprobación
-- -------------------------------------------------------------
-- SELECT CONSTRAINT_NAME, DELETE_RULE, UPDATE_RULE
-- FROM information_schema.REFERENTIAL_CONSTRAINTS
-- WHERE CONSTRAINT_SCHEMA = 'edna_moda';
