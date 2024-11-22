DROP TABLE dados_coletados CASCADE CONSTRAINTS;
DROP TABLE dispositivos CASCADE CONSTRAINTS;
DROP TABLE configuracoes_de_coleta CASCADE CONSTRAINTS;
DROP TABLE usuarios CASCADE CONSTRAINTS;
DROP TABLE roles CASCADE CONSTRAINTS;
DROP TABLE pessoas CASCADE CONSTRAINTS;

-- Drop da Tabelas de Auditoria e dos Triggers
DROP TABLE audit_roles CASCADE CONSTRAINTS;
DROP TABLE audit_pessoas CASCADE CONSTRAINTS;
DROP TABLE audit_usuarios CASCADE CONSTRAINTS;
DROP TABLE audit_configuracoes_de_coleta CASCADE CONSTRAINTS;
DROP TABLE audit_dispositivos CASCADE CONSTRAINTS;
DROP TABLE audit_dados_coletados CASCADE CONSTRAINTS;


-- Criando tabelas
CREATE TABLE roles (
    id_role NUMBER(19,0) GENERATED AS IDENTITY,
    nm_role VARCHAR2(255 CHAR),
    PRIMARY KEY (id_role),
    CONSTRAINT UK_NOME_DO_ROLE UNIQUE (nm_role)
);

CREATE TABLE pessoas (
    dt_nasc DATE,
    id_pessoa NUMBER(19,0) GENERATED AS IDENTITY,
    cpf VARCHAR2(255 CHAR),
    nm_pessoa VARCHAR2(255 CHAR),
    sobrenome VARCHAR2(255 CHAR),
    telefone VARCHAR2(255 CHAR),
    PRIMARY KEY (id_pessoa),
    CONSTRAINT UK_PESSOA_CPF UNIQUE (cpf)
);

CREATE TABLE usuarios (
    id_user NUMBER(19,0) GENERATED AS IDENTITY,
    pessoa NUMBER(19,0) NOT NULL UNIQUE,
    user_role NUMBER(19,0) NOT NULL,
    email VARCHAR2(255 CHAR),
    password VARCHAR2(255 CHAR),
    PRIMARY KEY (id_user),
    CONSTRAINT UK_USUARIO_EMAIL UNIQUE (email)
);

CREATE TABLE configuracoes_de_coleta (
    intervalo_de_horas NUMBER(10,0),
    temperatura_max NUMBER(10,0),
    tempo_de_umidade_min NUMBER(10,0),
    umidade_min NUMBER(10,0),
    id_config NUMBER(19,0) GENERATED AS IDENTITY,
    usuario_pertencente NUMBER(19,0),
    descricao_config VARCHAR2(255 CHAR),
    nm_config VARCHAR2(255 CHAR),
    PRIMARY KEY (id_config),
    CONSTRAINT UK_PARA_IDENTIFICAR_CONFIG_DUPLICADA UNIQUE (nm_config, usuario_pertencente)
);

CREATE TABLE dispositivos (
    config_atual NUMBER(19,0),
    dono NUMBER(19,0) NOT NULL,
    id_dispositivo NUMBER(19,0) GENERATED AS IDENTITY,
    identificador VARCHAR2(255 CHAR),
    marca VARCHAR2(255 CHAR),
    nm_dispositivo VARCHAR2(255 CHAR),
    PRIMARY KEY (id_dispositivo),
    CONSTRAINT UK_IDENTIFICADOR UNIQUE (identificador)
);

CREATE TABLE dados_coletados (
    temperatura NUMBER(10,0),
    umidade NUMBER(10,0),
    datatime_do_dado_coletado TIMESTAMP(6),
    dispositivo_pertencente NUMBER(19,0),
    id_dado_coletado NUMBER(19,0) GENERATED AS IDENTITY,
    identificador VARCHAR2(255 CHAR),
    PRIMARY KEY (id_dado_coletado),
    CONSTRAINT UK_IDENTIFICADORA_DE_DADOS_DUPLICADOS UNIQUE (datatime_do_dado_coletado, identificador)
);
    
-- Criando tabelas de auditoria
-- Tabela de Auditoria roles
CREATE TABLE audit_roles (
    audit_id NUMBER(19,0) GENERATED AS IDENTITY,
    operation_type VARCHAR2(10 CHAR),
    id_role NUMBER(19,0),
    nm_role VARCHAR2(255 CHAR),
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id)
);

