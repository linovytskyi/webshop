package com.example.orderservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Service API",
                version = "1.0.0",
                description = "CRUD operations for orders and payment request integration.",
                contact = @Contact(name = "Webshop Team")
        ),
        servers = {
                @Server(url = "http://localhost:8081", description = "Local order-service")
        }
)
public class OpenApiConfig {
}
