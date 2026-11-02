-- Script de banco de dados da MecâniQA (OAT 3)
-- Banco de dados oficial: MySQL. Acesso via JDBC puro (sem ORM/JPA/Hibernate).

CREATE DATABASE IF NOT EXISTS mecaniqa;
USE mecaniqa;

-- DDL -------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS peca (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50),
    fornecedor_marca VARCHAR(100),
    quantidade_estoque INT,
    preco_custo DECIMAL(10,2),
    preco_venda DECIMAL(10,2),
    data_cadastro DATETIME(6),
    data_ultima_atualizacao DATETIME(6),
    tamanho VARCHAR(30),
    cor VARCHAR(30),
    categoria VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS servico (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150),
    tempo_estimado_minutos INT,
    custo_tabelado DECIMAL(10,2),
    data_criacao DATETIME(6),
    data_ultima_atualizacao DATETIME(6)
);

CREATE TABLE IF NOT EXISTS pedido_pecas (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(30),
    data_criacao DATETIME(6),
    data_ultima_atualizacao DATETIME(6)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    pedido_codigo BIGINT NOT NULL,
    peca_codigo BIGINT NOT NULL,
    quantidade INT NOT NULL,
    PRIMARY KEY (pedido_codigo, peca_codigo),
    FOREIGN KEY (pedido_codigo) REFERENCES pedido_pecas(codigo),
    FOREIGN KEY (peca_codigo) REFERENCES peca(codigo)
);

CREATE TABLE IF NOT EXISTS ordem_servico (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255),
    status VARCHAR(30),
    data_criacao DATETIME(6),
    data_ultima_atualizacao DATETIME(6)
);

CREATE TABLE IF NOT EXISTS ordem_servico_servico (
    ordem_servico_codigo BIGINT NOT NULL,
    servico_codigo BIGINT NOT NULL,
    PRIMARY KEY (ordem_servico_codigo, servico_codigo),
    FOREIGN KEY (ordem_servico_codigo) REFERENCES ordem_servico(codigo),
    FOREIGN KEY (servico_codigo) REFERENCES servico(codigo)
);

CREATE TABLE IF NOT EXISTS ordem_servico_pedido_pecas (
    ordem_servico_codigo BIGINT NOT NULL,
    pedido_pecas_codigo BIGINT NOT NULL,
    PRIMARY KEY (ordem_servico_codigo, pedido_pecas_codigo),
    FOREIGN KEY (ordem_servico_codigo) REFERENCES ordem_servico(codigo),
    FOREIGN KEY (pedido_pecas_codigo) REFERENCES pedido_pecas(codigo)
);

-- DML -------------------------------------------------------------------
-- (reservado para manipulação de dados; sem massa de dados inicial obrigatória)