-- Tabela de Auditoria pessoas
CREATE TABLE audit_pessoas (
    audit_id NUMBER(19,0) GENERATED AS IDENTITY,
    operation_type VARCHAR2(10 CHAR),
    id_pessoa NUMBER(19,0),
    nm_pessoa VARCHAR2(255 CHAR),
    sobrenome VARCHAR2(255 CHAR),
    dt_nasc DATE,
    cpf VARCHAR2(255 CHAR),
    telefone VARCHAR2(255 CHAR),
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id)
);

-- Tabela de Auditoria usuarios
CREATE TABLE audit_usuarios (
    audit_id NUMBER(19,0) GENERATED AS IDENTITY,
    operation_type VARCHAR2(10 CHAR),
    id_user NUMBER(19,0),
    pessoa NUMBER(19,0),
    user_role NUMBER(19,0),
    email VARCHAR2(255 CHAR),
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id)
);

-- Tabela de Auditoria configuracoes_de_coleta
CREATE TABLE audit_configuracoes_de_coleta (
    audit_id NUMBER(19,0) GENERATED AS IDENTITY,
    operation_type VARCHAR2(10 CHAR),
    id_config NUMBER(19,0),
    nm_config VARCHAR2(255 CHAR),
    descricao_config VARCHAR2(255 CHAR),
    temperatura_max NUMBER(10,0),
    umidade_min NUMBER(10,0),
    tempo_de_umidade_min NUMBER(10,0),
    intervalo_de_horas NUMBER(10,0),
    usuario_pertencente NUMBER(19,0),
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id)
);

-- Tabela de Auditoria dispositivos
CREATE TABLE audit_dispositivos (
    audit_id NUMBER(19,0) GENERATED AS IDENTITY,
    operation_type VARCHAR2(10 CHAR),
    id_dispositivo NUMBER(19,0),
    nm_dispositivo VARCHAR2(255 CHAR),
    marca VARCHAR2(255 CHAR),
    identificador VARCHAR2(255 CHAR),
    dono NUMBER(19,0),
    config_atual NUMBER(19,0),
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id)
);

-- Tabela de Auditoria dados_coletados
CREATE TABLE audit_dados_coletados (
    audit_id NUMBER(19,0) GENERATED AS IDENTITY,
    operation_type VARCHAR2(10 CHAR),
    id_dado_coletado NUMBER(19,0),
    temperatura NUMBER(10,0),
    umidade NUMBER(10,0),
    datatime_do_dado_coletado TIMESTAMP(6),
    identificador VARCHAR2(255 CHAR),
    dispositivo_pertencente NUMBER(19,0),
    operation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (audit_id)
);

-- Criando as chaves estrangeiras
ALTER TABLE usuarios 
    ADD CONSTRAINT FK_PESSOA_USUARIO 
    FOREIGN KEY (pessoa) REFERENCES pessoas;

ALTER TABLE usuarios 
    ADD CONSTRAINT FK_USER_ROLE 
    FOREIGN KEY (user_role) REFERENCES roles;

ALTER TABLE configuracoes_de_coleta 
    ADD CONSTRAINT FK_USUARIO_PERTENCENTE 
    FOREIGN KEY (usuario_pertencente) REFERENCES usuarios;

ALTER TABLE dispositivos 
    ADD CONSTRAINT FK_CONFIGURACAO_ATUAL_DO_DISPOSITVO 
    FOREIGN KEY (config_atual) REFERENCES configuracoes_de_coleta;

ALTER TABLE dispositivos 
    ADD CONSTRAINT FK_DONO_DO_DISPOSITIVO 
    FOREIGN KEY (dono) REFERENCES usuarios;

ALTER TABLE dados_coletados 
    ADD CONSTRAINT FK_DISPOSITIVO_PERTENCENTE 
    FOREIGN KEY (dispositivo_pertencente) REFERENCES dispositivos;

-- Criando os Triggers
-- Trigger Auditoria roles
CREATE OR REPLACE TRIGGER trg_audit_roles
AFTER INSERT OR UPDATE OR DELETE ON roles
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO audit_roles (operation_type, id_role, nm_role)
        VALUES ('INSERT', :NEW.id_role, :NEW.nm_role);
    ELSIF UPDATING THEN
        INSERT INTO audit_roles (operation_type, id_role, nm_role)
        VALUES ('UPDATE', :NEW.id_role, :NEW.nm_role);
    ELSIF DELETING THEN
        INSERT INTO audit_roles (operation_type, id_role, nm_role)
        VALUES ('DELETE', :OLD.id_role, :OLD.nm_role);
    END IF;
