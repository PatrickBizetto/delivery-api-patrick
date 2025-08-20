package com.delivery_api.projection;

import java.math.BigDecimal;

// Interface de Projeção para relatórios
public interface RelatorioVendas {
    String getNomeRestaurante();
    BigDecimal getTotalVendas();
    Long getQuantidadePedidos();
}
