package com.example.deliveryservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Delivery Service API",
                version = "1.0.0",
                description = "CRUD operations for deliveries.",
                contact = @Contact(name = "Webshop Team")
        ),
        servers = {
                @Server(url = "http://localhost:8083", description = "Local delivery-service")
        }
)
public class OpenApiConfig {
}