END;
/

-- Trigger Auditoria pessoas
CREATE OR REPLACE TRIGGER trg_audit_pessoas
AFTER INSERT OR UPDATE OR DELETE ON pessoas
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO audit_pessoas (operation_type, id_pessoa, nm_pessoa, sobrenome, dt_nasc, cpf, telefone)
        VALUES ('INSERT', :NEW.id_pessoa, :NEW.nm_pessoa, :NEW.sobrenome, :NEW.dt_nasc, :NEW.cpf, :NEW.telefone);
    ELSIF UPDATING THEN
        INSERT INTO audit_pessoas (operation_type, id_pessoa, nm_pessoa, sobrenome, dt_nasc, cpf, telefone)
        VALUES ('UPDATE', :NEW.id_pessoa, :NEW.nm_pessoa, :NEW.sobrenome, :NEW.dt_nasc, :NEW.cpf, :NEW.telefone);
    ELSIF DELETING THEN
        INSERT INTO audit_pessoas (operation_type, id_pessoa, nm_pessoa, sobrenome, dt_nasc, cpf, telefone)
        VALUES ('DELETE', :OLD.id_pessoa, :OLD.nm_pessoa, :OLD.sobrenome, :OLD.dt_nasc, :OLD.cpf, :OLD.telefone);
    END IF;
END;
/

-- Trigger Auditoria usuarios
CREATE OR REPLACE TRIGGER trg_audit_usuarios
AFTER INSERT OR UPDATE OR DELETE ON usuarios
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO audit_usuarios (operation_type, id_user, pessoa, user_role, email)
        VALUES ('INSERT', :NEW.id_user, :NEW.pessoa, :NEW.user_role, :NEW.email);
    ELSIF UPDATING THEN
        INSERT INTO audit_usuarios (operation_type, id_user, pessoa, user_role, email)
        VALUES ('UPDATE', :NEW.id_user, :NEW.pessoa, :NEW.user_role, :NEW.email);
    ELSIF DELETING THEN
        INSERT INTO audit_usuarios (operation_type, id_user, pessoa, user_role, email)
        VALUES ('DELETE', :OLD.id_user, :OLD.pessoa, :OLD.user_role, :OLD.email);
    END IF;
END;
/

-- Trigger Auditoria configuracoes_de_coleta
CREATE OR REPLACE TRIGGER trg_audit_configuracoes_de_coleta
AFTER INSERT OR UPDATE OR DELETE ON configuracoes_de_coleta
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO audit_configuracoes_de_coleta (operation_type, id_config, nm_config, descricao_config, temperatura_max, umidade_min, tempo_de_umidade_min, intervalo_de_horas, usuario_pertencente)
        VALUES ('INSERT', :NEW.id_config, :NEW.nm_config, :NEW.descricao_config, :NEW.temperatura_max, :NEW.umidade_min, :NEW.tempo_de_umidade_min, :NEW.intervalo_de_horas, :NEW.usuario_pertencente);
    ELSIF UPDATING THEN
        INSERT INTO audit_configuracoes_de_coleta (operation_type, id_config, nm_config, descricao_config, temperatura_max, umidade_min, tempo_de_umidade_min, intervalo_de_horas, usuario_pertencente)
        VALUES ('UPDATE', :NEW.id_config, :NEW.nm_config, :NEW.descricao_config, :NEW.temperatura_max, :NEW.umidade_min, :NEW.tempo_de_umidade_min, :NEW.intervalo_de_horas, :NEW.usuario_pertencente);
    ELSIF DELETING THEN
        INSERT INTO audit_configuracoes_de_coleta (operation_type, id_config, nm_config, descricao_config, temperatura_max, umidade_min, tempo_de_umidade_min, intervalo_de_horas, usuario_pertencente)
        VALUES ('DELETE', :OLD.id_config, :OLD.nm_config, :OLD.descricao_config, :OLD.temperatura_max, :OLD.umidade_min, :OLD.tempo_de_umidade_min, :OLD.intervalo_de_horas, :OLD.usuario_pertencente);
    END IF;
END;
/

