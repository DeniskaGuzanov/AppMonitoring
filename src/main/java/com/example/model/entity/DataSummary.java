package com.example.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.example.etc.DataFormats.formatterTimeOnly;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dataSummary")
public class DataSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private LocalDateTime date;
    @Column
    private Double volExtract;
    @Column
    private Double volCiti;
    @Column
    private Double cleanWaterSupply;
    @Column
    private Double deltaCleanWaterSupply;

    private Double averageVolCiti;

    public String getDateT(){
        return date.format(formatterTimeOnly);
    }
}