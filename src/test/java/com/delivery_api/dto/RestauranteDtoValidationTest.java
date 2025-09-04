package com.delivery_api.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Testes de Validação para RestauranteDTO")
public class RestauranteDtoValidationTest {

    private static Validator validator;

    // Configura o validador uma única vez para todos os testes da classe
    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    // Teste "Caminho Feliz": Garante que um objeto válido passa sem erros.
    @Test
    @DisplayName("Deve ser válido quando todos os campos estão corretos")
    void devePassarQuandoDtoEstaCorreto() {
        RestauranteDTO dto = createValidDto();
        Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    // Usando @Nested para agrupar testes relacionados ao mesmo campo
    @Nested
    @DisplayName("Validações do Campo 'nome'")
    class NomeValidationTests {

        @Test
        @DisplayName("Deve falhar se nome for nulo")
        void deveFalharSeNomeForNulo() {
            RestauranteDTO dto = createValidDto();
            dto.setNome(null); // Inválido
            
            Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Nome é obrigatório");
        }

        @Test
        @DisplayName("Deve falhar se nome for muito curto (1 caractere)")
        void deveFalharSeNomeForCurto() {
            RestauranteDTO dto = createValidDto();
            dto.setNome("A"); // Inválido
            
            Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Nome deve ter entre 2 e 100 caracteres");
        }
    }

    @Nested
    @DisplayName("Validações do Campo 'telefone'")
    class TelefoneValidationTests {

        // Teste parametrizado: roda uma vez para cada string no @ValueSource
        @ParameterizedTest
        @ValueSource(strings = {"12345", "123456789012", "abcdefghij", ""})
        @DisplayName("Deve falhar para formatos de telefone inválidos")
        void deveFalharSeTelefoneInvalido(String telefoneInvalido) {
            RestauranteDTO dto = createValidDto();
            dto.setTelefone(telefoneInvalido); // Inválido
            
            Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
            
            assertThat(violations).isNotEmpty();
            // Aqui, a mensagem pode variar dependendo da anotação, então apenas checamos se houve erro
        }
    }
    
    @Nested
    @DisplayName("Validações do Campo 'taxaEntrega'")
    class TaxaEntregaValidationTests {

        @Test
        @DisplayName("Deve falhar se taxa de entrega for zero")
        void deveFalharSeTaxaEntregaForZero() {
            RestauranteDTO dto = createValidDto();
            dto.setTaxaEntrega(BigDecimal.ZERO); // Inválido (inclusive = false)
            
            Set<ConstraintViolation<RestauranteDTO>> violations = validator.validate(dto);
            
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Taxa de entrega deve ser positiva");
        }
    }

    // Método auxiliar para criar um DTO válido rapidamente
    private RestauranteDTO createValidDto() {
        RestauranteDTO dto = new RestauranteDTO();
        dto.setNome("Restaurante Modelo");
        dto.setCategoria("BRASILEIRA");
        dto.setTelefone("11987654321");
        dto.setTaxaEntrega(BigDecimal.valueOf(10.0));
        dto.setTempoEntrega(50);
        dto.setEndereco("Rua Válida, 123");
        dto.setHorarioFuncionamento("08:00-16:00");
        dto.setEmail("contato@modelo.com");
        return dto;
    }
}