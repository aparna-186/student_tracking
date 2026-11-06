package org.example.skillsprint.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.InternshipApplicationDAO;
import org.example.skillsprint.model.InternshipApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/applications")
public class InternshipApplicationController {
    private static final List<String> STATUSES=Arrays.asList("Applied","Online Assessment","Interview","Offer","Rejected","Withdrawn");
    private final InternshipApplicationDAO dao;
    public InternshipApplicationController(InternshipApplicationDAO dao){this.dao=dao;}

    @GetMapping({"","/","/list"})
    public String list(@RequestParam(name="company", required=false) String company,@RequestParam(name="status", required=false) String status,Model model,HttpServletResponse response,HttpServletRequest request) throws IOException {
        if(status!=null&&!status.isEmpty()&&!STATUSES.contains(status))status="";
        try{model.addAttribute("applications",dao.listForUser(currentUserId(request),company,status));model.addAttribute("statuses",STATUSES);return "application-list";}
        catch(SQLException|IllegalStateException e){return error(model,response,"The application could not be saved or loaded. Please try again.");}
    }
    @GetMapping("/new") public String newForm(Model model){return form(model,new InternshipApplication(),false,null);}
    @GetMapping("/edit") public String edit(@RequestParam("id") String id,Model model,HttpServletResponse response,HttpServletRequest request) throws IOException {
        Integer parsed=parseId(id);if(parsed==null){response.sendError(400,"Invalid application ID.");return null;}
        try{InternshipApplication a=dao.findForUser(parsed,currentUserId(request));if(a==null){response.sendError(404,"Application not found.");return null;}return form(model,a,true,null);}
        catch(SQLException e){return error(model,response,"The application could not be saved or loaded. Please try again.");}
    }
    @PostMapping("/create") public String create(@RequestParam(name="companyName",defaultValue="") String companyName,@RequestParam(name="role",defaultValue="") String role,@RequestParam(name="applicationReference",defaultValue="") String applicationReference,@RequestParam(name="appliedDate",required=false) String appliedDate,@RequestParam(name="status",defaultValue="") String status,@RequestParam(name="notes",defaultValue="") String notes,Model model,HttpServletResponse response,HttpServletRequest request) throws IOException {return save(0,false,companyName,role,applicationReference,appliedDate,status,notes,model,response,request);}
    @PostMapping("/update") public String update(@RequestParam("id") String id,@RequestParam(name="companyName",defaultValue="") String companyName,@RequestParam(name="role",defaultValue="") String role,@RequestParam(name="applicationReference",defaultValue="") String applicationReference,@RequestParam(name="appliedDate",required=false) String appliedDate,@RequestParam(name="status",defaultValue="") String status,@RequestParam(name="notes",defaultValue="") String notes,Model model,HttpServletResponse response,HttpServletRequest request) throws IOException {Integer parsed=parseId(id);if(parsed==null){response.sendError(400,"Invalid application ID.");return null;}return save(parsed,true,companyName,role,applicationReference,appliedDate,status,notes,model,response,request);}
    @PostMapping("/delete") public String delete(@RequestParam("id") String id,Model model,HttpServletResponse response,HttpServletRequest request) throws IOException {Integer parsed=parseId(id);if(parsed==null){response.sendError(400,"Invalid application ID.");return null;}try{if(!dao.delete(parsed,currentUserId(request))){response.sendError(404,"Application not found.");return null;}return "redirect:/applications/list";}catch(SQLException e){return error(model,response,"The application could not be saved or loaded. Please try again.");}}

    private String save(int id,boolean edit,String company,String role,String ref,String rawDate,String status,String notes,Model model,HttpServletResponse response,HttpServletRequest request) throws IOException {
        int uid=currentUserId(request);InternshipApplication a=new InternshipApplication();a.setApplicationId(id);a.setUserId(uid);a.setCompanyName(company.trim());a.setRole(role.trim());a.setApplicationReference(ref.trim());a.setStatus(status.trim());a.setNotes(notes.trim());
        String d=rawDate==null?"":rawDate.trim();if(!d.isEmpty())try{a.setAppliedDate(Date.valueOf(LocalDate.parse(d)));}catch(DateTimeParseException ignored){}
        String err=validate(a,rawDate);if(err!=null)return form(model,a,edit,err);
        try{if(edit){if(!dao.update(a,uid)){response.sendError(404,"Application not found.");return null;}}else dao.create(a);return "redirect:/applications/list";}
        catch(SQLException e){return error(model,response,"The application could not be saved or loaded. Please try again.");}
    }
    private String validate(InternshipApplication a,String raw){if(a.getCompanyName().isEmpty()||a.getRole().isEmpty())return "Company name and role are required.";if(a.getCompanyName().length()>100||a.getRole().length()>100)return "Company name and role must be 100 characters or fewer.";if(len(a.getApplicationReference())>100)return "External reference must be 100 characters or fewer.";if(len(a.getNotes())>500)return "Notes must be 500 characters or fewer.";if(!STATUSES.contains(a.getStatus()))return "Choose a valid application status.";if(raw!=null&&!raw.trim().isEmpty()&&a.getAppliedDate()==null)return "Enter a valid applied date.";return null;}
    private String form(Model m,InternshipApplication a,boolean edit,String error){m.addAttribute("application",a);m.addAttribute("editMode",edit);m.addAttribute("statuses",STATUSES);m.addAttribute("error",error);return "application-form";}
    private String error(Model m,HttpServletResponse r,String message){r.setStatus(500);m.addAttribute("error",message);return "application-error";}
    private int currentUserId(HttpServletRequest request){HttpSession s=request.getSession(false);Object v=s==null?null:s.getAttribute("userId");return v instanceof Number?((Number)v).intValue():0;}
    private Integer parseId(String s){try{return s!=null&&s.matches("[1-9][0-9]{0,9}")?Integer.valueOf(s):null;}catch(NumberFormatException e){return null;}}
    private int len(String s){return s==null?0:s.length();}
}
