package com.example.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.etc.exceptions.PrematureEntryException;
import com.example.model.entity.DataVos15;
import com.example.model.entity.DataVos5;
import com.example.model.services.DataServiceVOS15;
import com.example.model.services.DataServiceVOS5;
import com.example.model.services.LoggerService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Controller
public class InputDataController {
    final DataServiceVOS5 dataServiceVOS5;
    final DataServiceVOS15 dataServiceVOS15;
    final LoggerService loggerService;

    public InputDataController(DataServiceVOS5 dataServiceVOS5, DataServiceVOS15 dataServiceVOS15, LoggerService loggerService) {
        this.dataServiceVOS5 = dataServiceVOS5;
        this.dataServiceVOS15 = dataServiceVOS15;
        this.loggerService = loggerService;
    }


    /**
     * Форма ввода записи для ВОС5000
     */
    @GetMapping("/input/vos5")
//    @PreAuthorize("hasAnyRole('ADMIN','VOS5')")
    public String inputDataVos5() {
        loggerService.create("Запрос формы ввода для ВОС5000").push();
        return "vos5/inputVos5";
    }


    /**
     * Запись новых данных для ВОС5000 в базу
     */
    @PostMapping(value = "/input/vos5")
//    @PreAuthorize("hasAnyRole('ADMIN','VOS5')")
    public String addDataVos5(@RequestParam Double volExtract,
                              @RequestParam LocalDateTime date,
                              @RequestParam Double volCity,
                              @RequestParam Double volBackCity,
                              @RequestParam Double volBackVos15,
                              @RequestParam Double cleanWaterSupply,
                              @RequestParam Double pressureCity,
                              @RequestParam Double pressureBackCity,
                              @RequestParam Double pressureBackVos15,
                              Model model) {
        DataVos5 dataVos5;
        try {
            loggerService.create("Ввод данных в базу ВОС5000").push();
            dataVos5 = dataServiceVOS5.createDataVos5(
                    date,
                    volExtract,
                    volCity,
                    volBackCity,
                    volBackVos15,
                    cleanWaterSupply,
                    pressureCity,
                    pressureBackCity,
                    pressureBackVos15);
            model.addAttribute("result",
                    dataServiceVOS5.saveDataVos5(dataVos5) ?
                            "Запись добавлена" :
                            "Ошибка записи. Обратитесь к администратору.");
        } catch (PrematureEntryException e) {
            model.addAttribute("result",
                    e.getMessage());
        }
        model.addAttribute("url", "/main/vos5");
        return "result";
    }


    /**
     * Форма для изменения записи ВОС15000
     *
     * @param id - id записи
     */
    @GetMapping("/update/vos5/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','VOS5')")
    public String updateFormVos5(@PathVariable long id, Model model) {
        loggerService.create("Запрос данных для изменения ВОС5000")
                .addToLog("id=" +id)
                .push();
        DataVos5 dataVos5 = dataServiceVOS5.getDataVos5ById(id);
        if (dataVos5 == null) return "redirect:/main/vos5";
        model.addAttribute("entity", dataVos5);
        return "vos5/updateVos5";
    }

    /**
     * Обновление данных записи для ВОС5000 из web формы
     */
    @PostMapping(value = "/update/vos5")
//    @PreAuthorize("hasAnyRole('ADMIN','VOS5')")
    public String updateDataVos5(@RequestParam Long id,
                                 @RequestParam Long userId,
                                 @RequestParam LocalDateTime date,
                                 @RequestParam Double volExtract,
                                 @RequestParam Double volCiti,
                                 @RequestParam Double volBackCity,
                                 @RequestParam Double volBackVos15,
                                 @RequestParam Double cleanWaterSupply,
                                 @RequestParam Double pressureCity,
                                 @RequestParam Double pressureBackCity,
                                 @RequestParam Double pressureBackVos15,
                                 Model model) {
        loggerService.create("Обновление записи ВОС5000")
                .addToLog("id="+id).push();
        if (dataServiceVOS5.updateAndSaveDataVos5ByDate(
                id, userId,
                date, volExtract,
                volCiti, volBackCity, volBackVos15,
                cleanWaterSupply,
                pressureCity, pressureBackCity, pressureBackVos15)) {
            model.addAttribute("result", "Данные обновлены\n");
        } else {
            model.addAttribute("result", "При обновлении данных произошла ошибка");
        }
        model.addAttribute("url", "/main/vos5");
        return "result";
    }

