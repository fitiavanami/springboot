package org.insi.javaspring.controller;

import lombok.RequiredArgsConstructor;
import org.insi.javaspring.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var stats = dashboardService.getStats();

        model.addAttribute("stats", stats);
        model.addAttribute("title", "Dashboard");
        model.addAttribute("content", "dashboard/index");

        return "index";
    }
}