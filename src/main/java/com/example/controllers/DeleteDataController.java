package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.model.services.DataServiceVOS15;
import com.example.model.services.DataServiceVOS5;
import com.example.model.services.LoggerService;

/**
 * Класс контроллеров для запросов удаления данных
 */
@Controller
@RequestMapping("/delete")
public class DeleteDataController {
    final DataServiceVOS5 dataServiceVOS5;
    final DataServiceVOS15 dataServiceVOS15;
    final LoggerService loggerService;

    public DeleteDataController(DataServiceVOS5 dataServiceVOS5, DataServiceVOS15 dataServiceVOS15, LoggerService loggerService) {
        this.dataServiceVOS5 = dataServiceVOS5;
        this.dataServiceVOS15 = dataServiceVOS15;
        this.loggerService = loggerService;
    }

    /** Контроллер удаления записи часового расхода по id для ВОС5000
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/vos5/{id}")
    public String delDataVos5ById(@PathVariable(required = false) Long id, Model model) {
        loggerService.create("Удаление данных ВОС5000")
                .addToLog("id="+id).push();
        model.addAttribute("url", "/main/vos5");
        if (id == null) {
            //указан несуществующий id
            model.addAttribute("result", "Ошибка удаления записи. Не найдена запрошенная запись");
        } else {
            loggerService.create("Удаление данных ВОС5000")
                    .addToLog("id="+id).push();
            if (dataServiceVOS5.delDataVos5ById(id)) {
                model.addAttribute("result", "Запись удалена");
            }else{
                model.addAttribute("result", "Ошибка удаления записи");
            }
        }

        return "result";
    }

    /** Контроллер удаления записи часового расхода по id для ВОС15000
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/vos15/{id}")
    public String delDataVos15ById(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("url", "/main/vos15");
        if (id == null) {
            //указан несуществующий id
            model.addAttribute("result", "Ошибка удаления записи. Не найдена запрошенная запись");
        } else {
            loggerService.create("Удаление данных ВОС15000")
                    .addToLog("id="+id).push();
            if (dataServiceVOS15.delDataVos15ById(id)) {
                model.addAttribute("result", "Запись удалена");
            }else{
                model.addAttribute("result", "Ошибка удаления записи");
            }
        }
        return "result";
    }

}