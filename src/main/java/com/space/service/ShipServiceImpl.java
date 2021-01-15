package com.space.service;

import com.space.exception.BadRequestIdException;
import com.space.exception.NotFoundIdException;
import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


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


}
