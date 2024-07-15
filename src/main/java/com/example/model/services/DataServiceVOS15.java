package com.example.model.services;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.etc.exceptions.PrematureEntryException;
import com.example.model.entity.DataVos15;
import com.example.model.repository.DataRepositoryVos15;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class DataServiceVOS15 {

    @Autowired
    DataRepositoryVos15 dataRep15;
    @Autowired
    ReportService reportService;

    /**
     * Получить данные за текущий день
     */
    public List<DataVos15> getCurrentDayVos15() {
        return getDataVos15ByDay(LocalDateTime.now());
    }

    /**
     * Запись в базу данных
     */
    @Transactional
    public boolean saveDataVos15(DataVos15 dataVos15) {
        try {
            dataRep15.saveAndFlush(dataVos15);
            updateNextDataCleanWaterSupply(dataVos15);
            reportService.recalcSummaryReportByDate(dataVos15.getDate());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Обновление поля роста запаса чистой воды в следующей по времени записи
     *
     * @param dataVos15 - текущая запись
     */
    private void updateNextDataCleanWaterSupply(DataVos15 dataVos15) {
        DataVos15 nextData = dataRep15.getNextData(dataVos15.getDate());
        if (nextData != null) {
            nextData.setDeltaCleanWaterSupply(nextData.getCleanWaterSupply() - dataVos15.getCleanWaterSupply());
            dataRep15.saveAndFlush(nextData);
        }
    }



    /**
     * Создание записи по данным из формы с проверкой на повторный ввод
     *
     * @param date             - дата
     * @param volExtract       - объем добычи
     * @param volCiti          - отдача в город
     * @param cleanWaterSupply - запас чистой воды
     * @param pressureCity     - давление в трубопроводе в город
     * @return DataVos15
     * @throws PrematureEntryException
     */
    public DataVos15 createDataVos15(LocalDateTime date, Double volExtract, Double volLeftCity, Double volRightCity,
                                     Double cleanWaterSupply, Double pressureCity)
            throws PrematureEntryException {
        date = date.truncatedTo(ChronoUnit.HOURS); //усечение времени до часа (отбрасываем все что меньше часа)
        /* проверка на повторное добавление записи в текущем часе*/
        if (dataRep15.existsByDate(date)) {
            throw new PrematureEntryException(date);
        }
        DataVos15 dataVos15 = new DataVos15();
        return dataVos15.update(
                dataVos15.getId(),
                2L,
                date,
                volExtract,
                volLeftCity,
                volRightCity,
                cleanWaterSupply,
                pressureCity);
    }


    public boolean updateAndSaveDataVos15ByDate(DataVos15 dataVos15) {
        try {
            dataRep15.saveAndFlush(dataVos15);
            updateNextDataCleanWaterSupply(dataVos15);
            reportService.recalcSummaryReportByDate(dataVos15.getDate());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public DataVos15 getDataVos15ById(long id) {
        return dataRep15.findById(id).orElse(null);
    }


    /**
     * Создание строки записи (без записи в базу)
     *
     * @param id               - id
     * @param userId           - идентификатор пользователя
     * @param date             - дата
     * @param volExtract       - объем добычи воды
     * @param volLeftCiti          - объем подачи в город по левой нитке водовода
     * @param volRightCity          - объем подачи в город по правой нитке водовода
     * @param cleanWaterSupply - запас чистой воды
     * @param pressureCity     - давление в трубопроводе
     * @return - dataVos15
     */
    public DataVos15 createDataVos15(Long id, Long userId, LocalDateTime date,
                                     Double volExtract, Double volLeftCiti, Double volRightCity,
                                     Double cleanWaterSupply,
                                     Double pressureCity) {

        DataVos15 prevData = dataRep15.getPrevData(date);
        Double deltaCleanWaterSupply;
        if (prevData != null) {
            deltaCleanWaterSupply = cleanWaterSupply - prevData.getCleanWaterSupply();
        } else {
            deltaCleanWaterSupply = cleanWaterSupply;
        }

        return new DataVos15(id, userId, date,
                volExtract, volLeftCiti, volRightCity,
                cleanWaterSupply, deltaCleanWaterSupply,
                pressureCity);
    }

    /** Получить список записей за весь день по текущему времени
     * @param date текущая дата
     * @return список записей на текущий день(сутки)
     */
    public List<DataVos15> getDataVos15ByDay(LocalDateTime date) {
        return dataRep15.getDataVos15sByDateBetweenOrderByDateAsc(date.truncatedTo(ChronoUnit.DAYS),
                date.truncatedTo(ChronoUnit.DAYS).plusHours(23));
    }

    /** Удаление записи по id
     * @param id
     * @return true - если запись удалена
     */
    public boolean delDataVos15ById(Long id) {
        dataRep15.deleteById(id);
        return !dataRep15.existsById(id);
    }
}