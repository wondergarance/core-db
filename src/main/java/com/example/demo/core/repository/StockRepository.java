package com.example.demo.core.repository;

import com.example.demo.core.model.StockModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends CrudRepository<StockModel, Integer> {
    Optional<StockModel> findByName(String name);
}