-- Trigger Auditoria dispositivos
CREATE OR REPLACE TRIGGER trg_audit_dispositivos
AFTER INSERT OR UPDATE OR DELETE ON dispositivos
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO audit_dispositivos (operation_type, id_dispositivo, nm_dispositivo, marca, identificador, dono, config_atual)
        VALUES ('INSERT', :NEW.id_dispositivo, :NEW.nm_dispositivo, :NEW.marca, :NEW.identificador, :NEW.dono, :NEW.config_atual);
    ELSIF UPDATING THEN
        INSERT INTO audit_dispositivos (operation_type, id_dispositivo, nm_dispositivo, marca, identificador, dono, config_atual)
        VALUES ('UPDATE', :NEW.id_dispositivo, :NEW.nm_dispositivo, :NEW.marca, :NEW.identificador, :NEW.dono, :NEW.config_atual);
    ELSIF DELETING THEN
        INSERT INTO audit_dispositivos (operation_type, id_dispositivo, nm_dispositivo, marca, identificador, dono, config_atual)
        VALUES ('DELETE', :OLD.id_dispositivo, :OLD.nm_dispositivo, :OLD.marca, :OLD.identificador, :OLD.dono, :OLD.config_atual);
    END IF;
END;
/

-- Trigger Auditoria dados_coletados
CREATE OR REPLACE TRIGGER trg_audit_dados_coletados
AFTER INSERT OR UPDATE OR DELETE ON dados_coletados
FOR EACH ROW
BEGIN
    IF INSERTING THEN
        INSERT INTO audit_dados_coletados (operation_type, id_dado_coletado, temperatura, umidade, datatime_do_dado_coletado, identificador, dispositivo_pertencente)
        VALUES ('INSERT', :NEW.id_dado_coletado, :NEW.temperatura, :NEW.umidade, :NEW.datatime_do_dado_coletado, :NEW.identificador, :NEW.dispositivo_pertencente);
    ELSIF UPDATING THEN
        INSERT INTO audit_dados_coletados (operation_type, id_dado_coletado, temperatura, umidade, datatime_do_dado_coletado, identificador, dispositivo_pertencente)
        VALUES ('UPDATE', :NEW.id_dado_coletado, :NEW.temperatura, :NEW.umidade, :NEW.datatime_do_dado_coletado, :NEW.identificador, :NEW.dispositivo_pertencente);
    ELSIF DELETING THEN
        INSERT INTO audit_dados_coletados (operation_type, id_dado_coletado, temperatura, umidade, datatime_do_dado_coletado, identificador, dispositivo_pertencente)
        VALUES ('DELETE', :OLD.id_dado_coletado, :OLD.temperatura, :OLD.umidade, :OLD.datatime_do_dado_coletado, :OLD.identificador, :OLD.dispositivo_pertencente);
    END IF;
END;
/


SET SERVEROUTPUT ON; 

-- Procedures que realizam os inserts
-- Procedure para tabela ROLES
CREATE OR REPLACE PROCEDURE INSERT_ROLE(
    p_nm_role IN VARCHAR2
) AS
BEGIN
    INSERT INTO roles (nm_role) VALUES (p_nm_role);
    COMMIT;
END;
/

-- Procedure para tabela PESSOAS
CREATE OR REPLACE PROCEDURE INSERT_PESSOA(
    p_nm_pessoa IN VARCHAR2,
    p_sobrenome IN VARCHAR2,
    p_dt_nasc IN DATE,
    p_cpf IN VARCHAR2,
    p_telefone IN VARCHAR2
) AS
BEGIN
    INSERT INTO pessoas (nm_pessoa, sobrenome, dt_nasc, cpf, telefone)
    VALUES (p_nm_pessoa, p_sobrenome, p_dt_nasc, p_cpf, p_telefone);
    COMMIT;
END;
/

-- Procedure para tabela USUARIOS
CREATE OR REPLACE PROCEDURE INSERT_USUARIO(
    p_email IN VARCHAR2,
    p_password IN VARCHAR2,
    p_pessoa IN NUMBER,
    p_user_role IN NUMBER
) AS
BEGIN
    INSERT INTO usuarios (email, password, pessoa, user_role)
    VALUES (p_email, p_password, p_pessoa, p_user_role);
    COMMIT;
END;
/

