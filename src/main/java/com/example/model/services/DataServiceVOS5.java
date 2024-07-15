package com.example.model.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.etc.exceptions.PrematureEntryException;
import com.example.model.entity.DataVos5;
import com.example.model.repository.DataRepositoryVos5;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class DataServiceVOS5 {
    final DataRepositoryVos5 dataRep5;
    final ReportService reportService;

    public DataServiceVOS5(DataRepositoryVos5 dataRep5, ReportService reportService) {
        this.dataRep5 = dataRep5;
        this.reportService = reportService;
    }

    /**
     * Получить данные за текущий день
     */
    public List<DataVos5> getCurrentDayVos5() {
        return getDataVos5ByDay(LocalDateTime.now());
    }

    /**
     * Запись в базу данных
     */
    @Transactional
    public boolean saveDataVos5(DataVos5 dataVos5) {
        try {
            dataRep5.saveAndFlush(dataVos5);
            updateNextDataCleanWaterSupply(dataVos5);
            reportService.recalcSummaryReportByDate(dataVos5.getDate());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Обновление поля роста запаса чистой воды в следующей по времени записи
     *
     * @param dataVos5 - текущая запись
     */
    private void updateNextDataCleanWaterSupply(DataVos5 dataVos5) {
        DataVos5 nextData = dataRep5.getNextData(dataVos5.getDate());
        if (nextData != null) {
            nextData.setDeltaCleanWaterSupply(nextData.getCleanWaterSupply() - dataVos5.getCleanWaterSupply());
            dataRep5.saveAndFlush(nextData);
        }
    }

    /**
     * Создание записи по данным из формы с проверкой на повторный ввод в текущем часе
     *
     * @param date              - дата
     * @param volExtract        - объем добычи
     * @param volCiti           - отдача в город
     * @param volBackCity       - обратка из города
     * @param volBackVos15      - обратка от ВОС15000
     * @param cleanWaterSupply  - запас чистой воды
     * @param pressureCity      - давление в трубопроводе в город
     * @param pressureBackCity  - давление в трубопроводе обратка из города
     * @param pressureBackVos15 - давление в трубопроводе обратка от ВОС15000
     */
    public DataVos5 createDataVos5(LocalDateTime date, Double volExtract, Double volCiti,
                                   Double volBackCity, Double volBackVos15, Double cleanWaterSupply,
                                   Double pressureCity, Double pressureBackCity, Double pressureBackVos15)
            throws PrematureEntryException {
        date = date.truncatedTo(ChronoUnit.HOURS); //усечение времени до часа (отбрасываем все что меньше часа)
        if (dataRep5.existsByDate(date)) {
            //проверка на повторное добавление записи
            throw new PrematureEntryException(date);
        }

        DataVos5 dataVos5 = new DataVos5();
        Double lostCleanWaterSupply = getPrevCleanWaterSupply(date);//предыдущий запас воды

        dataVos5.setUserId(1L);
        dataVos5.setDate(date);
        dataVos5.setVolExtract(volExtract);
        dataVos5.setVolCity(volCiti);
        dataVos5.setVolBackCity(volBackCity);
        dataVos5.setVolBackVos15(volBackVos15);
        dataVos5.setCleanWaterSupply(cleanWaterSupply);
        dataVos5.setPressureCity(pressureCity);
        dataVos5.setPressureBackCity(pressureBackCity);
        dataVos5.setPressureBackVos15(pressureBackVos15);
        dataVos5.setDeltaCleanWaterSupply(dataVos5.getCleanWaterSupply() - lostCleanWaterSupply);
        return dataVos5;
    }

    /**
     * Получить предыдущее значение запаса воды
     *
     * @param date - время перед которым ищется запись
     * @return предыдущий запас воды или 0.0
     */
    private Double getPrevCleanWaterSupply(LocalDateTime date) {
        Double cleanWaterSupply = dataRep5.getPrevCleanWaterSupplyByDate(date);
        return Objects.requireNonNullElse(cleanWaterSupply, 0.0);
    }


    /**
     * Обновить запись указанными параметрами из запроса (ключевое поле - date)
     *
     * @param id                - идентификатор
     * @param userId            - идентификатор пользователя
     * @param date              - дата записи
     * @param volExtract        - объем добычи станции
     * @param volCiti           - объем подачи в город
     * @param volBackCity       - объем обратной подачи из города
     * @param volBackVos15      - объем обратной подачи от ВОС15000
     * @param cleanWaterSupply  - запас чистой воды в накопителе РЧВ
     * @param pressureCity      - давление подачи в город
     * @param pressureBackCity  - давление обратки из города
     * @param pressureBackVos15 - давление обратки от ВОС15000
     */
    public boolean updateAndSaveDataVos5ByDate(
            Long id, Long userId,
            LocalDateTime date,
            Double volExtract, Double volCiti, Double volBackCity, Double volBackVos15,
            Double cleanWaterSupply,
            Double pressureCity, Double pressureBackCity, Double pressureBackVos15) {
        DataVos5 updateData = dataRep5.getDataVos5ByDate(date);
        Double deltaCleanWaterSupply = cleanWaterSupply - getPrevCleanWaterSupply(date);
        updateData.update(id, userId, date, volExtract, volCiti, volBackCity, volBackVos15, cleanWaterSupply, deltaCleanWaterSupply, pressureCity, pressureBackCity, pressureBackVos15);
        return updateAndSaveDataVos5ByDate(updateData);
    }


    /**
     * Обновление записи в базе с параллельным обновлением строки сводного отчета
     * @param dataVos5 обновленные данные для записи в базу
     */
    private boolean updateAndSaveDataVos5ByDate(DataVos5 dataVos5) {
        try {
//            dataRep5.deleteById(dataVos5.getId());//удаляем старую запись
            dataRep5.saveAndFlush(dataVos5);//добавляем измененную
            updateNextDataCleanWaterSupply(dataVos5); //обновляем значение роста запаса РЧВ в следующей по времени, существующей записи
            reportService.recalcSummaryReportByDate(dataVos5.getDate());//обновляем соответствующую запись сводного отчета
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public DataVos5 getDataVos5ById(long id) {
        return dataRep5.findById(id).orElse(null);
    }

    public List<DataVos5> getDataVos5ByDay(LocalDateTime date) {
        return dataRep5.getDataVos5sByDateBetweenOrderByDateAsc(date.truncatedTo(ChronoUnit.DAYS),
                date.truncatedTo(ChronoUnit.DAYS).plusHours(23));
    }

    /** Удаление записи по id
     * @param id
     * @return true - если запись удалена
     */
    public boolean delDataVos5ById(Long id) {
        dataRep5.deleteById(id);
        return !dataRep5.existsById(id);
    }
}