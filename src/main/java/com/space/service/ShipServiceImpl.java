package com.space.service;

import com.space.exception.BadRequestIdException;
import com.space.exception.NotFoundIdException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class ShipServiceImpl implements ShipService {

    @Autowired
    ShipRepository shipRepository;


    @Override
    public Page<Ship> getAllShips(Specification<Ship> specification, Pageable pageable) {
        return shipRepository.findAll(specification, pageable);
    }
    @Override
    public Long shipCount(Specification<Ship> specification) {
        return shipRepository.count(specification);
    }

    @Override
    public Ship getById(Long id) {
        if (id <= 0) {
            throw new BadRequestIdException();
        }
        return shipRepository.findById(id).orElseThrow(() -> new NotFoundIdException(String.format("Ship with id %s not found", id)));
    }

    @Override
    public Specification<Ship> filterByName(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(root.get("name"), "%" + name + "%");
    }

    @Override
    public Specification<Ship> filterByPlanet(String planet) {
        return (root, query, cb) -> planet == null ? null : cb.like(root.get("planet"), "%" + planet + "%");
    }

    @Override
    public Specification<Ship> filterByDate(Long after, Long before) {
        return (root, query, cb) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
                return cb.lessThanOrEqualTo(root.get("prodDate"), new Date(before));
            }
            if (before == null) {
                return cb.greaterThanOrEqualTo(root.get("prodDate"), new Date(after));
            }
            return cb.between(root.get("prodDate"), new Date(after), new Date(before));
        };
    }

    @Override
    public Specification<Ship> filterByShipType(ShipType shipType) {
        return (root, query, cb) -> shipType == null ? null : cb.equal(root.get("shipType"), shipType);
    }

    @Override
    public Specification<Ship> filterByIsUsed(Boolean isUsed) {
        return (root, query, cb) -> {
            if (isUsed == null) {
                return null;
            }
            if (Boolean.TRUE.equals(isUsed)) {
                return cb.isTrue(root.get("isUsed"));
            }
            return cb.isFalse(root.get("isUsed"));
        };
    }

    @Override
    public Specification<Ship> filterByCrew(Integer minCrew, Integer maxCrew) {
        return (root, query, cb) -> {
            if (minCrew == null && maxCrew == null) {
                return null;
            }
            if (minCrew == null) {
                return cb.lessThanOrEqualTo(root.get("crewSize"), maxCrew);
            }
            if (maxCrew == null) {
                return cb.greaterThanOrEqualTo(root.get("crewSize"), minCrew);
            }
            return cb.between(root.get("crewSize"), minCrew, maxCrew);
        };
    }

    @Override
    public Specification<Ship> filterByRating(Double minRating, Double maxRating) {
        return (root, query, cb) -> {
            if (minRating == null && maxRating == null) {
                return null;
            }
            if (minRating == null) {
                return cb.lessThanOrEqualTo(root.get("rating"), maxRating);
            }
            if (maxRating == null) {
                return cb.greaterThanOrEqualTo(root.get("rating"), minRating);
            }
            return cb.between(root.get("rating"), minRating, maxRating);
        };
    }

    @Override
    public Specification<Ship> filterBySpeed(Double minSpeed, Double maxSpeed) {
        return (root, query, cb) -> {
            if (minSpeed == null && maxSpeed == null) {
                return null;
            }

            if (minSpeed == null) {
                return cb.lessThanOrEqualTo(root.get("speed"), maxSpeed);
            }

            if (maxSpeed == null) {
                return cb.greaterThanOrEqualTo(root.get("speed"), minSpeed);
            }

            return cb.between(root.get("speed"), minSpeed, maxSpeed);
        };
    }


}