-- Procedure para tabela CONFIGURACOES_DE_COLETA
CREATE OR REPLACE PROCEDURE INSERT_CONFIGURACAO(
    p_nm_config IN VARCHAR2,
    p_descricao_config IN VARCHAR2,
    p_temperatura_max IN NUMBER,
    p_umidade_min IN NUMBER,
    p_tempo_de_umidade_min IN NUMBER,
    p_intervalo_de_horas IN NUMBER,
    p_usuario_pertencente IN NUMBER
) AS
BEGIN
    INSERT INTO configuracoes_de_coleta (
        nm_config, descricao_config, temperatura_max, umidade_min, 
        tempo_de_umidade_min, intervalo_de_horas, usuario_pertencente
    )
    VALUES (
        p_nm_config, p_descricao_config, p_temperatura_max, p_umidade_min, 
        p_tempo_de_umidade_min, p_intervalo_de_horas, p_usuario_pertencente
    );
    COMMIT;
END;
/

-- Procedure para tabela DISPOSITIVOS
CREATE OR REPLACE PROCEDURE INSERT_DISPOSITIVO(
    p_nm_dispositivo IN VARCHAR2,
    p_marca IN VARCHAR2,
    p_identificador IN VARCHAR2,
    p_dono IN NUMBER,
    p_config_atual IN NUMBER
) AS
BEGIN
    INSERT INTO dispositivos (nm_dispositivo, marca, identificador, dono, config_atual)
    VALUES (p_nm_dispositivo, p_marca, p_identificador, p_dono, p_config_atual);
    COMMIT;
END;
/

-- Procedure para tabela DADOS_COLETADOS
CREATE OR REPLACE PROCEDURE INSERT_DADO_COLETADO(
    p_temperatura IN NUMBER,
    p_umidade IN NUMBER,
    p_datatime_do_dado_coletado IN TIMESTAMP,
    p_dispositivo_pertencente IN NUMBER,
    p_identificador IN VARCHAR2
) AS
BEGIN
    INSERT INTO dados_coletados (
        temperatura, umidade, datatime_do_dado_coletado, dispositivo_pertencente, identificador
    )
    VALUES (
        p_temperatura, p_umidade, p_datatime_do_dado_coletado, p_dispositivo_pertencente, p_identificador
    );
    COMMIT;
END;
/


-- Insercao de roles
BEGIN
    INSERT_ROLE('ROLE_ADMIN');
    INSERT_ROLE('ROLE_FREE');
    INSERT_ROLE('ROLE_PREMIUM');
    INSERT_ROLE('ROLE_SUPORTE');
    INSERT_ROLE('ROLE_MONITORAMENTO');
    INSERT_ROLE('ROLE_TESTER');
    INSERT_ROLE('ROLE_QA');
    INSERT_ROLE('ROLE_DEV');
    INSERT_ROLE('ROLE_FINANCEIRO');
    INSERT_ROLE('ROLE_FRANQUEADOS');
END;
/

-- Insercao de pessoa 
BEGIN
    INSERT_PESSOA('Macirander', 'Silva', TO_DATE('1990-04-15', 'YYYY-MM-DD'), '53775555382', '11987654321');
    INSERT_PESSOA('Amanda', 'Santos', TO_DATE('1995-08-25', 'YYYY-MM-DD'), '05852175137', '11987654322');
    INSERT_PESSOA('Lucas', 'Costa', TO_DATE('1992-11-10', 'YYYY-MM-DD'), '62261415575', '11987654323');
    INSERT_PESSOA('Bruna', 'Oliveira', TO_DATE('1989-02-20', 'YYYY-MM-DD'), '60412367211', '11987654324');
    INSERT_PESSOA('Carlos', 'Pereira', TO_DATE('1994-07-13', 'YYYY-MM-DD'), '12309441252', '11987654325');
    INSERT_PESSOA('Mariana', 'Lima', TO_DATE('1988-01-05', 'YYYY-MM-DD'), '68879496875', '11987654326');
    INSERT_PESSOA('Ricardo', 'Alves', TO_DATE('1991-06-30', 'YYYY-MM-DD'), '55460660831', '11987654327');
    INSERT_PESSOA('Juliana', 'Rodrigues', TO_DATE('1993-12-17', 'YYYY-MM-DD'), '73654742700', '11987654328');
    INSERT_PESSOA('Roberto', 'Gomes', TO_DATE('1996-05-03', 'YYYY-MM-DD'), '72615211404', '11987654329');
    INSERT_PESSOA('Tatiane', 'Martins', TO_DATE('1990-09-21', 'YYYY-MM-DD'), '29445433858', '11987654330');
END;
/

