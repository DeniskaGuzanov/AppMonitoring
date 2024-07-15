package com.example.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.model.entity.DataVos5;

import java.time.LocalDateTime;
import java.util.List;

public interface DataRepositoryVos5 extends JpaRepository<DataVos5, Long> {

    /**
     * Получить cледующую по времени запись
     */
    @Query("SELECT d FROM DataVos5 d where d.date> :date ORDER BY d.date ASC LIMIT 1")
    DataVos5 getNextData(@Param("date") LocalDateTime date);

    /**
     * Получить предыдущий запас воды
     */
    @Query("select d.cleanWaterSupply from DataVos5 d where d.date<:date order by d.date desc limit 1")
    Double getPrevCleanWaterSupplyByDate(@Param("date") LocalDateTime date);

    /**
     * Получить следующий запас воды
     */
    @Query("select d.cleanWaterSupply from DataVos5 d where d.date>:date order by d.date asc limit 1")
    Double getNextCleanWaterSupplyByDate(@Param("date") LocalDateTime date);


    /**
     * Проверка наличия записи с заданной датой
     *
     * @param date - дата LocalDateTime
     * @return true-есть запись
     * false - нет записи
     */
    boolean existsByDate(LocalDateTime date);

    /**
     * Получить запись на указанную дату и время
     */
    DataVos5 getDataVos5ByDate(LocalDateTime date);

    /**
     * Получить все записи между указанными датами (включительно)
     *
     * @param date  - начало
     * @param date2 - конец
     */
    List<DataVos5> getDataVos5sByDateBetweenOrderByDateAsc(LocalDateTime date, LocalDateTime date2);
}