package com.example.model.services;


import org.springframework.stereotype.Service;
import com.example.model.entity.DataSummary;
import com.example.model.entity.DataVos15;
import com.example.model.entity.DataVos5;
import com.example.model.repository.DataRepositorySummary;
import com.example.model.repository.DataRepositoryVos15;
import com.example.model.repository.DataRepositoryVos5;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.example.etc.StaticTools.dropSmallDecimalPart;

@Service
public class ReportService {

    DataRepositoryVos5 dataRepositoryVos5;
    DataRepositoryVos15 dataRepositoryVos15;
    DataRepositorySummary dataRepositorySummary;

    Boolean recalAllData;

    public ReportService(DataRepositoryVos5 dataRepositoryVos5, DataRepositoryVos15 dataRepositoryVos15, DataRepositorySummary dataRepositorySummary) {
        this.dataRepositoryVos5 = dataRepositoryVos5;
        this.dataRepositoryVos15 = dataRepositoryVos15;
        this.dataRepositorySummary = dataRepositorySummary;
        this.recalAllData = false;
    }


    /**
     * Строка сводного отчета на указанную дату
     *
     * @param dataVos5
     * @param dataVos15
     * @return
     */
    public DataSummary buildDataSummary(DataVos5 dataVos5, DataVos15 dataVos15) {
        if (dataVos5 == null || dataVos15 == null) return null;
//        if (dataVos5.getDate().equals(dataVos15.getDate())) return null;
        DataSummary dataSummary = new DataSummary();
        dataSummary.setDate(dataVos5.getDate());
        dataSummary.setVolExtract(dropSmallDecimalPart(dataVos5.getVolExtract() + dataVos15.getVolExtract(), 1));
        dataSummary.setVolCiti(dropSmallDecimalPart(dataVos5.getVolCity() + dataVos15.getVolCity() - dataVos5.getVolBackCity() - dataVos5.getVolBackVos15(), 1));
        dataSummary.setCleanWaterSupply(dropSmallDecimalPart(dataVos5.getCleanWaterSupply() + dataVos15.getCleanWaterSupply(), 1));
        dataSummary.setDeltaCleanWaterSupply(dropSmallDecimalPart(dataSummary.getVolExtract() - dataSummary.getVolCiti(), 1));
        dataSummary.setAverageVolCiti(dropSmallDecimalPart(getAverageVolCityFromDateOnTime(dataSummary.getDate()), 1));
        return dataSummary;
    }

    public void saveDataSummaryOneRecord(LocalDateTime date) {
        DataSummary dataSummary = buildDataSummary(dataRepositoryVos5.getDataVos5ByDate(date), dataRepositoryVos15.getDataVos15ByDate(date));
        if (dataSummary != null) dataRepositorySummary.save(dataSummary);
    }

    /**
     * Получить отчет за текущий день
     *
     * @return
     */
    public List<DataSummary> getSummaryReportToDay() {
        return getSummaryReportByDay(LocalDate.now());
    }

    /**
     * Перерасчет сводного отчета на указанную дату
     *
     * @param date - дата LocalDateTime
     */
    public void recalcSummaryReportByDate(LocalDateTime date) {
        date = date.truncatedTo(ChronoUnit.DAYS);
        dataRepositorySummary.deleteDataSummaryByDateBetween(date, date.plusHours(23));
        for (int i = 0; i < 24; i++) {
            saveDataSummaryOneRecord(date.plusHours(i));
        }
    }

    /**
     * Перерасчет сводного отчета на указанную дату
     *
     * @param date - дата DateTime
     */
    public void recalcSummaryReportByDate(LocalDate date) {
        recalcSummaryReportByDate(date.atStartOfDay());
    }


    public String recalcSummaryReportFromDateToDate(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) return "Ошибка. Одна или обе даты не заданны";
        if (date1.isAfter(date2)) return "Ошибка. Дата начала периода не может быть позже даты окончания.";
        if (this.recalAllData) return "Ошибка.Уже выполняется задача пересчета сводного отчета.";
        this.recalAllData = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LocalDate date = null;
                try {
                    for (date = date1; !date.isAfter(date2); date = date.plusDays(1)) {
                        recalcSummaryReportByDate(date);
                    }}catch (Exception e){
                    System.out.println("Error: " + date);
                    e.printStackTrace();
                }finally {
                    recalAllData = false;
                }
            }
        });
        thread.start();

        return "Задание на пересчет сводного отчета запущено.";
    }

    /**
     * Проверка существования отчета на указанную дату
     *
     * @param date -дата LocalDateTime
     * @return
     */
    public boolean existsReportByDate(LocalDate date) {
        LocalDateTime localDate = date.atStartOfDay();
        return dataRepositorySummary.existsByDateBetween(localDate, localDate.plusHours(23));
    }

    /**
     * Получить сводный отчет на указанную дату
     *
     * @param date
     * @return
     */
    public List<DataSummary> getSummaryReportByDay(LocalDate date) {
        if (existsReportByDate(date)) {
            LocalDateTime dateBegin = date.atStartOfDay();
            LocalDateTime dateEnd = dateBegin.plusHours(23);
            return dataRepositorySummary.getDataSummaryByDateBetween(dateBegin, dateEnd);
        }
        return null;
    }

    public Double getAverageVolCityFromDateOnTime(LocalDateTime endTime) {
        LocalDateTime startTime = endTime.minusDays(10);
        Double result = 0.0;
        int count = 0;
        for (LocalDateTime time = startTime; !time.isAfter(endTime); time = time.plusDays(1)) {
            DataSummary data = dataRepositorySummary.getDataSummaryByDate(time);
            if (data != null) {
                result += data.getVolCiti();
                count++;
            }
        }
        if (count == 0) return 0.0;
        return result / count;
    }
}