-- Insercao de usuario
BEGIN
    INSERT_USUARIO('macirander@gmail.com', '$2a$12$Zvkma38pz7JXx51ONAIPleA7j7HxcCrNy2xtq2J73WrxTKckB.c5W', 1, 1);  -- Admin
    INSERT_USUARIO('amandacontato@gmail.com', '$2a$12$bvNw3g5vm.09fK6QNexiXuHDu4ScHJ.gIQcSL9NGgX7JAElG3YSFm', 2, 2);  -- Free
    INSERT_USUARIO('lucasport@gmail.com', '$2a$12$QNbwrgWip17ey0B6ggOI1OFuApQKRJXE2E13dBYyRMz8S8/1WpIdG', 3, 2);  -- Free
    INSERT_USUARIO('brunassr@gmail.com', '$2a$12$Kb1/D1nWFQeddbIKOo21MeQa7Si.KTjfxHrKxLATdILl5kEp0l43q', 4, 2);  -- Free
    INSERT_USUARIO('carloscrls@hotmail.com', '$2a$12$BX51rgQpVpElcCSExeAwiOTVZI5lk2AOxIZbRs.c65JL.Ip4Tmaqy', 5, 2);  -- Free
    INSERT_USUARIO('marianajusto@hotmail.com', '$2a$12$UBGufaHibBTVSghns6ViM.ct4oV0UcnKoQ4ILVfAQNCbPCenPXfqq', 6, 3);  -- Premium
    INSERT_USUARIO('ricardo412@outlook.com', '$2a$12$/hvW5ynEtPATMQ/Sa.3tr.jq7DbcgSaVj5XTWAwUnkKTzlYHd.NAW', 7, 3);  -- Premium
    INSERT_USUARIO('julianaju12@outlook.com', '$2a$12$TjloVUZSHW/ZdQcJD1hl9.4ggK.jljgSm4y3Pxw6.nPedbZ26P562', 8, 3);  -- Premium
    INSERT_USUARIO('robertokakaroto2@outlook.com', '$2a$12$yuRgWxVTRVEKBe9sMEh8TOcZnUf1B6/hgOexaTD/K.cg.pd74PPLC', 9, 3);  -- Premium
    INSERT_USUARIO('tatianevilma5@outlook.com', '$2a$12$PcTa2m4ADLwTyjsDkSofiOsqAgBoPHtgGQwqpQqpAF4phgwa0QUUi', 10, 3);  -- Premium
END;
/

-- Insercao de configuracao
BEGIN
    INSERT_CONFIGURACAO('config_basica_1', 'Configuracao_padrao_1', 50, 30, 10, 4, 1);
    INSERT_CONFIGURACAO('config_basica_2', 'Configuracao_padrao_2', 60, 35, 15, 5, 1);
    INSERT_CONFIGURACAO('config_basica_3', 'Configuracao_padrao_3', 70, 40, 20, 6, 1);
    INSERT_CONFIGURACAO('config_basica_4', 'Configuracao_padrao_4', 80, 45, 25, 7, 1);
    INSERT_CONFIGURACAO('config_basica_5', 'Configuracao_padrao_5', 90, 50, 30, 8, 1);

    INSERT_CONFIGURACAO('config_personalizada_1', 'Configuracao personalizada para Mariana', NULL, 60, 20, 6, 6);
    INSERT_CONFIGURACAO('config_personalizada_2', 'Configuracao personalizada para Ricardo', 75, 50, 30, 5, 7);
    INSERT_CONFIGURACAO('config_personalizada_3', 'Configuracao personalizada para Juliana', 80, 45, 25, 7, 8);
    INSERT_CONFIGURACAO('config_personalizada_4', 'Configuracao personalizada para Roberto', 85, 48, 28, 6, 9);
    INSERT_CONFIGURACAO('config_personalizada_5', 'Configuracao personalizada para Tatiane', 90, 55, 35, 8, 10);
END;
/


