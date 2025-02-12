-- Criando as sequences
CREATE SEQUENCE seq_cliente;
CREATE SEQUENCE seq_produto;

ALTER SEQUENCE seq_cliente OWNER TO techchallenge_usr;
ALTER SEQUENCE seq_produto OWNER TO techchallenge_usr;

-- Criando a tabela clientes
CREATE TABLE public.cliente (
     id int8 DEFAULT nextval('seq_cliente'::regclass) NOT NULL,
     nome varchar(255) NOT NULL,
     email varchar(255) NOT NULL,
     cpf char(14) NOT NULL,
     CONSTRAINT pk_clientes PRIMARY KEY (id),
     CONSTRAINT uq_email UNIQUE (cpf)
);

ALTER TABLE public.cliente OWNER TO techchallenge_usr;

INSERT INTO cliente (nome, email, cpf) VALUES
('João Silva', 'joao.silva@example.com', '123.456.789-00'),
('Maria Souza', 'maria.souza@example.com', '234.567.890-11'),
('Pedro Oliveira', 'pedro.oliveira@example.com', '345.678.901-22'),
('Ana Santos', 'ana.santos@example.com', '456.789.012-33'),
('Carlos Pereira', 'carlos.pereira@example.com', '567.890.123-44'),
('Paula Lima', 'paula.lima@example.com', '678.901.234-55'),
('Ricardo Costa', 'ricardo.costa@example.com', '789.012.345-66'),
('Fernanda Almeida', 'fernanda.almeida@example.com', '890.123.456-77'),
('Roberto Martins', 'roberto.martins@example.com', '901.234.567-88'),
('Juliana Ferreira', 'juliana.ferreira@example.com', '012.345.678-99');

-- Criando a tabela produto
CREATE TABLE public.produto (
	id int8 DEFAULT nextval('seq_produto'::regclass) NOT NULL,
	categoria int2 NOT NULL,
	valor float8 NOT NULL,
	descricao varchar(255) NOT NULL,
	nome varchar(255) NOT NULL,
	CONSTRAINT produto_categoria_check CHECK (((categoria >= 0) AND (categoria <= 3))),
	CONSTRAINT pk_produto PRIMARY KEY (id)
);

ALTER TABLE public.produto OWNER TO techchallenge_usr;

-- Sanduíches (Categoria 0)
INSERT INTO public.produto (categoria, valor, descricao, nome)
VALUES
(0, 12.50, 'Sanduíche de carne bovina com queijo e salada', 'Cheeseburger'),
(0, 14.00, 'Sanduíche de frango grelhado com alface e tomate', 'Chicken Sandwich'),
(0, 15.50, 'Sanduíche de carne bovina com bacon e cheddar', 'Bacon Cheeseburger'),
(0, 16.00, 'Sanduíche de carne bovina com cogumelos e queijo suíço', 'Mushroom Swiss Burger'),
(0, 13.50, 'Sanduíche de carne bovina com picles e molho especial', 'Classic Burger'),
(0, 11.00, 'Sanduíche de frango empanado com maionese', 'Crispy Chicken Sandwich'),
(0, 17.00, 'Sanduíche duplo de carne bovina com cheddar', 'Double Cheeseburger'),
(0, 14.50, 'Sanduíche vegetariano com queijo e salada', 'Veggie Burger'),
(0, 18.00, 'Sanduíche de carne bovina com cebola caramelizada e queijo brie', 'Gourmet Burger'),
(0, 19.00, 'Sanduíche de carne bovina com gorgonzola e nozes', 'Blue Cheese Burger');

-- Acompanhamentos (Categoria 1)
INSERT INTO public.produto (categoria, valor, descricao, nome)
VALUES
(1, 5.00, 'Batata frita crocante', 'Batata Frita'),
(1, 7.00, 'Anéis de cebola empanados', 'Anéis de Cebola'),
(1, 6.50, 'Batata frita temperada com queijo e bacon', 'Batata Cheddar Bacon'),
(1, 5.50, 'Batata frita com molho cheddar', 'Batata Cheddar'),
(1, 4.00, 'Porção de nuggets de frango', 'Nuggets'),
(1, 6.00, 'Porção de mandioca frita', 'Mandioca Frita'),
(1, 4.50, 'Porção de batata doce frita', 'Batata Doce Frita'),
(1, 7.50, 'Mini coxinhas de frango', 'Mini Coxinhas'),
(1, 6.00, 'Porção de polenta frita', 'Polenta Frita'),
(1, 8.00, 'Bolinho de queijo', 'Bolinho de Queijo');

-- Bebidas (Categoria 2)
INSERT INTO public.produto (categoria, valor, descricao, nome)
VALUES
(2, 3.50, 'Refrigerante de cola 350ml', 'Refrigerante Cola'),
(2, 3.50, 'Refrigerante de guaraná 350ml', 'Refrigerante Guaraná'),
(2, 4.00, 'Suco de laranja 300ml', 'Suco de Laranja'),
(2, 4.00, 'Suco de uva 300ml', 'Suco de Uva'),
(2, 5.00, 'Água mineral com gás 500ml', 'Água Mineral com Gás'),
(2, 4.50, 'Chá gelado de limão 350ml', 'Chá Gelado de Limão'),
(2, 4.50, 'Chá gelado de pêssego 350ml', 'Chá Gelado de Pêssego'),
(2, 2.50, 'Água mineral sem gás 500ml', 'Água Mineral'),
(2, 6.00, 'Café expresso 150ml', 'Café Expresso'),
(2, 7.00, 'Milkshake de chocolate 400ml', 'Milkshake de Chocolate');

-- Sobremesas (Categoria 3)
INSERT INTO public.produto (categoria, valor, descricao, nome)
VALUES
(3, 8.50, 'Torta de maçã', 'Torta de Maçã'),
(3, 9.00, 'Brownie com sorvete de baunilha', 'Brownie com Sorvete'),
(3, 7.00, 'Pudim de leite condensado', 'Pudim de Leite'),
(3, 6.50, 'Mousse de chocolate', 'Mousse de Chocolate'),
(3, 7.50, 'Sorvete de chocolate 2 bolas', 'Sorvete de Chocolate'),
(3, 7.50, 'Sorvete de morango 2 bolas', 'Sorvete de Morango'),
(3, 7.50, 'Sorvete de creme 2 bolas', 'Sorvete de Creme'),
(3, 9.00, 'Petit gâteau com sorvete de baunilha', 'Petit Gâteau'),
(3, 5.00, 'Brigadeiro', 'Brigadeiro'),
(3, 8.00, 'Cheesecake de frutas vermelhas', 'Cheesecake');