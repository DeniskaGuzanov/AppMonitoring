package com.example.etc.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Исключение для перехвата ситуации повторного ввода данных в текущем часе
 */
public class PrematureEntryException extends Exception {
    @Getter
    String message;
    public PrematureEntryException(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm");
        message = "Данные за "+date.format(formatter)+" уже были переданы.\n";
    }

}