-- Insercao de dispositivo
BEGIN
    INSERT_DISPOSITIVO('Placa Solar Direita', 'Marca A - Modelo X', 'DS001', 1, 1);
    INSERT_DISPOSITIVO('Placa Solar Esquerda', 'Marca A - Modelo X', 'DS002', 2, 2);
    INSERT_DISPOSITIVO('Placa Solar Norte', 'Marca B - Modelo Y', 'DS003', 3, 2);
    INSERT_DISPOSITIVO('Placa Solar Sul', 'Marca B - Modelo Y', 'DS004', 4, 2);
    INSERT_DISPOSITIVO('Placa Solar Leste', 'Marca C - Modelo Z', 'DS005', 5, 2);
    INSERT_DISPOSITIVO('Placa Solar Oeste', 'Marca C - Modelo Z', 'DS006', 6, 3);
    INSERT_DISPOSITIVO('Placa Solar Superior', 'Marca D - Modelo A', 'DS007', 7, 3);
    INSERT_DISPOSITIVO('Placa Solar Inferior', 'Marca D - Modelo A', 'DS008', 8, 3);
    INSERT_DISPOSITIVO('Placa Solar Central', 'Marca E - Modelo B', 'DS009', 9, 3);
    INSERT_DISPOSITIVO('Placa Solar Extrema', 'Marca E - Modelo B', 'DS010', 10, 3);
END;
/


