package com.example.demo.core.repository;

import com.example.demo.core.model.StockModel;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
@Ignore
public class StockRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    StockRepository stockRepository;

    @Test
    public void whenFindByName_thenReturnStock() {
        StockModel stockModel = new StockModel();
        stockModel.setName("sport");
        stockModel.setCapacity(30);
        stockModel.setShoes("[{\"color\": \"BLACK\",\"size\": 40,\"quantity\": 10}]");
        stockModel = entityManager.persistAndFlush(stockModel);

        assertEquals(stockRepository.findByName("sport"), stockModel);
    }
}
