package com.example.deliveryservice.service;

import com.example.deliveryservice.model.Delivery;
import com.example.deliveryservice.model.DeliveryStatus;
import com.example.deliveryservice.repository.DeliveryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    public Delivery findById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery with id " + id + " not found"));
    }

    public Delivery create(Delivery delivery) {
        delivery.setId(null);
        if (delivery.getStatus() == null) {
            delivery.setStatus(DeliveryStatus.NEW);
        }
        return deliveryRepository.save(delivery);
    }

    public Delivery update(Long id, Delivery updatedDelivery) {
        Delivery existingDelivery = findById(id);
        existingDelivery.setOrderId(updatedDelivery.getOrderId());
        existingDelivery.setAddress(updatedDelivery.getAddress());
        existingDelivery.setDeliveryDate(updatedDelivery.getDeliveryDate());
        existingDelivery.setStatus(updatedDelivery.getStatus());
        return deliveryRepository.save(existingDelivery);
    }

    public void delete(Long id) {
        Delivery delivery = findById(id);
        deliveryRepository.delete(delivery);
    }
}
