package com.example.demo.core.service;

import com.example.demo.core.model.StockModel;
import com.example.demo.core.repository.StockRepository;
import com.example.demo.dto.out.ExtendedShoe;
import com.example.demo.dto.out.Stock;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.*;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(StockService.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Stock getStock(String name) {
        var stockModel = stockRepository.findByName(name).orElse(null);
        return mapToStock(stockModel);
    }

    public Stock.State getState(String name) {
        return getStock(name).getState();
    }

    public Stock updateStock(String name, List<ExtendedShoe> shoes) {
        var stockModel = stockRepository.findByName(name).orElse(null);
        if (stockModel != null) {
            updateStockModel(shoes, stockModel);
            try {
                stockRepository.save(stockModel);
            } catch (ConstraintViolationException e) {
                LOGGER.error("Capacity of shoes exceeds the limit (0-30)", e);
                throw new IllegalArgumentException(e);
            }
        }

        return getStock(name);
    }

    private Stock mapToStock(StockModel stockModel) {
        if (stockModel != null) {
            try {
                var shoes = convertToShoeList(stockModel.getShoes());

                return Stock.builder()
                        .name(stockModel.getName())
                        .shoes(shoes)
                        .state(getState(shoes))
                        .build();
            } catch (JsonProcessingException e) {
                LOGGER.error("Json convert error", e);
            }
        }
        return null;
    }

    private List<ExtendedShoe> convertToShoeList(String shoesStr) throws JsonProcessingException {
        var shoes = objectMapper.readValue(shoesStr, ExtendedShoe[].class);
        return Arrays.asList(shoes);
    }

    private String convertToShoesJsonStr(List<ExtendedShoe> shoeList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(shoeList);
    }


    private int getCapacity(List<ExtendedShoe> shoes) {
        return shoes.stream()
                .map(ExtendedShoe::getQuantity)
                .reduce(0, Integer::sum);
    }

    private Stock.State getState(List<ExtendedShoe> shoes) {
        var count = getCapacity(shoes);
        if (count == 0) {
            return Stock.State.EMPTY;
        } else if (count == 30) {
            return Stock.State.FULL;
        }
        return Stock.State.SOME;
    }

    private void updateStockModel(List<ExtendedShoe> shoes, StockModel stockModel) {
        try {
            List<ExtendedShoe> dbShoes = convertToShoeList(stockModel.getShoes());
            for (ExtendedShoe shoe : shoes) {
                ExtendedShoe dbShoe = getShoeByfields(dbShoes, shoe.getColor(), shoe.getSize());
                if (dbShoe != null) {
                    var quantity = shoe.getQuantity();
                    if (quantity < 0) {
                        var message = "The minimum capacity of shoes is 0";
                        LOGGER.error(message);
                        throw new IllegalArgumentException(message);
                    }
                    dbShoe.setQuantity(quantity);
                }
            }

            var dbShoesStr = convertToShoesJsonStr(dbShoes);
            var capacity = getCapacity(dbShoes);

            stockModel.setShoes(dbShoesStr);
            stockModel.setCapacity(capacity);
        } catch (JsonProcessingException e) {
            LOGGER.error("Json convert error", e);
        }
    }

    private ExtendedShoe getShoeByfields(List<ExtendedShoe> dbShoes, ExtendedShoe.Color color, int size) {
        for (ExtendedShoe shoe : dbShoes) {
            if (shoe.getColor() == color && shoe.getSize() == size) {
                return shoe;
            }
        }
        return null;
    }
}
