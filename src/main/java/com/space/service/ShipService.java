package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface ShipService {

    Page<Ship> getAllShips(Specification<Ship> specification, Pageable pageable);
    Long shipCount(Specification<Ship> specification);
    Ship getById(Long id);
    Specification<Ship> filterByName(String name);
}
