# Delivery Tech API

## 📝 Descrição
Este projeto é uma API RESTful para um sistema de delivery, desenvolvido como parte do curso qualificaSP - Arquitetura de Software com SpringBoot. A API permite o gerenciamento completo de clientes, restaurantes, produtos e o fluxo de criação de pedidos.

## ✨ Funcionalidades
✅ Gerenciamento de Clientes: CRUD completo (Criar, Ler, Atualizar, Inativar).

✅ Gerenciamento de Restaurantes: CRUD completo (Criar, Ler, Atualizar, Inativar).

✅ Gerenciamento de Produtos: CRUD completo, com associação a um restaurante.

✅ Fluxo de Pedidos:

    - Criação de um novo pedido.

    - Adição de itens a um pedido existente.

    - Confirmação de pedido com cálculo de totais.

    - Listagem de pedidos por cliente.

✅ Validação de Dados: Garante a integridade dos dados na entrada da API.

✅ Banco de Dados em Memória: Utiliza H2 para facilitar o desenvolvimento e os testes

## 🚀 Tecnologias
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3
- **Data:** Spring Data JPA / Hibernate
- **Banco de Dados:** H2 In-Memory Database
- **Build Tool:** Maven
- **Validação:** Jakarta Bean Validation
- **Utilitários:** Lombok

## 🏃‍♂️ Como executar
### Pré-requisitos
Antes de começar, você vai precisar ter instalado em sua máquina:
- [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) - Versão 21 ou superior.
- [Maven](https://maven.apache.org/download.cgi) - Versão 3.8 ou superior.

### Passo a Passo
1. **Clone o repositório:**
   `git clone [URL_DO_SEU_REPOSITORIO_AQUI]`

2. **Navegue até a pasta do projeto:**
   `git clone [URL_DO_SEU_REPOSITORIO_AQUI]`

3. **Execute a aplicação com o Maven:**
   `mvn spring-boot:run`

4. **Acesse a API:**
- O servidor estará rodando em http://localhost:8080.
- Você pode testar os endpoints usando o Postman ou outro cliente HTTP.

5. **Acesse o Console do H2 (Opcional):**
- Para visualizar o banco de dados em memória, acesse http://localhost:8080/h2-console no seu navegador.
- Use as seguintes credenciais para logar:
    - JDBC URL: jdbc:h2:mem:deliverydb
    - User Name: sa
    - Password: (deixe em branco)

## 📖 Documentação da API

Aqui estão os principais endpoints disponíveis.

### Clientes (`/clientes`)
| Método HTTP | Endpoint | Descrição | Exemplo de Corpo (Body) |
| :--- | :--- | :--- | :--- |
| `GET` | `/clientes` | Lista todos os clientes ativos. | N/A |
| `GET` | `/clientes/{id}` | Busca um cliente por ID. | N/A |
| `POST` | `/clientes` | Cadastra um novo cliente. | `{"nome": "Novo Cliente", "email": "novo@email.com", ...}` |
| `PUT` | `/clientes/{id}` | Atualiza os dados de um cliente. | `{"nome": "Cliente Atualizado", "email": "att@email.com", ...}` |
| `DELETE`| `/clientes/{id}` | Inativa um cliente (soft delete). | N/A |

### Restaurantes (`/restaurantes`)
| Método HTTP | Endpoint | Descrição | Exemplo de Corpo (Body) |
| :--- | :--- | :--- | :--- |
| `GET` | `/restaurantes` | Lista todos os restaurantes ativos. | N/A |
| `GET` | `/restaurantes/{id}` | Busca um restaurante por ID. | N/A |
| `POST` | `/restaurantes` | Cadastra um novo restaurante. | `{"nome": "Nova Pizzaria", "categoria": "Italiana", ...}` |
| `PUT` | `/restaurantes/{id}` | Atualiza os dados de um restaurante. | `{"nome": "Pizzaria Editada", "taxaEntrega": 7.50, ...}` |
| `DELETE`| `/restaurantes/{id}` | Inativa um restaurante (soft delete). | N/A |

### Produtos (`/produtos`)
| Método HTTP | Endpoint | Descrição | Exemplo de Corpo (Body) |
| :--- | :--- | :--- | :--- |
| `GET` | `/produtos/restaurante/{id}` | Lista os produtos de um restaurante. | N/A |
| `GET` | `/produtos/{id}` | Busca um produto por ID. | N/A |
| `POST` | `/produtos?restauranteId={id}` | Cadastra um novo produto. | `{"nome": "Refrigerante", "preco": 8.00, ...}` |
| `PUT` | `/produtos/{id}` | Atualiza os dados de um produto. | `{"nome": "Refrigerante Diet", "preco": 8.50, ...}` |
| `DELETE`| `/produtos/{id}` | Torna um produto indisponível. | N/A |

### Pedidos (`/pedidos`)
| Método HTTP | Endpoint | Descrição |
| :--- | :--- | :--- |
| `GET` | `/pedidos/cliente/{id}` | Lista todos os pedidos de um cliente. |
| `POST`| `/pedidos?clienteId={id}&restauranteId={id}` | Cria um novo pedido (vazio). |
| `POST`| `/pedidos/{id}/itens?produtoId={id}&quantidade={qtd}` | Adiciona um item a um pedido. |
| `PUT` | `/pedidos/{id}/confirmar` | Confirma um pedido, calculando os totais. |

---
## 🏗️ Estrutura do Projeto
O projeto está organizado em uma arquitetura de camadas para separação de responsabilidades:

- **`com.delivery_api.controller`**: Camada de API REST, responsável por receber requisições HTTP.
- **`com.delivery_api.service`**: Camada de serviço, onde reside a lógica de negócio.
- **`com.delivery_api.repository`**: Camada de acesso a dados (Data Access Layer), usando Spring Data JPA.
- **`com.delivery_api.model`**: As entidades JPA que modelam o banco de dados.
- **`com.delivery_api.enums`**: Enumerações usadas no projeto, como `StatusPedido`.

## 👨‍💻 Desenvolvedor
Patrick B. Cruz - TI 56 - Arquitetura de Sistemas  
Desenvolvido com JDK 21 e Spring Boot 3.5.5
