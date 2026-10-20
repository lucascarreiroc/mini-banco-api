package com.lucascarreiro.minibanco.shared.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "banco.limites")
public record LimiteProperties(BigDecimal diario) {
}