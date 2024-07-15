package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.model.entity.DataSummary;
import com.example.model.services.ReportService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.example.etc.DataFormats.formatterDateOnly;

/**
 * Класс контроллеров страниц отчетов
 */
@Controller
@RequestMapping("/report")
public class ReportController {
    final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** Главная страница с выбором отчетов
     */
    @GetMapping("")
    public String reportMainPage() {
        return "report/mainReport";
    }

    /**
     * Сводный отчет за текущие сутки,
     * возвращает сводный отчет по текущему времени(дате)
     */
    @GetMapping("/summary")
    public String summaryReport(Model model) {
        List<DataSummary> dataSummaryList = reportService.getSummaryReportToDay();
        if(dataSummaryList==null){
            dataSummaryList=new ArrayList<>();
        }
        model.addAttribute(dataSummaryList);
        model.addAttribute("currentDate", LocalDate.now().format(formatterDateOnly));
        return "report/summaryReport";
    }

    /**
     * Принудительное обновление(пересчет) сводного отчета за текущие сутки
     */
    @GetMapping("/summary/update")
    public String summaryReportUpdate() {
        reportService.recalcSummaryReportByDate(LocalDate.now());
        return "redirect:/report/summary";
    }



    /** Обработчик GET запроса сводного отчета за определенный день
     * @param date - дата (необязательный параметр)
     */
    @GetMapping("/dayreports")
    public String dayReports(@RequestParam(required = false) LocalDate date, Model model) {
        if (date == null) {
            model.addAttribute("isReportExist", false);
            model.addAttribute("currentDate", " --- ");
            model.addAttribute("message","Выберите дату отчета");
        } else {
            model.addAttribute("currentDate",date);
            if(reportService.existsReportByDate(date)) {
                model.addAttribute("isReportExist",true);
                model.addAttribute("dataList", reportService.getSummaryReportByDay(date));
            }else {
                model.addAttribute("isReportExist", false);
                model.addAttribute("currentDate", "");
                model.addAttribute("message","Нет отчета на этот день "+date.format(formatterDateOnly));
            }
        }
        return "report/dayReports";
    }
}