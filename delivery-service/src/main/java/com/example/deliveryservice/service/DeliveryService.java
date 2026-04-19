package com.example.deliveryservice.service;

import com.example.deliveryservice.model.Delivery;
import com.example.deliveryservice.model.DeliveryStatus;
import com.example.deliveryservice.repository.DeliveryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public List<Delivery> findAll() {
        log.info("Fetching all deliveries");
        return deliveryRepository.findAll();
    }

    public Delivery findById(Long id) {
        log.info("Fetching delivery by id={}", id);
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + id + " not found"));
    }

    public Delivery create(Delivery delivery) {
        log.info("Creating new delivery for orderId={}", delivery.getOrderId());
        delivery.setId(null);
        if (delivery.getStatus() == null) {
            delivery.setStatus(DeliveryStatus.NEW);
        }
        Delivery savedDelivery = deliveryRepository.save(delivery);
        log.info("Delivery created with id={} and status={}", savedDelivery.getId(), savedDelivery.getStatus());
        return savedDelivery;
    }

    public Delivery update(Long id, Delivery updatedDelivery) {
        log.info("Updating delivery id={}", id);
        Delivery existingDelivery = findById(id);
        existingDelivery.setOrderId(updatedDelivery.getOrderId());
        existingDelivery.setAddress(updatedDelivery.getAddress());
        existingDelivery.setDeliveryDate(updatedDelivery.getDeliveryDate());
        existingDelivery.setStatus(updatedDelivery.getStatus());
        Delivery savedDelivery = deliveryRepository.save(existingDelivery);
        log.info("Delivery id={} updated with status={}", savedDelivery.getId(), savedDelivery.getStatus());
        return savedDelivery;
    }

    public void delete(Long id) {
        log.info("Deleting delivery id={}", id);
        Delivery delivery = findById(id);
        deliveryRepository.delete(delivery);
        log.info("Delivery id={} deleted", id);
    }
}
