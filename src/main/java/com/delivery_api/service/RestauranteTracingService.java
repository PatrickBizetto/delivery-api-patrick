package com.delivery_api.service;

import com.delivery_api.dto.RestauranteDTO;
import com.delivery_api.dto.RestauranteResponseDTO;

public interface RestauranteTracingService {

    /**
     * Executa o fluxo completo de cadastrar um novo restaurante e depois buscá-lo,
     * com cada etapa sendo rastreada por spans do Sleuth/Brave.
     *
     * @param dto Os dados do restaurante a ser cadastrado.
     * @return O DTO do restaurante que foi buscado após o cadastro.
     */
    RestauranteResponseDTO cadastrarEBuscarComTracing(RestauranteDTO dto);

}