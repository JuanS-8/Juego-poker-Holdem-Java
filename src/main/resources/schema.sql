-- Esquema de la base de datos H2 del juego Póker Texas Hold'em

CREATE TABLE IF NOT EXISTS jugador (
    id              VARCHAR(36)  PRIMARY KEY,
    nombre          VARCHAR(50)  NOT NULL UNIQUE,
    es_bot          BOOLEAN      NOT NULL,
    fichas          INT          NOT NULL DEFAULT 0,
    manos_jugadas   INT          NOT NULL DEFAULT 0,
    manos_ganadas   INT          NOT NULL DEFAULT 0,
    maximo_fichas   INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS partida (
    id              VARCHAR(36)  PRIMARY KEY,
    posicion_boton  INT          NOT NULL,
    ciega_pequena   INT          NOT NULL,
    ciega_grande    INT          NOT NULL,
    numero_mano     INT          NOT NULL,
    fecha_guardado  TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS partida_jugador (
    id_partida      VARCHAR(36)  NOT NULL REFERENCES partida(id) ON DELETE CASCADE,
    id_jugador      VARCHAR(36)  NOT NULL REFERENCES jugador(id),
    asiento         INT          NOT NULL,
    fichas          INT          NOT NULL,
    PRIMARY KEY (id_partida, id_jugador)
);

CREATE TABLE IF NOT EXISTS historial_mano (
    id              VARCHAR(36)  PRIMARY KEY,
    id_partida      VARCHAR(36)  NOT NULL,
    numero_mano     INT          NOT NULL,
    comunitarias    VARCHAR(100),
    bote            INT          NOT NULL,
    fecha           TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS accion_mano (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_historial    VARCHAR(36)  NOT NULL REFERENCES historial_mano(id) ON DELETE CASCADE,
    orden           INT          NOT NULL,
    id_jugador      VARCHAR(36)  NOT NULL,
    fase            VARCHAR(10)  NOT NULL,
    tipo            VARCHAR(10)  NOT NULL,
    monto           INT          NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS ganador_mano (
    id_historial    VARCHAR(36)  NOT NULL REFERENCES historial_mano(id) ON DELETE CASCADE,
    nombre_jugador  VARCHAR(50)  NOT NULL,
    monto           INT          NOT NULL,
    PRIMARY KEY (id_historial, nombre_jugador)
);
