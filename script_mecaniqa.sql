-- Script do banco de dados da MecâniQA (OAT 3)
-- DDL: criação do banco e das tabelas
CREATE DATABASE IF NOT EXISTS mecaniqa;
USE mecaniqa;

CREATE TABLE IF NOT EXISTS pecas (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50),
    fornecedor_marca VARCHAR(100),
    quantidade_estoque INT,
    preco_custo DOUBLE,
    preco_venda DOUBLE,
    data_cadastro DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL,
    tamanho VARCHAR(20),
    cor VARCHAR(30),
    categoria VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS servicos (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    tempo_estimado_minutos INT,
    custo_tabelado DOUBLE,
    data_criacao DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos_pecas (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(30) NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    codigo_pedido BIGINT NOT NULL,
    codigo_peca BIGINT NOT NULL,
    quantidade INT NOT NULL,
    PRIMARY KEY (codigo_pedido, codigo_peca),
    FOREIGN KEY (codigo_pedido) REFERENCES pedidos_pecas (codigo),
    FOREIGN KEY (codigo_peca) REFERENCES pecas (codigo)
);

CREATE TABLE IF NOT EXISTS ordens_servico (
    codigo BIGINT AUTO_INCREMENT PRIMARY KEY,
    descricao VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    data_criacao DATETIME NOT NULL,
    data_ultima_atualizacao DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS ordem_servico_servicos (
    codigo_ordem BIGINT NOT NULL,
    codigo_servico BIGINT NOT NULL,
    PRIMARY KEY (codigo_ordem, codigo_servico),
    FOREIGN KEY (codigo_ordem) REFERENCES ordens_servico (codigo),
    FOREIGN KEY (codigo_servico) REFERENCES servicos (codigo)
);

CREATE TABLE IF NOT EXISTS ordem_servico_pedidos (
    codigo_ordem BIGINT NOT NULL,
    codigo_pedido BIGINT NOT NULL,
    PRIMARY KEY (codigo_ordem, codigo_pedido),
    FOREIGN KEY (codigo_ordem) REFERENCES ordens_servico (codigo),
    FOREIGN KEY (codigo_pedido) REFERENCES pedidos_pecas (codigo)
);

-- DML: dados iniciais de exemplo
INSERT INTO pecas (codigo_barras, fornecedor_marca, quantidade_estoque, preco_custo, preco_venda,
                   data_cadastro, data_ultima_atualizacao, tamanho, cor, categoria)
VALUES ('7891234567890', 'Bosch', 10, 50, 80, NOW(), NOW(), 'M', 'Preto', 'MOTOR');

INSERT INTO servicos (nome, tempo_estimado_minutos, custo_tabelado, data_criacao, data_ultima_atualizacao)
VALUES ('Revisão', 60, 150, NOW(), NOW());
