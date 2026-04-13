package com.example.deliveryservice.controller;

import com.example.deliveryservice.model.Delivery;
import com.example.deliveryservice.service.DeliveryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryService.findAll();
    }

    @GetMapping("/{id}")
    public Delivery getDeliveryById(@PathVariable Long id) {
        return deliveryService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery createDelivery(@RequestBody Delivery delivery) {
        return deliveryService.create(delivery);
    }

    @PutMapping("/{id}")
    public Delivery updateDelivery(@PathVariable Long id, @RequestBody Delivery delivery) {
        return deliveryService.update(id, delivery);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable Long id) {
        deliveryService.delete(id);
    }
}
