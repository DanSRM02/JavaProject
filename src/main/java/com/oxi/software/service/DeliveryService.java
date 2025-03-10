package com.oxi.software.service;

import com.oxi.software.entity.Delivery;
import com.oxi.software.repository.DeliveryRepository;
import com.oxi.software.repository.projection.DeliveryBasicProjection;
import com.oxi.software.service.dao.Idao;
import com.oxi.software.utilities.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService implements Idao<Delivery, Long> {
    
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Override
    public Delivery findBy(Long id) {
        return this.deliveryRepository.findById(id).orElseThrow(()->
                new CustomException("Delivery with id " + id + " not found", HttpStatus.NO_CONTENT));
    }

    @Override
    public Delivery save(Delivery obje) {
        this.deliveryRepository.save(obje);
        return obje;
    }

    @Override
    public void saveAll(Iterable<Delivery> obje) {
        this.deliveryRepository.saveAll(obje);
    }

    @Override
    public void delete(Delivery obje) {
        this.deliveryRepository.delete(obje);
    }

    @Override
    public List<Delivery> findAll() {
        return this.deliveryRepository.findAll();
    }

    public List<DeliveryBasicProjection> findDeliveryDetails(Long deliveryId) {
        return this.deliveryRepository.findDeliveriesByDomiciliary(deliveryId);
        }


}
