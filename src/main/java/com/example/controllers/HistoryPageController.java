package com.example.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.model.entity.DataVos15;
import com.example.model.entity.DataVos5;
import com.example.model.services.DataServiceVOS15;
import com.example.model.services.DataServiceVOS5;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс контроллеров обращения к разделу истории /history
 */
@Controller
@RequestMapping("/history")
public class HistoryPageController {
    final DataServiceVOS5 dataServiceVOS5;
    final DataServiceVOS15 dataServiceVOS15;

    public HistoryPageController(DataServiceVOS5 dataServiceVOS5, DataServiceVOS15 dataServiceVOS15) {
        this.dataServiceVOS5 = dataServiceVOS5;
        this.dataServiceVOS15 = dataServiceVOS15;
    }

    /**
     * Обработчик запроса данных расходов для ВОС5000
     *
     * @param date - дата
     */
    @GetMapping("/vos5")
    public String historyPageVos5(@RequestParam(required = false) LocalDate date, Model model) {
        if (date == null) {
            //не указана дата
            model.addAttribute(model.addAttribute("selectedDate", LocalDateTime.now()));

            model.addAttribute("message", "Выберите дату ");
            model.addAttribute("dataExists", false);
            return "history/historyVos5";
        }
        //указана дата
        List<DataVos5> dataVos5s = dataServiceVOS5.getDataVos5ByDay(date.atStartOfDay());
        model.addAttribute("selectedDate", date);
        if (dataVos5s == null || dataVos5s.isEmpty()) {
            //но нет данных
            model.addAttribute("message", "Нет записей за указанную дату");
            model.addAttribute("dataExists", false);
            return "history/historyVos5";
        }
        model.addAttribute("message", "");
        model.addAttribute("dataExists", true);
        model.addAttribute("dataVos5", dataVos5s);
        sendAuthUserToModel(model);
        return "history/historyVos5";
    }

    /**
     * Обработчик запроса данных расходов для ВОС15000
     *
     * @param date - дата
     */
    @GetMapping("/vos15")
    public String historyPageVos15(@RequestParam(required = false) LocalDate date, Model model) {
        if (date == null) {
            //не указана дата
            model.addAttribute(model.addAttribute("selectedDate", LocalDateTime.now()));
            model.addAttribute("message", "Выберите дату ");
            model.addAttribute("dataExists", false);
            return "history/historyVos15";
        }
        //указана дата
        List<DataVos15> dataVos15s = dataServiceVOS15.getDataVos15ByDay(date.atStartOfDay());
        model.addAttribute("selectedDate", date);
        if (dataVos15s == null || dataVos15s.isEmpty()) {
            //но нет данных
            model.addAttribute("message", "Нет записей за указанную дату");
            model.addAttribute("dataExists", false);
            return "history/historyVos15";
        }
        model.addAttribute("message", "");
        model.addAttribute("dataExists", true);
        model.addAttribute("dataVos15", dataVos15s);
        sendAuthUserToModel(model);
        return "history/historyVos15";
    }

    private static void sendAuthUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if ("anonymousUser".equalsIgnoreCase(username)) username = "анонимный пользователь";
        model.addAttribute("username", username);
    }
}