    /**
     * Форма ввода записи для ВОС15000
     */
    @GetMapping("/input/vos15")
    public String inputDataVos15() {

        loggerService.create("Запрос формы ввода данных ВОС15000").push();
        return "vos15/inputVos15";
    }

    @RequestMapping(value = "/input/vos15", method = RequestMethod.POST)
    @PreAuthorize("hasAnyRole('ADMIN','VOS15')")
    public String addDataVos15(@RequestParam LocalDateTime date,
                               @RequestParam Double volExtract,
                               @RequestParam Double volLeftCity,
                               @RequestParam Double volRightCity,
                               @RequestParam Double cleanWaterSupply,
                               @RequestParam Double pressureCity,
                               Model model) {
        DataVos15 dataVos15;
        loggerService.create("Запись данных ВОС15000").push();
        try {
            dataVos15 = dataServiceVOS15.createDataVos15(
                    date,
                    volExtract,
                    volLeftCity, volRightCity,
                    cleanWaterSupply,
                    pressureCity);
            model.addAttribute("result",
                    dataServiceVOS15.saveDataVos15(dataVos15) ?
                            "Запись добавлена" :
                            "Ошибка записи. Обратитесь к администратору.");
        } catch (PrematureEntryException e) {
            // если данные за текущий час уже были добавлены в базу сообщаем об ошибке
            model.addAttribute("result",
                    e.getMessage());
        }
        model.addAttribute("url", "/main/vos15");
        return "result";
    }

    /**
     * Обновление данных записи для ВОС15000 из web формы
     */
    @PostMapping("/update/vos15")
    public String updateDataVos15(@RequestParam Long id,
                                  @RequestParam Long userId,
                                  @RequestParam LocalDateTime date,
                                  @RequestParam Double volExtract,
                                  @RequestParam Double volLeftCity,
                                  @RequestParam Double volRightCity,
                                  @RequestParam Double cleanWaterSupply,
                                  @RequestParam Double pressureCity,
                                  Model model) {
        loggerService.create("Обновление данных ВОС15000")
                .addToLog("id="+id)
                .push();
        date = date.truncatedTo(ChronoUnit.HOURS);
        if (userId == null) userId = 0L;
        DataVos15 dataVos15 = dataServiceVOS15.createDataVos15(id,
                userId,
                date,
                volExtract,
                volLeftCity, volRightCity,
                cleanWaterSupply,
                pressureCity);
        if (dataServiceVOS15.updateAndSaveDataVos15ByDate(dataVos15)) {
            model.addAttribute("result", "Данные обновлены\n");
        } else {
            model.addAttribute("result", "При обновлении данных произошла ошибка");
        }
        model.addAttribute("url", "/main/vos15");
        return "result";
    }


    /**
     * форма для изменения записи ВОС15000
     *
     * @param id - id записи
     */
    @GetMapping("/update/vos15/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','VOS15')")
    public String updateFormVos15(@PathVariable long id, Model model) {
        loggerService.create("Запрос формы для обновления данных ВОС15000")
                .addToLog("id=" +id)
                .push();
        DataVos15 dataVos15 = dataServiceVOS15.getDataVos15ById(id);
        if (dataVos15 == null) return "redirect:/main/vos15";
        model.addAttribute("entity", dataVos15);
        return "vos15/updateVos15";
    }
}