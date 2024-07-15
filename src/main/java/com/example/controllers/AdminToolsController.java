package com.example.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.model.services.LoggerService;
import com.example.model.services.ReportService;

import java.time.LocalDate;

@Controller
public class AdminToolsController {
    ReportService reportService;
    LoggerService loggerService;

    public AdminToolsController(ReportService reportService, LoggerService loggerService) {
        this.loggerService = loggerService;
        this.reportService = reportService;
    }

    @GetMapping("/admin/fullupdatesummary")
    public String summaryReportFullUpdateGET() {
        return "report/fullUpdateSynnaryReportsFromDateToDate";
    }

    @PostMapping("/admin/fullupdatesummary")
    public String summaryReportFullUpdatePOST(@RequestParam(required = false) LocalDate date1,
                                              @RequestParam(required = false) LocalDate date2,
                                              Model model) {
        if (date1 == null) {
            date1 = LocalDate.now().minusDays(1);
        }
        if (date1 != null && date2 == null) {
            date2 = LocalDate.now();
        }
        loggerService.create("Запуск пересчета сводных отчетов c "+date1+" по "+date2).push();

        model.addAttribute("report", reportService.recalcSummaryReportFromDateToDate(date1, date2));

        return "report/fullUpdateSynnaryReportsFromDateToDate";
    }

    @GetMapping("/admin")
    public String adminIndexPage() {
        return "adminTools/admin";
    }

    @GetMapping("/admin/logview")
    public String logviewPage() {
        return "adminTools/logview";
    }

}