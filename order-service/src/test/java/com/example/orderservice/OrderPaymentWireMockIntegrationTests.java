package com.example.orderservice;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "resilience4j.retry.instances.paymentClient.maxAttempts=1",
                "resilience4j.ratelimiter.instances.paymentClient.limitForPeriod=100",
                "resilience4j.ratelimiter.instances.paymentClient.limitRefreshPeriod=1s",
                "resilience4j.bulkhead.instances.paymentClient.maxConcurrentCalls=100"
        }
)
class OrderPaymentWireMockIntegrationTests {

    private static final WireMockServer PAYMENT_SERVICE = new WireMockServer(wireMockConfig()
            .dynamicPort()
            .usingFilesUnderClasspath("wiremock"));

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void startPaymentService() {
        PAYMENT_SERVICE.start();
        WireMock.configureFor("localhost", PAYMENT_SERVICE.port());
    }

    @AfterAll
    static void stopPaymentService() {
        PAYMENT_SERVICE.stop();
    }

    @DynamicPropertySource
    static void paymentServiceProperties(DynamicPropertyRegistry registry) {
        registry.add("integration.payment.url", PAYMENT_SERVICE::baseUrl);
    }

    @BeforeEach
    void resetState() {
        PAYMENT_SERVICE.resetAll();
    }

    @Test
    void requestPaymentSendsExpectedRequestAndUpdatesOrderStatus() throws Exception {
        Long orderId = createOrder();
        PAYMENT_SERVICE.stubFor(post(urlEqualTo("/api/payments"))
                .withHeader("Idempotency-Key", equalTo("payment-order-" + orderId))
                .withRequestBody(equalToJson("""
                        {
                          "orderId": %d,
                          "amount": 45000.00,
                          "paymentMethod": "CARD"
                        }
                        """.formatted(orderId)))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("payment-created-response.json")
                        .withTransformers("response-template")));

        ResponseEntity<String> response = requestPayment(orderId, "CARD");
        ResponseEntity<String> orderResponse = restTemplate.getForEntity("/api/orders/{id}", String.class, orderId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"id\":101");
        assertThat(orderResponse.getBody()).contains("\"status\":\"PAYMENT_REQUESTED\"");
        PAYMENT_SERVICE.verify(1, postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("Idempotency-Key", equalTo("payment-order-" + orderId)));
    }

    @Test
    void requestPaymentUsesStableIdempotencyKeyForSameOrder() throws Exception {
        Long orderId = createOrder();
        PAYMENT_SERVICE.stubFor(post(urlEqualTo("/api/payments"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("payment-created-response.json")
                        .withTransformers("response-template")));

        ResponseEntity<String> firstResponse = requestPayment(orderId, "CARD");
        ResponseEntity<String> secondResponse = requestPayment(orderId, "CARD");

        assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        PAYMENT_SERVICE.verify(2, postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("Idempotency-Key", equalTo("payment-order-" + orderId)));
    }

    @Test
    void requestPaymentReturnsNotFoundAndDoesNotCallPaymentServiceWhenOrderDoesNotExist() {
        ResponseEntity<String> response = requestPayment(999L, "CARD");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        PAYMENT_SERVICE.verify(0, postRequestedFor(urlEqualTo("/api/payments")));
    }

    @Test
    void requestPaymentPropagatesPaymentServiceFailure() throws Exception {
        Long orderId = createOrder();
        PAYMENT_SERVICE.stubFor(post(urlEqualTo("/api/payments"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("payment-error-response.json")));

        ResponseEntity<String> response = requestPayment(orderId, "CARD");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        PAYMENT_SERVICE.verify(1, postRequestedFor(urlEqualTo("/api/payments")));
    }

    private Long createOrder() throws Exception {
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/orders",
                Map.of(
                        "customerName", "Ivan Petrenko",
                        "productName", "Laptop",
                        "quantity", 1,
                        "totalPrice", new BigDecimal("45000.00"),
                        "status", "CREATED"
                ),
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode responseBody = objectMapper.readTree(response.getBody());
        return responseBody.get("id").asLong();
    }

    private ResponseEntity<String> requestPayment(Long orderId, String paymentMethod) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return restTemplate.postForEntity(
                "/api/orders/{id}/payment",
                new HttpEntity<>("""
                        {
                          "paymentMethod": "%s"
                        }
                        """.formatted(paymentMethod), headers),
                String.class,
                orderId
        );
    }

}
