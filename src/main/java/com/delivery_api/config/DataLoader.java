package com.delivery_api.config;

import com.delivery_api.model.*;
import com.delivery_api.model.Cliente;
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
        // inserirProdutos();
        // inserirPedidos();

        testarConsultas();

        System.out.println("\n=== CARGA DE DADOS CONCLUÍDA ===");
    }

    private void inserirClientes() {
        System.out.println("\n--- Inserindo Clientes ---");
        Cliente cliente1 = new Cliente("João Silva", "joao@email.com", "11999999999", "Rua A, 123", true);
        Cliente cliente2 = new Cliente("Maria Santos", "maria@email.com", "11888888888", "Rua B, 456", true);
        Cliente cliente3 = new Cliente("Pedro Oliveira", "pedro@email.com", "11777777777", "Rua C, 789", false);

        clienteRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3));
        System.out.println("✓ 3 clientes inseridos");
    }

    private void inserirRestaurantes() {
        System.out.println("\n--- Inserindo Restaurantes ---");
        Restaurante r1 = new Restaurante("Pizza Express", "Italiana", "Av. Principal, 100", "1133333333", new BigDecimal("3.50"), true);
        Restaurante r2 = new Restaurante("Burger King", "Fast Food", "Rua Central, 200", "1144444444", new BigDecimal("5.00"), true);

        restauranteRepository.saveAll(Arrays.asList(r1, r2));
        System.out.println("✓ 2 restaurantes inseridos");
    }

    private void testarConsultas() {
        System.out.println("\n=== TESTANDO CONSULTAS DOS REPOSITORIES ===");

        // Teste ClienteRepository
        System.out.println("\n--- Testes ClienteRepository ---");
        var clientePorEmail = clienteRepository.findByEmail("joao@email.com");
        System.out.println("Cliente por email (joao@email.com): " + (clientePorEmail.isPresent() ? clientePorEmail.get().getNome() : "Não encontrado"));

        var clientesAtivos = clienteRepository.findByAtivoTrue();
        System.out.println("Clientes ativos encontrados: " + clientesAtivos.size());

        var clientesPorNome = clienteRepository.findByNomeContainingIgnoreCase("silva");
        System.out.println("Clientes com 'silva' no nome: " + clientesPorNome.size());

        boolean emailExiste = clienteRepository.existsByEmail("maria@email.com");
        System.out.println("Email maria@email.com existe? " + emailExiste);

        System.out.println("\n--- Testes RestauranteRepository ---");
        var restaurantesItalianos = restauranteRepository.findByCategoria("Italiana");
        System.out.println("Restaurantes Italianos: " + restaurantesItalianos.size());
    }
}
