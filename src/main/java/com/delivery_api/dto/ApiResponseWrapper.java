package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Wrapper padrão para as respostas da API, encapsulando os dados, status e uma mensagem.")
public class ApiResponseWrapper<T> implements Serializable{

    @Schema(description = "Indica se a requisição foi processada com sucesso.", example = "true")
    private boolean success;

    @Schema(description = "Os dados de retorno da requisição. O tipo varia de acordo com o endpoint.")
    private T data;

    @Schema(description = "Uma mensagem informativa sobre o resultado da operação.", example = "Operação realizada com sucesso.")
    private String message;

    @Schema(description = "Data e hora em que a resposta foi gerada.", example = "2025-09-06T10:30:00")
    private LocalDateTime timestamp;

    public ApiResponseWrapper() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResponseWrapper(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

}