-- Insercao de dado coletado
BEGIN
    -- Dispositivo DS001
    INSERT_DADO_COLETADO(35, 50, TO_TIMESTAMP('2024-11-19 06:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'DS001');
    INSERT_DADO_COLETADO(36, 51, TO_TIMESTAMP('2024-11-19 07:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'DS001');
    INSERT_DADO_COLETADO(37, 52, TO_TIMESTAMP('2024-11-19 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 1, 'DS001');
    
    -- Dispositivo DS002
    INSERT_DADO_COLETADO(38, 53, TO_TIMESTAMP('2024-11-19 06:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'DS002');
    INSERT_DADO_COLETADO(39, 54, TO_TIMESTAMP('2024-11-19 07:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'DS002');
    INSERT_DADO_COLETADO(40, 55, TO_TIMESTAMP('2024-11-19 08:30:00', 'YYYY-MM-DD HH24:MI:SS'), 2, 'DS002');
    
    -- Dispositivo DS003
    INSERT_DADO_COLETADO(41, 56, TO_TIMESTAMP('2024-11-19 06:45:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'DS003');
    INSERT_DADO_COLETADO(42, 57, TO_TIMESTAMP('2024-11-19 07:45:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'DS003');
    INSERT_DADO_COLETADO(43, 58, TO_TIMESTAMP('2024-11-19 08:45:00', 'YYYY-MM-DD HH24:MI:SS'), 3, 'DS003');
    
    -- Dispositivo DS004
    INSERT_DADO_COLETADO(44, 59, TO_TIMESTAMP('2024-11-19 07:15:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'DS004');
    INSERT_DADO_COLETADO(45, 60, TO_TIMESTAMP('2024-11-19 08:15:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'DS004');
    INSERT_DADO_COLETADO(46, 61, TO_TIMESTAMP('2024-11-19 09:15:00', 'YYYY-MM-DD HH24:MI:SS'), 4, 'DS004');
    
    -- Dispositivo DS005
    INSERT_DADO_COLETADO(47, 62, TO_TIMESTAMP('2024-11-19 07:45:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'DS005');
    INSERT_DADO_COLETADO(48, 63, TO_TIMESTAMP('2024-11-19 08:45:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'DS005');
    INSERT_DADO_COLETADO(49, 64, TO_TIMESTAMP('2024-11-19 09:45:00', 'YYYY-MM-DD HH24:MI:SS'), 5, 'DS005');
    
    -- Dispositivo DS006
    INSERT_DADO_COLETADO(50, 65, TO_TIMESTAMP('2024-11-19 07:50:00', 'YYYY-MM-DD HH24:MI:SS'), 6, 'DS006');
    INSERT_DADO_COLETADO(51, 66, TO_TIMESTAMP('2024-11-19 08:50:00', 'YYYY-MM-DD HH24:MI:SS'), 6, 'DS006');
    INSERT_DADO_COLETADO(52, 67, TO_TIMESTAMP('2024-11-19 09:50:00', 'YYYY-MM-DD HH24:MI:SS'), 6, 'DS006');
    
    -- Dispositivo DS007
    INSERT_DADO_COLETADO(53, 68, TO_TIMESTAMP('2024-11-19 08:00:00', 'YYYY-MM-DD HH24:MI:SS'), 7, 'DS007');
    INSERT_DADO_COLETADO(54, 69, TO_TIMESTAMP('2024-11-19 09:00:00', 'YYYY-MM-DD HH24:MI:SS'), 7, 'DS007');
    INSERT_DADO_COLETADO(55, 70, TO_TIMESTAMP('2024-11-19 10:00:00', 'YYYY-MM-DD HH24:MI:SS'), 7, 'DS007');
END;
/


-- Package validacoes
-- agrupa funcoes relacionadas a validacao
CREATE OR REPLACE PACKAGE pkg_validacoes AS
    FUNCTION validar_cpf(p_cpf IN VARCHAR2) RETURN VARCHAR2;

    FUNCTION validar_data_nascimento(p_data_nasc IN DATE) RETURN VARCHAR2;
END pkg_validacoes;
/

CREATE OR REPLACE PACKAGE BODY pkg_validacoes AS

    FUNCTION validar_cpf(p_cpf IN VARCHAR2) RETURN VARCHAR2 IS
        v_cpf_limpo VARCHAR2(11);
        soma NUMBER;
        resto NUMBER;
        digito1 NUMBER;
        digito2 NUMBER;
    BEGIN
        v_cpf_limpo := REGEXP_REPLACE(p_cpf, '[^0-9]', '');

        IF LENGTH(v_cpf_limpo) != 11 THEN
            RETURN 'CPF invalido: numero de digitos incorreto';
        END IF;

        IF REGEXP_LIKE(v_cpf_limpo, '^([0-9])\1{10}$') THEN
            RETURN 'CPF invalido: todos os digitos iguais';
        END IF;

        soma := 0;
        FOR i IN 1..9 LOOP
            soma := soma + TO_NUMBER(SUBSTR(v_cpf_limpo, i, 1)) * (11 - i);
        END LOOP;
        resto := MOD(soma * 10, 11);
        digito1 := CASE WHEN resto = 10 THEN 0 ELSE resto END;

        soma := 0;
        FOR i IN 1..10 LOOP
            soma := soma + TO_NUMBER(SUBSTR(v_cpf_limpo, i, 1)) * (12 - i);
        END LOOP;
        resto := MOD(soma * 10, 11);
        digito2 := CASE WHEN resto = 10 THEN 0 ELSE resto END;

        IF digito1 != TO_NUMBER(SUBSTR(v_cpf_limpo, 10, 1)) OR
           digito2 != TO_NUMBER(SUBSTR(v_cpf_limpo, 11, 1)) THEN
            RETURN 'CPF invalido: dIgitos verificadores incorretos';
        END IF;

        RETURN 'CPF valido';
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 'Erro inesperado ao validar CPF: ' || SQLERRM;
    END;

    FUNCTION validar_data_nascimento(p_data_nasc IN DATE) RETURN VARCHAR2 IS
        v_hoje DATE := SYSDATE;
        v_idade NUMBER;
    BEGIN
        IF p_data_nasc > v_hoje THEN
            RETURN 'Data de nascimento invalida: data no futuro';
        END IF;

        v_idade := TRUNC(MONTHS_BETWEEN(v_hoje, p_data_nasc) / 12);

        IF v_idade < 18 THEN
            RETURN 'Data de nascimento invalida: menor de 18 anos';
        END IF;

        RETURN 'Data de nascimento valida';
    EXCEPTION
        WHEN OTHERS THEN
            RETURN 'Erro inesperado ao validar a data de nascimento: ' || SQLERRM;
    END;

END pkg_validacoes;
/


-- Package exportacao
CREATE OR REPLACE PACKAGE pkg_exportacao IS
    PROCEDURE exportar_dados_json;
END pkg_exportacao;
/

CREATE OR REPLACE PACKAGE BODY pkg_exportacao IS

    PROCEDURE exportar_dados_json IS
        v_json CLOB;
    BEGIN
        SELECT JSON_ARRAYAGG(
                   JSON_OBJECT(
                       'id_usuario' VALUE u.id_user,
                       'nome' VALUE p.nm_pessoa,
                       'sobrenome' VALUE p.sobrenome,
                       'email' VALUE u.email,
                       'role' VALUE r.nm_role
                   )
               ) 
        INTO v_json
        FROM usuarios u
        JOIN pessoas p ON u.pessoa = p.id_pessoa
        JOIN roles r ON u.user_role = r.id_role;

        DBMS_OUTPUT.PUT_LINE(v_json);
    EXCEPTION
        WHEN OTHERS THEN
            DBMS_OUTPUT.PUT_LINE('Erro ao exportar dados: ' || SQLERRM);
    END exportar_dados_json;

END pkg_exportacao;
/

EXEC pkg_exportacao.exportar_dados_json;
