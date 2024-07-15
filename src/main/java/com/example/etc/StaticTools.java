package com.example.etc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;

/**
 * Класс вспомогательных методов
 */
public class StaticTools {
    /**
     * Метод усечения дробной части числа
     *
     * @param number - исходное число
     * @param round  - число знаков после запятой после усечения
     * @return число типа Double
     */
    public static Double dropSmallDecimalPart(Double number, int round) {
        return new BigDecimal(number).setScale(round, RoundingMode.HALF_UP).doubleValue();
    }
}