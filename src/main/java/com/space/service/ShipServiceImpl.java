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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
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
    public void createShip(Ship ship) {
        checkLengthNameAndPlanet(ship.getName());
        checkLengthNameAndPlanet(ship.getPlanet());
        checkCrewSize(ship.getCrewSize());
        checkSpeed(ship.getSpeed());
        checkProdDate(ship.getProdDate());
        if (ship.getUsed() == null) {
            ship.setUsed(false);
        }
        ship.setRating(calculateRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate()));
        shipRepository.saveAndFlush(ship);
    }

    @Override
    public void deleteShip(Long id) {
        Ship ship = getById(id);
        shipRepository.delete(ship);
    }

    @Override
    public Ship updateShip(Long id, Ship newShip) {
        Ship baseShip = getById(id);

        if (newShip.getName() != null) {
            checkLengthNameAndPlanet(newShip.getName());
            baseShip.setName(newShip.getName());
        }

        if (newShip.getPlanet() != null) {
            checkLengthNameAndPlanet(newShip.getPlanet());
            baseShip.setPlanet(newShip.getPlanet());
        }

        if (newShip.getShipType() != null) {
            baseShip.setShipType(newShip.getShipType());
        }

        if (newShip.getProdDate() != null) {
            checkProdDate(newShip.getProdDate());
            baseShip.setProdDate(newShip.getProdDate());
        }

        if (newShip.getUsed() != null) {
            baseShip.setUsed(newShip.getUsed());
        }

        if (newShip.getSpeed() != null) {
            checkSpeed(newShip.getSpeed());
            baseShip.setSpeed(newShip.getSpeed());
        }

        if (newShip.getCrewSize() != null) {
            checkCrewSize(newShip.getCrewSize());
            baseShip.setCrewSize(newShip.getCrewSize());
        }
        baseShip.setRating(calculateRating(baseShip.getSpeed(), baseShip.getUsed(), baseShip.getProdDate()));
        return shipRepository.saveAndFlush(baseShip);
    }

    private Double calculateRating(Double speed, Boolean isUsed, Date prodDate) {
        double result = 80 * speed * (Boolean.TRUE.equals(isUsed) ? 0.5 : 1) / (3019 - convertCalendarToInt(prodDate) + 1);
        return  BigDecimal.valueOf(result).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private int convertCalendarToInt(Date prodDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(prodDate.getTime());
        return calendar.get(Calendar.YEAR);
    }

    private void checkProdDate(Date prodDate) {
        if (prodDate == null) {
            throw new BadRequestIdException();
        }
        if (convertCalendarToInt(prodDate) < 2800 || convertCalendarToInt(prodDate) > 3019) {
            throw new BadRequestIdException();
        }
    }

    private void checkSpeed(Double speed) {
        if (speed == null) {
            throw new BadRequestIdException("Speed should not been NULL");
        }
        double speedScale = BigDecimal.valueOf(speed).setScale(2, RoundingMode.HALF_UP).doubleValue();
        if (speedScale < 0.01 || speedScale > 0.99) {
            throw new BadRequestIdException("Speed has incorrect value (expected: 0,01...0,99)");
        }
    }

    private void checkCrewSize(Integer crewSize) {
        if (crewSize == null || crewSize < 1 || crewSize > 9999) {
            throw new BadRequestIdException("Crew size has incorrect value (expected: 1...9999)");
        }
    }

    private void checkLengthNameAndPlanet(String name) {
        if (name == null || name.length() > 50 || name.equals("")) {
            throw new BadRequestIdException("Length of name more than 50 characters");
        }
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
