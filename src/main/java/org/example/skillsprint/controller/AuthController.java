package org.example.skillsprint.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.skillsprint.dao.UserDAO;
import org.example.skillsprint.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;
import java.util.regex.Pattern;

@Controller
public class AuthController {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserDAO users;

    public AuthController(UserDAO users) { this.users = users; }

    @GetMapping("/register")
    public String registerForm() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam(name="fullName", defaultValue="") String fullName,
                           @RequestParam(name="email", defaultValue="") String email,
                           @RequestParam(name="college", defaultValue="") String college,
                           @RequestParam(name="branch", defaultValue="") String branch,
                           @RequestParam(name="githubUrl", defaultValue="") String githubUrl,
                           @RequestParam(name="linkedinUrl", defaultValue="") String linkedinUrl,
                           @RequestParam(name="password", required=false) String password,
                           @RequestParam(name="confirmPassword", required=false) String confirmPassword,
                           Model model, HttpServletRequest request) {
        fullName=fullName.trim(); email=email.trim().toLowerCase(Locale.ROOT);
        college=college.trim(); branch=branch.trim(); githubUrl=githubUrl.trim(); linkedinUrl=linkedinUrl.trim();
        String error = validateRegistration(fullName,email,password,confirmPassword,college,branch,githubUrl,linkedinUrl);
        if(error!=null){ model.addAttribute("error",error); return "register"; }
        try {
            if(users.emailExists(email)){ model.addAttribute("error","An account with this email already exists."); return "register"; }
            User user=new User(fullName,email,hashPassword(password),empty(college),empty(branch),empty(githubUrl),empty(linkedinUrl));
            if(!users.createUser(user)){ model.addAttribute("error","An account with this email already exists."); return "register"; }
            model.addAttribute("success","Registration successful. You can now continue to login.");
        } catch(SQLException | GeneralSecurityException e){
            request.getServletContext().log("Registration could not be completed.",e);
            model.addAttribute("error","Registration could not be completed right now. Please try again.");
        }
        return "register";
    }

    @GetMapping("/login")
    public String loginForm() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam(name="email", defaultValue="") String email,
                        @RequestParam(name="password", required=false) String password,
                        Model model, HttpServletRequest request) {
        email=email.trim().toLowerCase(Locale.ROOT);
        if(email.isEmpty()||password==null||password.isEmpty()||email.length()>100||!EMAIL.matcher(email).matches()){
            model.addAttribute("message","Invalid email or password"); model.addAttribute("success",false); return "login";
        }
        try {
            User user=users.findByEmail(email);
            if(user==null||!verifyPassword(password,user.getPasswordHash())){
                model.addAttribute("message","Invalid email or password"); model.addAttribute("success",false); return "login";
            }
            HttpSession session=request.getSession(false);
            if(session==null) session=request.getSession(); else request.changeSessionId();
            session.setAttribute("userId",user.getUserId()); session.setAttribute("userName",user.getFullName());
            return "redirect:/dashboard";
        } catch(SQLException|GeneralSecurityException|IllegalArgumentException e){
            request.getServletContext().log("Login could not be completed.",e);
            model.addAttribute("message","Login could not be completed right now. Please try again.");
            model.addAttribute("success",false); return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        HttpSession session=request.getSession(false); if(session!=null) session.invalidate();
        return "redirect:/login";
    }

    private String validateRegistration(String name,String email,String password,String confirm,String college,String branch,String github,String linkedin){
        if(name.isEmpty()) return "Full name is required.";
        if(name.length()>100) return "Full name must be 100 characters or fewer.";
        if(email.isEmpty()) return "Email is required.";
        if(email.length()>100||!EMAIL.matcher(email).matches()) return "Enter a valid email address.";
        if(password==null||password.isEmpty()) return "Password is required.";
        if(password.length()<8) return "Password must be at least 8 characters long.";
        if(password.length()>128) return "Password must be 128 characters or fewer.";
        if(confirm==null||!password.equals(confirm)) return "Password and confirm password must match.";
        if(college.length()>150) return "College must be 150 characters or fewer.";
        if(branch.length()>100) return "Branch must be 100 characters or fewer.";
        if(!validUrl(github)) return "Enter a valid GitHub URL beginning with http:// or https://.";
        if(!validUrl(linkedin)) return "Enter a valid LinkedIn URL beginning with http:// or https://.";
        return null;
    }
    private boolean validUrl(String value){
        if(value.isEmpty()) return true; if(value.length()>255) return false;
        try{URI u=URI.create(value);String s=u.getScheme();return u.isAbsolute()&&u.getHost()!=null&&("http".equalsIgnoreCase(s)||"https".equalsIgnoreCase(s));}
        catch(IllegalArgumentException e){return false;}
    }
    private String hashPassword(String password)throws GeneralSecurityException{
        byte[] salt=new byte[16];RANDOM.nextBytes(salt);PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,210000,256);
        try{return "pbkdf2-sha256$210000$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());}
        finally{spec.clearPassword();}
    }
    private boolean verifyPassword(String password,String encoded)throws GeneralSecurityException{
        if(encoded==null)return false;String[] p=encoded.split("\\$",-1);if(p.length!=4||!"pbkdf2-sha256".equals(p[0]))return false;
        int iterations=Integer.parseInt(p[1]);byte[] salt=Base64.getDecoder().decode(p[2]),expected=Base64.getDecoder().decode(p[3]);
        if(iterations<1||salt.length==0||expected.length==0)return false;
        PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,iterations,expected.length*8);
        try{return MessageDigest.isEqual(expected,SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded());}
        finally{spec.clearPassword();}
    }
    private String empty(String value){return value.isEmpty()?null:value;}
}
