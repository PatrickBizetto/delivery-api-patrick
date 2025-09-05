-- data.sql (CORRIGIDO PARA NÃO INCLUIR IDs NA TABELA USUARIO)

-- Inserindo usuários sem especificar o ID. O banco de dados irá gerar automaticamente.
-- Senha para todos os usuários de teste: "123456"
INSERT INTO USUARIO (NOME, EMAIL, SENHA, ROLE, ATIVO, RESTAURANTE_ID) VALUES ('Admin Geral', 'admin@delivery.com', '$2a$10$DnUtdBLQhp7osUMrSZeTwuey5kZFXiLUTm/IJ9SYyfgJRT64GWWJq', 'ADMIN', true, NULL);
INSERT INTO USUARIO (NOME, EMAIL, SENHA, ROLE, ATIVO, RESTAURANTE_ID) VALUES ('Dono Pizzaria', 'dono.pizzaria@delivery.com', '$2a$10$DnUtdBLQhp7osUMrSZeTwuey5kZFXiLUTm/IJ9SYyfgJRT64GWWJq', 'RESTAURANTE', true, 1);
INSERT INTO USUARIO (NOME, EMAIL, SENHA, ROLE, ATIVO, RESTAURANTE_ID) VALUES ('Dono Cantina', 'dono.cantina@delivery.com', '$2a$10$DnUtdBLQhp7osUMrSZeTwuey5kZFXiLUTm/IJ9SYyfgJRT64GWWJq', 'RESTAURANTe', true, 2);
INSERT INTO USUARIO (NOME, EMAIL, SENHA, ROLE, ATIVO, RESTAURANTE_ID) VALUES ('Ana Cliente', 'ana.cliente@email.com', '$2a$10$DnUtdBLQhp7osUMrSZeTwuey5kZFXiLUTm/IJ9SYyfgJRT64GWWJq', 'CLIENTE', true, NULL);
INSERT INTO USUARIO (NOME, EMAIL, SENHA, ROLE, ATIVO, RESTAURANTE_ID) VALUES ('Carlos Entregador', 'carlos.entregador@delivery.com', '$2a$10$DnUtdBLQhp7osUMrSZeTwuey5kZFXiLUTm/IJ9SYyfgJRT64GWWJq', 'ENTREGADOR', true, NULL);

-- Clientes (com ID para manter a referência nos pedidos)
INSERT INTO CLIENTE (ID, NOME, EMAIL, TELEFONE, ENDERECO, ATIVO) VALUES (4, 'Ana Cliente', 'ana.cliente@email.com', '11999998888', 'Rua das Flores, 123, Apto 45', true);

-- Restaurantes (com ID para manter a referência nos produtos e pedidos)
INSERT INTO RESTAURANTE (ID, NOME, CATEGORIA, ENDERECO, TELEFONE, TAXA_ENTREGA, TEMPO_ENTREGA, HORARIO_FUNCIONAMENTO, ATIVO) VALUES (1, 'Pizzaria Italiana Deliciosa', 'Italiana', 'Rua da Pizza, 10', '11987654321', 5.00, 45, '18:00-23:00', true);
INSERT INTO RESTAURANTE (ID, NOME, CATEGORIA, ENDERECO, TELEFONE, TAXA_ENTREGA, TEMPO_ENTREGA, HORARIO_FUNCIONAMENTO, ATIVO) VALUES (2, 'Cantina da Nona', 'Italiana', 'Av. Massa, 20', '11912345678', 7.50, 50, '19:00-00:00', true);
INSERT INTO RESTAURANTE (ID, NOME, CATEGORIA, ENDERECO, TELEFONE, TAXA_ENTREGA, TEMPO_ENTREGA, HORARIO_FUNCIONAMENTO, ATIVO) VALUES (3, 'Sushi House', 'Japonesa', 'Travessa do Peixe, 30', '11955554444', 12.00, 60, '12:00-22:00', false);

-- Produtos
INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (1, 'Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 45.00, 'Pizza Salgada', true, 1);
INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (2, 'Refrigerante 2L', 'Coca-Cola, Guaraná ou Fanta', 12.50, 'Bebidas', true, 1);
INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (3, 'Pizza Calabresa', 'Molho, calabresa e cebola', 48.00, 'Pizza Salgada', false, 1);
INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (4, 'Lasanha Bolonhesa', 'Massa artesanal com molho à bolonhesa', 55.00, 'Massas', true, 2);

-- Pedidos
-- Pedido 1 (Pendente da cliente Ana na Pizzaria)
INSERT INTO PEDIDO (ID, NUMERO_PEDIDO, DATA_PEDIDO, ENDERECO_ENTREGA, SUBTOTAL, TAXA_ENTREGA, VALOR_TOTAL, OBSERVACOES, STATUS, CLIENTE_ID, RESTAURANTE_ID) VALUES (1, 'PED-001', '2025-09-04T20:30:00', 'Rua das Flores, 123, Apto 45', 57.50, 5.00, 62.50, 'Caprichar na mussarela!', 'PENDENTE', 4, 1);

-- Itens do Pedido 1
INSERT INTO ITEM_PEDIDO (ID, QUANTIDADE, PRECO_UNITARIO, SUBTOTAL, PEDIDO_ID, PRODUTO_ID) VALUES (1, 1, 45.00, 45.00, 1, 1); -- 1 Pizza Margherita
INSERT INTO ITEM_PEDIDO (ID, QUANTIDADE, PRECO_UNITARIO, SUBTOTAL, PEDIDO_ID, PRODUTO_ID) VALUES (2, 1, 12.50, 12.50, 1, 2); -- 1 Refrigerante

-- Pedido 2 (Entregue da cliente Ana na Cantina da Nona)
INSERT INTO PEDIDO (ID, NUMERO_PEDIDO, DATA_PEDIDO, ENDERECO_ENTREGA, SUBTOTAL, TAXA_ENTREGA, VALOR_TOTAL, OBSERVACOES, STATUS, CLIENTE_ID, RESTAURANTE_ID) VALUES (2, 'PED-002', '2025-09-03T19:00:00', 'Rua das Flores, 123, Apto 45', 55.00, 7.50, 62.50, NULL, 'ENTREGUE', 4, 2);

-- Itens do Pedido 2
INSERT INTO ITEM_PEDIDO (ID, QUANTIDADE, PRECO_UNITARIO, SUBTOTAL, PEDIDO_ID, PRODUTO_ID) VALUES (3, 1, 55.00, 55.00, 2, 4); -- 1 Lasanha