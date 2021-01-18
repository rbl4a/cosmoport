package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface ShipService {

    Page<Ship> getAllShips(Specification<Ship> specification, Pageable pageable);
    Long shipCount(Specification<Ship> specification);
    Ship getById(Long id);
    void createShip(Ship ship);
    void deleteShip(Long id);
    Ship updateShip(Long id, Ship ship);
    Specification<Ship> filterByName(String name);
    Specification<Ship> filterByPlanet(String planet);
    Specification<Ship> filterByDate(Long after, Long before);
    Specification<Ship> filterByShipType(ShipType shipType);
    Specification<Ship> filterByIsUsed(Boolean isUsed);
    Specification<Ship> filterByCrew(Integer minCrew, Integer maxCrew);
    Specification<Ship> filterByRating(Double minRating, Double maxRating);
    Specification<Ship> filterBySpeed(Double min, Double max);


}
