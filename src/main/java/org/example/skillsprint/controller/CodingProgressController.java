package org.example.skillsprint.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.CodingProgressDAO;
import org.example.skillsprint.model.CodingProgress;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

@Controller @RequestMapping("/coding")
public class CodingProgressController {
    private static final List<String> DIFFICULTIES=Arrays.asList("Easy","Medium","Hard"),STATUSES=Arrays.asList("Solved","Attempted","Unsolved");
    private final CodingProgressDAO dao;
    public CodingProgressController(CodingProgressDAO dao){this.dao=dao;}
    @GetMapping({"","/","/list"}) public String list(@RequestParam(name="platform",required=false) String platform,@RequestParam(name="difficulty",required=false) String difficulty,@RequestParam(name="status",required=false) String status,Model m,HttpServletRequest req,HttpServletResponse res){
        if(difficulty!=null&&!difficulty.isEmpty()&&!DIFFICULTIES.contains(difficulty))difficulty="";if(status!=null&&!status.isEmpty()&&!STATUSES.contains(status))status="";platform=trim(platform);if(platform.length()>50)platform=platform.substring(0,50);
        try{m.addAttribute("records",dao.listForUser(uid(req),platform,difficulty,status));m.addAttribute("difficulties",DIFFICULTIES);m.addAttribute("statuses",STATUSES);return "coding-list";}catch(SQLException e){return error(m,res);}}
    @GetMapping("/new") public String newForm(Model m){CodingProgress c=new CodingProgress();return form(m,c,false,null,null);}
    @GetMapping("/edit") public String edit(@RequestParam("id") String id,Model m,HttpServletRequest req,HttpServletResponse res) throws IOException {Integer i=parse(id);if(i==null){res.sendError(400,"Invalid coding record ID.");return null;}try{CodingProgress c=dao.findForUser(i,uid(req));if(c==null){res.sendError(404,"Coding record not found.");return null;}return form(m,c,true,null,null);}catch(SQLException e){return error(m,res);}}
    @PostMapping("/create") public String create(@RequestParam(name="platform",defaultValue="") String platform,@RequestParam(name="problemName",defaultValue="") String problemName,@RequestParam(name="difficulty",defaultValue="") String difficulty,@RequestParam(name="problemStatus",defaultValue="") String problemStatus,@RequestParam(name="solvedDate",required=false) String solvedDate,@RequestParam(name="problemUrl",defaultValue="") String problemUrl,Model m,HttpServletRequest req,HttpServletResponse res) throws IOException {return save(0,false,platform,problemName,difficulty,problemStatus,solvedDate,problemUrl,m,req,res);}
    @PostMapping("/update") public String update(@RequestParam("id") String id,@RequestParam(name="platform",defaultValue="") String platform,@RequestParam(name="problemName",defaultValue="") String problemName,@RequestParam(name="difficulty",defaultValue="") String difficulty,@RequestParam(name="problemStatus",defaultValue="") String problemStatus,@RequestParam(name="solvedDate",required=false) String solvedDate,@RequestParam(name="problemUrl",defaultValue="") String problemUrl,Model m,HttpServletRequest req,HttpServletResponse res) throws IOException {Integer i=parse(id);if(i==null){res.sendError(400,"Invalid coding record ID.");return null;}return save(i,true,platform,problemName,difficulty,problemStatus,solvedDate,problemUrl,m,req,res);}
    @PostMapping("/delete") public String delete(@RequestParam("id") String id,HttpServletRequest req,HttpServletResponse res,Model m) throws IOException {Integer i=parse(id);if(i==null){res.sendError(400,"Invalid coding record ID.");return null;}try{if(!dao.delete(i,uid(req))){res.sendError(404,"Coding record not found.");return null;}return "redirect:/coding/list";}catch(SQLException e){return error(m,res);}}
    private String save(int id,boolean edit,String platform,String name,String difficulty,String status,String rawDate,String url,Model m,HttpServletRequest req,HttpServletResponse res) throws IOException {int user=uid(req);CodingProgress c=new CodingProgress();c.setCodingId(id);c.setUserId(user);c.setPlatform(trim(platform));c.setProblemName(trim(name));c.setDifficulty(trim(difficulty));c.setProblemStatus(trim(status));c.setProblemUrl(trim(url));String d=trim(rawDate);if(!d.isEmpty())try{c.setSolvedDate(Date.valueOf(LocalDate.parse(d)));}catch(DateTimeParseException ignored){}
        String e=validate(c,rawDate);if(e!=null)return form(m,c,edit,e,rawDate);try{if(edit){if(!dao.update(c,user)){res.sendError(404,"Coding record not found.");return null;}}else dao.create(c);return "redirect:/coding/list";}catch(SQLException ex){return error(m,res);}}
    private String validate(CodingProgress c,String raw){if(c.getProblemName().isEmpty())return "Problem name is required.";if(c.getProblemName().length()>150)return "Problem name must be 150 characters or fewer.";if(c.getPlatform().length()>50)return "Platform must be 50 characters or fewer.";if(!DIFFICULTIES.contains(c.getDifficulty()))return "Choose Easy, Medium or Hard for difficulty.";if(!STATUSES.contains(c.getProblemStatus()))return "Choose a valid problem status.";if(raw!=null&&!raw.trim().isEmpty()&&c.getSolvedDate()==null)return "Enter a valid solved date.";if(c.getProblemUrl().length()>255)return "Problem URL must be 255 characters or fewer.";if(!c.getProblemUrl().isEmpty()&&!url(c.getProblemUrl()))return "Problem URL must be a valid http or https URL.";return null;}
    private boolean url(String s){try{URI u=new URI(s);return u.getScheme()!=null&&(u.getScheme().equalsIgnoreCase("http")||u.getScheme().equalsIgnoreCase("https"))&&u.getHost()!=null&&!u.getHost().isEmpty();}catch(Exception e){return false;}}
    private String form(Model m,CodingProgress c,boolean edit,String error,String raw){m.addAttribute("record",c);m.addAttribute("editMode",edit);m.addAttribute("difficulties",DIFFICULTIES);m.addAttribute("statuses",STATUSES);m.addAttribute("error",error);m.addAttribute("rawDate",raw);return "coding-form";}
    private String error(Model m,HttpServletResponse r){r.setStatus(500);m.addAttribute("error","The coding record could not be saved or loaded. Please try again.");return "coding-error";}
    private int uid(HttpServletRequest r){HttpSession s=r.getSession(false);Object v=s==null?null:s.getAttribute("userId");return v instanceof Number?((Number)v).intValue():0;}
    private Integer parse(String s){try{return s!=null&&s.matches("[1-9][0-9]{0,9}")?Integer.valueOf(s):null;}catch(NumberFormatException e){return null;}}
    private String trim(String s){return s==null?"":s.trim();}
}
