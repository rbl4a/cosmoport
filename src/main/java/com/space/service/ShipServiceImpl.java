package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("shipService")
public class ShipServiceImpl implements ShipService {

    @Autowired
    ShipRepository shipRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Ship> getAll() {
        return new ArrayList<>(shipRepository.findAll());
    }

    @Override
    public Long getCount() {
        return shipRepository.count();
    }

}
