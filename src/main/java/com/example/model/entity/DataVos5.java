package com.example.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.etc.StaticTools;
import java.time.LocalDateTime;
import static com.example.etc.DataFormats.formatterTimeOnly;

@Entity
@Data
@Table(name = "datavos5")
@AllArgsConstructor
@NoArgsConstructor
public class DataVos5 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long userId;
    @Column
    private LocalDateTime date;
    @Column
    private Double volExtract;
    @Column
    private Double volCity;
    @Column
    private Double volBackCity;
    @Column
    private Double volBackVos15;
    @Column
    private Double cleanWaterSupply;
    @Column
    private Double deltaCleanWaterSupply;
    @Column
    private Double pressureCity;
    @Column
    private Double pressureBackCity;
    @Column
    private Double pressureBackVos15;

    /**
     * Рост запаса чистой воды вычисленный
     */
    public Double getDeltaCleanWaterSupplyCalculated() {
        return StaticTools.dropSmallDecimalPart(getVolAll() - volCity, 1);
    }

    /**
     * Общий приход воды
     */
    public Double getVolAll() {
        return StaticTools.dropSmallDecimalPart(volExtract + volBackCity + volBackVos15, 1);
    }

    /**
     * Расчетный запас воды
     */
    public Double getCleanWaterSupplyCalc() {
        return StaticTools.dropSmallDecimalPart(cleanWaterSupply + getDeltaCleanWaterSupplyCalculated(), 1);
    }

    /**
     * Представление времени для табличного вывода
     *
     * @return the formatted date-time string, not null
     */
    public String getDateT() {
        return date.format(formatterTimeOnly);
    }

    /**
     * Обновление полей данных
     */
    public DataVos5 update(Long id, Long userId, LocalDateTime date,
                           Double volExtract, Double volCiti, Double volBackCity, Double volBackVos15,
                           Double cleanWaterSupply, Double deltaCleanWaterSupply,
                           Double pressureCity, Double pressureBackCity, Double pressureBackVos15) {
        this.setId(id);
        this.setUserId(userId);
        this.setDate(date);
        this.setVolExtract(volExtract);
        this.setVolCity(volCiti);
        this.setVolBackCity(volBackCity);
        this.setVolBackVos15(volBackVos15);
        this.setCleanWaterSupply(cleanWaterSupply);
        this.setDeltaCleanWaterSupply(deltaCleanWaterSupply);
        this.setPressureCity(pressureCity);
        this.setPressureBackCity(pressureBackCity);
        this.setPressureBackVos15(pressureBackVos15);
        return this;
    }
}