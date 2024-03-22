CREATE DATABASE IF NOT EXISTS pmsys;
USE pmsys;

-- Criação da tabela de Usuário
CREATE TABLE Usuario (
    ID_Usuario INT AUTO_INCREMENT PRIMARY KEY,
    NomeUsuario VARCHAR(255),
    Email VARCHAR(50),
    Senha  VARCHAR(14),
    Telefone VARCHAR(14)
);

-- Criação da tabela de NotaFiscal & Fornecedor
CREATE TABLE NotaFiscal (
    ID_NotaFiscal INT AUTO_INCREMENT PRIMARY KEY,
    numeroFornecedor INT,
    nameFornecedor VARCHAR(45),
    numeroCnpj VARCHAR(30),
    dataEmissao DATE,
    dataRecebimento DATE,
    tempoSegundos INT
);

-- Criação da tabela de Tempo
CREATE TABLE Tempo (
    ID_Tempo INT AUTO_INCREMENT PRIMARY KEY,
    ID_NotaFiscal INT,
    Cronometro TIME,
    DataConferencia DATE,
    FOREIGN KEY (ID_NotaFiscal) REFERENCES NotaFiscal(ID_NotaFiscal)
);
