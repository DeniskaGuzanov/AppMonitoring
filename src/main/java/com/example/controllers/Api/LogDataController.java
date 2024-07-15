package com.example.controllers.Api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.entity.LogData;
import com.example.model.repository.LogDataRepository;

import java.util.Collections;
import java.util.List;

@RestController
public class LogDataController {
    final LogDataRepository repo;

    public LogDataController(LogDataRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/api/log")
    public List<LogData> getLogData() {
        List<LogData> logDataList = repo.findAll();
        return Collections.unmodifiableList(logDataList);
    }



}