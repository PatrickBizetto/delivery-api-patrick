package com.delivery_api.service.impl;

import brave.Span;
import brave.Tracer;
import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;
import com.delivery_api.service.RestauranteService;
import com.delivery_api.service.RestauranteTracingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RestauranteTracingServiceImpl implements RestauranteTracingService {

    private static final Logger log = LoggerFactory.getLogger(RestauranteTracingServiceImpl.class);

    @Autowired
    private Tracer tracer; // Ferramenta para criar e gerenciar spans.

    @Autowired
    private RestauranteService restauranteService; // Seu serviço de negócio original.

    @Override
    public RestauranteResponseDTO cadastrarEBuscarComTracing(RestauranteDTO dto) {
        log.info("Iniciando fluxo de cadastro com tracing para o restaurante: {}", dto.getNome());

        // 1. Cria um "span" pai para agrupar todo o fluxo de negócio.
        Span parentSpan = tracer.nextSpan().name("fluxo-cadastro-e-busca-restaurante").start();

        // Garante que o span atual seja o que acabamos de criar.
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(parentSpan)) {
            parentSpan.tag("fluxo.negocio", "novo_restaurante");
            parentSpan.tag("restaurante.nome.entrada", dto.getNome());

            // 2. Chama o método de cadastro, que terá seu próprio span (filho).
            RestauranteResponseDTO restauranteCadastrado = chamarCadastroRestauranteComSpan(dto);

            // Adiciona informações ao span pai após o cadastro.
            parentSpan.tag("restaurante.id.cadastrado", restauranteCadastrado.getId().toString());
            parentSpan.annotate("Cadastro concluído, iniciando busca.");

            // 3. Chama o método de busca, que também terá seu span (filho).
            return chamarBuscaRestauranteComSpan(restauranteCadastrado.getId());

        } catch (Exception e) {
            log.error("Erro no fluxo de tracing: {}", e.getMessage());
            parentSpan.error(e); // Marca o span com erro.
            throw e; // Relança a exceção para não quebrar o fluxo de erro da aplicação.
        } finally {
            parentSpan.finish(); // Finaliza o span pai, calculando sua duração.
            log.info("Finalizado fluxo de cadastro com tracing.");
        }
    }

    /**
     * Envelopa a chamada ao método de cadastro real com um span filho.
     */
    private RestauranteResponseDTO chamarCadastroRestauranteComSpan(RestauranteDTO dto) {
        Span childSpan = tracer.nextSpan().name("service-cadastrar-restaurante").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(childSpan)) {
            childSpan.tag("camada", "service");
            childSpan.tag("operacao.crud", "create");

            // Executa a lógica de negócio real
            return restauranteService.cadastrarRestaurante(dto);
        } finally {
            childSpan.finish();
        }
    }

    /**
     * Envelopa a chamada ao método de busca real com um span filho.
     */
    private RestauranteResponseDTO chamarBuscaRestauranteComSpan(Long id) {
        Span childSpan = tracer.nextSpan().name("service-buscar-restaurante-por-id").start();
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(childSpan)) {
            childSpan.tag("camada", "service");
            childSpan.tag("operacao.crud", "read");
            childSpan.tag("restaurante.id", id.toString());

            // Executa a lógica de negócio real
            return restauranteService.buscarRestaurantePorId(id);
        } finally {
            childSpan.finish();
        }
    }
}