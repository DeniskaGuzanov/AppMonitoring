package com.example.etc;

import java.time.format.DateTimeFormatter;

/**
 * константы формата даты
 */
public class DataFormats {
    public static final DateTimeFormatter formatterTimeOnly = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter formatterDateOnly = DateTimeFormatter.ofPattern("dd.MM.uuuu");
    public static final DateTimeFormatter formatterDateTimeFull = DateTimeFormatter.ofPattern("dd.MM.uuuu HH:mm");
}