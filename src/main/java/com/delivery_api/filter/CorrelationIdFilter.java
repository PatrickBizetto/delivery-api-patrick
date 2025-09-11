package com.delivery_api.filter;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.trim().isEmpty()) {
                correlationId = UUID.randomUUID().toString();
            }

            // Adiciona o ID ao MDC para ser usado nos logs
            MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
            
            // Adiciona o ID ao header da resposta para o cliente
            httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

            chain.doFilter(request, response);
        } finally {
            // Limpa o MDC para evitar vazamento de contexto entre threads
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}