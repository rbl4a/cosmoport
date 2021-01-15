package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/rest/ships")
public class ShipRestController {
    @Autowired
    private ShipService shipService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShipById(@PathVariable Long id) {
        return new ResponseEntity<>(shipService.getById(id), HttpStatus.OK);
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getShips(@RequestParam(value = "name", required = false) String name,
                                               @RequestParam(value = "planet", required = false) String planet,
                                               @RequestParam(value = "shipType", required = false) ShipType shipType,
                                               @RequestParam(value = "after", required = false) Long after,
                                               @RequestParam(value = "before", required = false) Long before,
                                               @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                               @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                               @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                               @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                               @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                               @RequestParam(value = "minRating", required = false) Double minRating,
                                               @RequestParam(value = "maxRating", required = false) Double maxRating,
                                               @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                               @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
                                               @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order)
    {
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by(Sort.Direction.ASC, order.getFieldName()));
        List<Ship> ships = shipService.getAllShips(Specification.where(shipService.filterByName(name)), pageable).getContent();
        return new ResponseEntity<>(ships, HttpStatus.OK);
    }



    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Long> getCount(@RequestParam(value = "name", required = false) String name) {
        return new ResponseEntity<>(shipService.shipCount(Specification.where(shipService.filterByName(name))), HttpStatus.OK);
    }

}
