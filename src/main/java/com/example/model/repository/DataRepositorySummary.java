package com.example.model.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import com.example.model.entity.DataSummary;

import java.time.LocalDateTime;
import java.util.List;

//todo добавить описание
@Repository
public interface DataRepositorySummary extends ListCrudRepository<DataSummary, Long> {

    List<DataSummary> getDataSummaryByDateBetween(LocalDateTime date, LocalDateTime date2);

    @Transactional
    void deleteDataSummaryByDateBetween(LocalDateTime dateStart, LocalDateTime dateEnd);


    boolean existsByDateBetween(LocalDateTime date1,LocalDateTime date2);

    DataSummary getDataSummaryByDate(LocalDateTime date);
}