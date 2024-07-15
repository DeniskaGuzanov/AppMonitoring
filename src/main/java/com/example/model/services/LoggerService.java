package com.example.model.services;

import org.springframework.stereotype.Service;
import com.example.model.entity.LogData;
import com.example.model.repository.LogDataRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;


@Service
public class LoggerService {
    LogDataRepository logDataRepository;

    private LogData logData;
    private StringBuilder logMessage;
    private LocalDateTime logDateTime;

    public LoggerService(LogDataRepository logDataRepository) {
        this.logDataRepository = logDataRepository;
        logDateTime = null;
        logMessage = new StringBuilder();
    }

    public LoggerService create() {
        logData = new LogData();
        logDateTime = LocalDateTime.now();
        return this;
    }

    public LoggerService create(String message) {
        logDateTime = LocalDateTime.now();
        logMessage.append(message).append("; ");
        return this;
    }

    public LoggerService addToLog(String message) {
        if (logDateTime == null) {
            logDateTime =LocalDateTime.now();
        }
        logMessage.append(message + "; ");
        return this;
    }


    public void push() {
        if (logDateTime == null) {
            System.out.println("LoggerService.push() - ошибка записи в лог. Нет установленного времени.");
            return;
        }
        logData = new LogData(logDateTime,
                logMessage.toString());
        logDataRepository.save(logData);
        System.out.println("Save to log: " + logData.toString());
        clear();
    }

    public void clear() {
        logDateTime = null;
        logMessage.setLength(0);
        logData = null;
    }

    /**
     * Получить все записи лога начиная с date
     *
     * @param date
     * @return
     */
    public ArrayList<LogData> getLogDataFromDate(LocalDate date) {
        ArrayList<LogData> logDataArrayList = logDataRepository.getAllFromDate(date.atTime(LocalTime.MIN));
        if (logDataArrayList == null) {
            return new ArrayList<>();
        }
        return logDataArrayList;
    }

    /**
     * Очистка старых записей в логе старше date
     *
     * @param date
     */
    public void cleagLogDataInDatabaseOlderDate(LocalDate date) {
        logDataRepository.deleteOlderDate(date.atTime(LocalTime.MIN));
    }


}