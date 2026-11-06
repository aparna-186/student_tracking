package org.example.skillsprint.controller;

import jakarta.servlet.http.HttpServletRequest;import jakarta.servlet.http.HttpSession;import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SessionController {
    @GetMapping("/session-test") public String sessionTest(HttpServletRequest request,Model model){HttpSession s=request.getSession(false);if(s!=null){Object id=s.getAttribute("userId"),name=s.getAttribute("userName");if(id!=null&&name!=null){model.addAttribute("userId",id);model.addAttribute("userName",name);model.addAttribute("sessionValid",true);}}return "session-test";}
}
