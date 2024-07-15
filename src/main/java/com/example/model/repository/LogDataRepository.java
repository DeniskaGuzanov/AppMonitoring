package com.example.model.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.model.entity.LogData;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface LogDataRepository extends JpaRepository<LogData, Long> {


    List<LogData> findAll();

    @Query("SELECT d FROM LogData d where d.timestamp>=:date ORDER BY d.timestamp DESC")
    ArrayList<LogData> getAllFromDate(@Param("date") LocalDateTime date);

    @Transactional
    @Modifying
    @Query("Delete from LogData d where d.timestamp<:date")
    void deleteOlderDate(@Param("date") LocalDateTime date);
}