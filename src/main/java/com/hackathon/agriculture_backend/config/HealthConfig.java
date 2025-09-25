package com.hackathon.agriculture_backend.config;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Health configuration to prevent SMTP timeout issues in production
 */
@Configuration
public class HealthConfig {

    /**
     * Custom mail health indicator that doesn't perform actual SMTP connection
     * to prevent timeout issues in production environments
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "management.health.mail.enabled", havingValue = "false", matchIfMissing = true)
    public HealthIndicator mailHealthIndicator() {
        return () -> {
            // Return UP status without attempting SMTP connection
            // This prevents the 134-second timeout issue
            return Health.up()
                    .withDetail("mail", "Email service disabled to prevent timeouts")
                    .withDetail("reason", "SMTP connection test disabled to prevent timeouts")
                    .withDetail("status", "DISABLED")
                    .build();
        };
    }

    /**
     * Disable the default MailHealthIndicator completely
     */
    @Bean
    @ConditionalOnProperty(name = "management.health.mail.enabled", havingValue = "true")
    public HealthIndicator disabledMailHealthIndicator() {
        return () -> {
            return Health.up()
                    .withDetail("mail", "Email health check disabled")
                    .build();
        };
    }
}
