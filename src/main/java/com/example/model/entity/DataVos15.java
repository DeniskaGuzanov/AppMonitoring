package com.example.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.etc.StaticTools;

import java.time.LocalDateTime;

import static com.example.etc.DataFormats.formatterTimeOnly;


/**
 * Запись часового расхода по станции ВОС15000
 */
@Entity
@Data
@Table(name = "datavos15")
@NoArgsConstructor
public class DataVos15 {
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
    private Double volLeftCity;
    @Column
    private Double volRightCity;
    @Column
    private Double cleanWaterSupply;
    @Column
    private Double deltaCleanWaterSupply;
    @Column
    private Double pressureCity;

    public DataVos15(Long id, Long userId, LocalDateTime date, Double volExtract, Double volLeftCity, Double volRightCity, Double cleanWaterSupply, Double deltaCleanWaterSupply, Double pressureCity) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.volExtract = volExtract;
        this.volCity = volLeftCity + volRightCity;
        this.volLeftCity = volLeftCity;
        this.volRightCity = volRightCity;
        this.cleanWaterSupply = cleanWaterSupply;
        this.deltaCleanWaterSupply = deltaCleanWaterSupply;
        this.pressureCity = pressureCity;
    }

    public Double getDeltaCleanWaterSupplyCalculated() {
        return StaticTools.dropSmallDecimalPart(volExtract - volCity, 1);
    }


    public String getDateT() {
        return date.format(formatterTimeOnly);
    }

    public DataVos15 update(Long id, Long userId, LocalDateTime date,
                            Double volExtract, Double volLeftCity, Double volRightCity,
                            Double cleanWaterSupply,
                            Double pressureCity) {
        this.setId(id);
        this.setUserId(userId);
        this.setDate(date);
        this.setVolExtract(volExtract);
        this.setVolCity(volLeftCity + volRightCity);
        this.setVolLeftCity(volLeftCity);
        this.setVolRightCity(volRightCity);
        this.setCleanWaterSupply(cleanWaterSupply);
        this.setDeltaCleanWaterSupply(getDeltaCleanWaterSupplyCalculated());
        this.setPressureCity(pressureCity);
        return this;
    }
}