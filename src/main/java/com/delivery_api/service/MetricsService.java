package com.delivery_api.service;

import io.micrometer.core.instrument.Timer;

/**
 * Interface que define o contrato para o serviço de métricas da aplicação.
 * Abstrai a lógica de registro de métricas de negócio e performance.
 */
public interface MetricsService {

    /**
     * Incrementa o contador de pedidos totais processados.
     */
    void incrementarPedidosProcessados();

    /**
     * Incrementa o contador de pedidos finalizados com sucesso.
     */
    void incrementarPedidosComSucesso();

    /**
     * Incrementa o contador de pedidos que resultaram em erro.
     */
    void incrementarPedidosComErro();

    /**
     * Inicia a cronometragem de uma operação.
     *
     * @return um {@link Timer.Sample} para ser usado ao finalizar a cronometragem.
     */
    Timer.Sample iniciarTimerPedido();

    /**
     * Finaliza a cronometragem de uma operação de pedido.
     *
     * @param sample o {@link Timer.Sample} retornado pelo método iniciarTimerPedido.
     */
    void finalizarTimerPedido(Timer.Sample sample);

    /**
     * Define o valor atual do medidor (gauge) de usuários ativos.
     *
     * @param quantidade o número de usuários ativos no momento.
     */
    void setUsuariosAtivos(int quantidade);
}