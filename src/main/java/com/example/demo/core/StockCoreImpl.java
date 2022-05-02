package com.example.demo.core;

import com.example.demo.core.service.StockService;
import com.example.demo.dto.out.ExtendedShoe;
import com.example.demo.dto.out.Stock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Implementation(version = 2)
public class StockCoreImpl extends AbstractStockCore {

    @Autowired
    private StockService stockService;

    @Override
    public Stock.State getStockState(String name) {
        return stockService.getState(name);
    }

    @Override
    public Stock getStock(String name) {
        return stockService.getStock(name);
    }

    @Override
    public Stock updateStock(String name, List<ExtendedShoe> shoes) {
        return stockService.updateStock(name, shoes);
    }
}
