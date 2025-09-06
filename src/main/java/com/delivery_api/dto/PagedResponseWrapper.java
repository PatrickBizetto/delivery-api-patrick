package com.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Schema(description = "Wrapper padrão para respostas que retornam listas paginadas de dados.")
public class PagedResponseWrapper<T> {

    @Schema(description = "A lista de itens contidos na página atual.")
    private List<T> content;

    @Schema(description = "Um objeto contendo todas as informações sobre a paginação.")
    private PageInfo page;

    @Schema(description = "Um objeto contendo links úteis para navegar entre as páginas.")
    private PageLinks links;

    public PagedResponseWrapper(Page<T> page) {
        this.content = page.getContent();
        this.page = new PageInfo(page);
        this.links = new PageLinks(page);
    }
    
    // As classes internas (inner classes) são usadas para organizar a resposta
    
    @Getter
    @Schema(description = "Detalhes sobre o estado da paginação.")
    public static class PageInfo {
        @Schema(description = "O número da página atual, começando em 0.", example = "0")
        private final int number;
        
        @Schema(description = "O número de itens por página.", example = "10")
        private final int size;
        
        @Schema(description = "O número total de itens em todas as páginas.", example = "42")
        private final long totalElements;
        
        @Schema(description = "O número total de páginas disponíveis.", example = "5")
        private final int totalPages;
        
        @Schema(description = "Indica se esta é a primeira página.", example = "true")
        private final boolean first;
        
        @Schema(description = "Indica se esta é a última página.", example = "false")
        private final boolean last;

        public PageInfo(Page<?> page) {
            this.number = page.getNumber();
            this.size = page.getSize();
            this.totalElements = page.getTotalElements();
            this.totalPages = page.getTotalPages();
            this.first = page.isFirst();
            this.last = page.isLast();
        }
    }

    @Getter
    @Schema(description = "Links de navegação para a primeira, última, próxima e página anterior.")
    public static class PageLinks {
        @Schema(description = "URL para a primeira página de resultados.", example = "/api/restaurantes?page=0&size=10")
        private String first;
        
        @Schema(description = "URL para a última página de resultados.", example = "/api/restaurantes?page=4&size=10")
        private String last;
        
        @Schema(description = "URL para a próxima página de resultados (nulo se for a última).", example = "/api/restaurantes?page=1&size=10")
        private String next;
        
        @Schema(description = "URL para a página anterior de resultados (nulo se for a primeira).", example = "null")
        private String prev;

        public PageLinks(Page<?> page) {
            // Lógica para gerar os links (pode ser melhorada com UriComponentsBuilder)
            // Para a documentação, o importante são os exemplos
            if (page.getTotalPages() > 1) {
                 this.first = "?page=0&size=" + page.getSize();
                 this.last = "?page=" + (page.getTotalPages() - 1) + "&size=" + page.getSize();
            }
            if (page.hasNext()) {
                this.next = "?page=" + (page.getNumber() + 1) + "&size=" + page.getSize();
            }
            if (page.hasPrevious()) {
                this.prev = "?page=" + (page.getNumber() - 1) + "&size=" + page.getSize();
            }
        }
    }
}