package com.example.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.model.entity.DataVos15;
import com.example.model.entity.DataVos5;
import com.example.model.services.DataServiceVOS15;
import com.example.model.services.DataServiceVOS5;
import com.example.model.services.UserServices;

import java.util.List;

/**
 * Класс контроллеров обработчиков главной страницы и страниц часовых расходов для каждой станции
 */
@Controller
public class MainPageController {
    final DataServiceVOS5 dataServiceVOS5;
    final DataServiceVOS15 dataServiceVOS15;
    final UserServices userServices;

    public MainPageController(DataServiceVOS5 dataServiceVOS5, DataServiceVOS15 dataServiceVOS15, UserServices userServices) {
        this.dataServiceVOS5 = dataServiceVOS5;
        this.dataServiceVOS15 = dataServiceVOS15;
        this.userServices = userServices;
    }

    /**
     * Главная страница расходов для ВОС5000
     */
    @GetMapping("/main/vos5")
    public String mainPageVos5(Model model) {
        sendAuthUserToModel(model);
        List<DataVos5> dataVos5 = dataServiceVOS5.getCurrentDayVos5();
        model.addAttribute("dataVos5", dataVos5);
        return "vos5/mainVos5";
    }

    /**
     * Главная страница расходов для ВОС15000
     */
    @GetMapping("/main/vos15")
    public String mainPageVos15(Model model) {
        sendAuthUserToModel(model);
        List<DataVos15> data = dataServiceVOS15.getCurrentDayVos15();
        model.addAttribute("dataVos15", data);
        return "vos15/mainVos15";
    }

    /**
     * Обработчик стартовой страницы
     */
    @GetMapping("/")
    public String indexPage(Model model) {
        sendAuthUserToModel(model);
        return "index";
    }

    /**
     * Обработчик страницы входа (авторизации)
     */

    @GetMapping("/login")
    public String login(HttpServletRequest request) {
        String referrer = request.getHeader("Referer");
        if(referrer!=null){
            request.getSession().setAttribute("url_prior_login", referrer);
        }
        return "login";
    }


    /**
     * Метод передачи имени авторизованного пользователя в модель
     * если пользователь авторизован, то передается его имя
     * если анонимный пользователь по передается строка "неавторизованный пользователь"
     */
    private static void sendAuthUserToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
//        System.out.println(username);
        if ("anonymousUser".equalsIgnoreCase(username)) username = "анонимный пользователь";
        model.addAttribute("username", username);
    }
}