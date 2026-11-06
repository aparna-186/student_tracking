package org.example.skillsprint.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.DashboardDAO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.sql.SQLException;

@Controller
public class DashboardController {
    private final DashboardDAO dao;
    public DashboardController(DashboardDAO dao){this.dao=dao;}
    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request,Model model){
        HttpSession session=request.getSession(false);Object value=session==null?null:session.getAttribute("userId");
        if(!(value instanceof Number)){return "redirect:/login";}
        try{model.addAttribute("dashboardSummary",dao.getSummaryForUser(((Number)value).intValue()));model.addAttribute("userName",session.getAttribute("userName"));return "dashboard";}
        catch(SQLException e){throw new IllegalStateException("Unable to load the dashboard summary.",e);}
    }
}
