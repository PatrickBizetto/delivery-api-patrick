package com.delivery_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Configuração centralizada para o Spring Cache com Redis.
 *
 * Esta classe estabelece o comportamento padrão para todos os caches da aplicação,
 * garantindo consistência e as melhores práticas de serialização.
 */
@Configuration
public class CacheConfig {

    /**
     * Define a configuração padrão para todos os caches gerenciados pelo Spring.
     * Ao criar este Bean, o Spring Boot o utilizará automaticamente para o RedisCacheManager.
     *
     * @return A configuração de cache a ser usada como padrão.
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()

                // Define um tempo de vida (TTL) padrão para as entradas de cache.
                // Após 10 minutos de inatividade, a entrada expira e será buscada do banco novamente.
                .entryTtl(Duration.ofMinutes(10))

                // Desabilita o cache de valores nulos. Isso evita que uma busca por um
                // dado inexistente seja cacheada, garantindo que a aplicação tente buscar
                // novamente caso o dado seja criado.
                .disableCachingNullValues()

                // Define o serializador para as CHAVES do cache.
                // Usar StringRedisSerializer torna as chaves legíveis no Redis.
                // Ex: "clientes::1" em vez de um código binário.
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))

                // Define o serializador para os VALORES do cache.
                // Usar GenericJackson2JsonRedisSerializer resolve o problema com LocalDateTime
                // e armazena os dados em formato JSON, que é legível e interoperável.
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }
}