package com.example.demo.core.service;

import com.example.demo.core.model.StockModel;
import com.example.demo.core.repository.StockRepository;
import com.example.demo.core.service.StockService;
import com.example.demo.dto.out.Stock;
import com.example.demo.dto.out.StockShoe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    private Stock stock;

    @Before
    public void initStock() {
        StockModel stockModel = new StockModel();
        stockModel.setName("sport");
        stockModel.setCapacity(30);
        stockModel.setShoes("[{\"color\": \"BLACK\",\"size\": 40,\"quantity\": 10}]");

        List<StockShoe> shoes = new ArrayList<>();
        shoes.add(new StockShoe(StockShoe.Color.BLACK, 40, 10));
        stock = Stock.builder()
                .name("sport")
                .state(Stock.State.SOME)
                .shoes(shoes)
                .build();

        Mockito.when(stockRepository.findByName(anyString())).thenReturn(java.util.Optional.of(stockModel));
    }

    @After
    public void destroy() {
        stockRepository.deleteAll();
    }

    @Test
    public void whenFindByName_thenReturnStock() {
        var stockFromService = stockService.getStock("sport");
        assertEquals(stock.getName(), stockFromService.getName());
    }

    @Test
    public void whenGetState_thenReturnState() {
        var state = stockService.getState("sport");
        assertEquals(stock.getState(), state);
    }

    @Test
    public void whenUpdateStock_thenReturnStock() {
        List<StockShoe> shoes = new ArrayList<>();
        shoes.add(new StockShoe(StockShoe.Color.BLACK, 40, 5));
        var updatedStock = stockService.updateStock("sport", shoes);
        assertEquals(updatedStock.getShoes().get(0).getQuantity(), 5);
    }

    @Test
    public void whenUpdateStock_thenReturnException() {
        List<StockShoe> shoes = new ArrayList<>();
        shoes.add(new StockShoe(StockShoe.Color.BLACK, 40, 31));

        assertThrows(IllegalArgumentException.class,
                () -> stockService.updateStock("sport", shoes));
    }
}
