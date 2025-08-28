package com.delivery_api.config;

import com.delivery_api.model.*;
import com.delivery_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private RestauranteRepository restauranteRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO CARGA DE DADOS DE TESTE ===");

        // Limpar dados existentes (a ordem é importante por causa das chaves estrangeiras)
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        restauranteRepository.deleteAll();
        clienteRepository.deleteAll();

        inserirClientes();
        inserirRestaurantes();
        inserirProdutos();
        // inserirPedidos();

        testarConsultas();

        System.out.println("\n=== CARGA DE DADOS CONCLUÍDA ===");
    }

    private void inserirClientes() {
        System.out.println("\n--- Inserindo Clientes ---");

        Cliente cliente1 = new Cliente("Cliente Teste", "cliente@teste.com", null, null, true);
        Cliente cliente2 = new Cliente("João Silva", "joao@email.com", "11999999999", "Rua A, 123", true);
        Cliente cliente3 = new Cliente("Maria Santos", "maria@email.com", "11888888888", "Rua B, 456", true);
        Cliente cliente4 = new Cliente("Pedro Oliveira", "pedro@email.com", "11777777777", "Rua C, 789", false);

        clienteRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3, cliente4));
        System.out.println("✓ 4 clientes inseridos");
    }

    private void inserirRestaurantes() {
        System.out.println("\n--- Inserindo Restaurantes ---");

        Restaurante r1 = new Restaurante(
                "Pizzaria Italiana Deliciosa", "Italiana",
                "Rua da Pizza, 10", "11987654321",
                new BigDecimal("5.00"), true,
                45, "18:00-23:00"
        );

        Restaurante r2 = new Restaurante(
                "Cantina da Nona", "Italiana",
                "Av. Massa, 20", "11912345678",
                new BigDecimal("7.50"), true,
                50, "19:00-00:00"
        );

        Restaurante r3 = new Restaurante(
                "Sushi House", "Japonesa",
                "Travessa do Peixe, 30", "11955554444",
                new BigDecimal("12.00"), false,
                60, "12:00-22:00"
        );

        restauranteRepository.saveAll(Arrays.asList(r1, r2, r3));
        System.out.println("✓ 3 restaurantes inseridos");
    }

    private void inserirProdutos() {
        System.out.println("\n--- Inserindo Produtos ---");

        // Buscar restaurante 1 (Pizzaria Italiana Deliciosa)
        Restaurante r1 = restauranteRepository.findByNome("Pizzaria Italiana Deliciosa")
                .stream().findFirst().orElseThrow();

        Produto p1 = new Produto(null, "Pizza Margherita",
                "Molho de tomate, mussarela e manjericão",
                new BigDecimal("45.00"), "Pizza Salgada", true, r1, null);

        Produto p2 = new Produto(null, "Refrigerante 2L",
                "Coca-Cola, Guaraná ou Fanta",
                new BigDecimal("12.50"), "Bebidas", true, r1, null);

        Produto p3 = new Produto(null, "Pizza Calabresa",
                "Molho, calabresa e cebola",
                new BigDecimal("48.00"), "Pizza Salgada", false, r1, null);

        produtoRepository.saveAll(Arrays.asList(p1, p2, p3));
        System.out.println("✓ 3 produtos inseridos");
    }

    private void testarConsultas() {
        System.out.println("\n=== TESTANDO CONSULTAS DOS REPOSITORIES ===");

        // Teste ClienteRepository
        var clientePorEmail = clienteRepository.findByEmail("joao@email.com");
        System.out.println("Cliente por email (joao@email.com): "
                + (clientePorEmail.isPresent() ? clientePorEmail.get().getNome() : "Não encontrado"));

        var clientesAtivos = clienteRepository.findByAtivoTrue();
        System.out.println("Clientes ativos encontrados: " + clientesAtivos.size());

        var clientesPorNome = clienteRepository.findByNomeContainingIgnoreCase("silva");
        System.out.println("Clientes com 'silva' no nome: " + clientesPorNome.size());

        boolean emailExiste = clienteRepository.existsByEmail("maria@email.com");
        System.out.println("Email maria@email.com existe? " + emailExiste);

        // Teste RestauranteRepository
        var restaurantesItalianos = restauranteRepository.findByCategoria("Italiana");
        System.out.println("Restaurantes Italianos: " + restaurantesItalianos.size());
    }
}
