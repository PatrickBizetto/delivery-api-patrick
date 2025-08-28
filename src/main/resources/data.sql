-- data.sql (CORRIGIDO)

-- Adicionamos TELEFONE e ENDERECO com valores nulos para a inserção funcionar
INSERT INTO CLIENTE (ID, NOME, EMAIL, TELEFONE, ENDERECO, ATIVO) VALUES (1, 'Cliente Teste', 'cliente@teste.com', null, null, true);

-- O resto do seu arquivo continua igual...
INSERT INTO RESTAURANTE (ID, NOME, CATEGORIA, ENDERECO, TELEFONE, TAXA_ENTREGA, TEMPO_ENTREGA, HORARIO_FUNCIONAMENTO, ATIVO) VALUES (1, 'Pizzaria Italiana Deliciosa', 'Italiana', 'Rua da Pizza, 10', '11987654321', 5.00, 45, '18:00-23:00', true);
INSERT INTO RESTAURANTE (ID, NOME, CATEGORIA, ENDERECO, TELEFONE, TAXA_ENTREGA, TEMPO_ENTREGA, HORARIO_FUNCIONAMENTO, ATIVO) VALUES (2, 'Cantina da Nona', 'Italiana', 'Av. Massa, 20', '11912345678', 7.50, 50, '19:00-00:00', true);
INSERT INTO RESTAURANTE (ID, NOME, CATEGORIA, ENDERECO, TELEFONE, TAXA_ENTREGA, TEMPO_ENTREGA, HORARIO_FUNCIONAMENTO, ATIVO) VALUES (3, 'Sushi House', 'Japonesa', 'Travessa do Peixe, 30', '11955554444', 12.00, 60, '12:00-22:00', false);

INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (1, 'Pizza Margherita', 'Molho de tomate, mussarela e manjericão', 45.00, 'Pizza Salgada', true, 1);
INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (2, 'Refrigerante 2L', 'Coca-Cola, Guaraná ou Fanta', 12.50, 'Bebidas', true, 1);
INSERT INTO PRODUTO (ID, NOME, DESCRICAO, PRECO, CATEGORIA, DISPONIVEL, RESTAURANTE_ID) VALUES (3, 'Pizza Calabresa', 'Molho, calabresa e cebola', 48.00, 'Pizza Salgada', false, 1);