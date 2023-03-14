package com.cw6sem.controller;

import com.cw6sem.domain.Role;
import com.cw6sem.entity.Appraiser;
import com.cw6sem.entity.User;
import com.cw6sem.service.AdminService;
import com.cw6sem.service.UserService;
import com.cw6sem.validators.ErrorCase;
import com.cw6sem.validators.Validation;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.cw6sem.validators.ErrorCase.errorMsg;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMINISTRATOR')")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/admin-profile")
    public String profile(Model model){
        List<User> users = adminService.findCustomersAndAppraisers();
        List<Appraiser> appraisers = adminService.findAppraisersFeedback();
        model.addAttribute("users",users);
        model.addAttribute("appraisers",appraisers);
        return "/admin/profile";
    }
    @GetMapping("/filterUsers")
    public String filterUsers(@RequestParam("filter") String filter, Model model){
        List<User> users = null;
        List<Appraiser> appraisers = adminService.findAppraisersFeedback();
        if(filter.equals("all")){  users = adminService.findCustomersAndAppraisers();}
        if(filter.equals("appraiser")){  users = adminService.findAllAppraisers();}
        if(filter.equals("customer")){users = adminService.findAllCustomers();}
        model.addAttribute("users",users);
        model.addAttribute("appraisers",appraisers);
        return "/admin/profile";
    }
    @GetMapping("/grantAppraiser/{id}")
    public String grantAppraiser(@PathVariable("id") Long id){
        User user = adminService.getUserById(id);
        if(user.getRole() == Role.APPRAISER){
            errorMsg = ErrorCase.userAlreadyAppraiser();
            return "redirect:/admin/grant-error";
        }
        adminService.grantAppraiser(id);
        return "redirect:/admin/admin-profile";
    }
    @GetMapping("/grantCustomer/{id}")
    public String grantCustomer(@PathVariable("id") Long id){
        User user = adminService.getUserById(id);
        if(user.getRole() == Role.CUSTOMER){
            errorMsg = ErrorCase.userAlreadyCustomer();
            return "redirect:/admin/grant-error";
        }
        adminService.grantCustomer(id);
        return "redirect:/admin/admin-profile";
    }
    @GetMapping("/grant-error")
    public String grantError(Model model){
        List<User> users = adminService.findCustomersAndAppraisers();
        List<Appraiser> appraisers = adminService.findAppraisersFeedback();
        model.addAttribute("users",users);
        model.addAttribute("appraisers",appraisers);
        model.addAttribute("error",errorMsg);
        return "/admin/profile";
    }

    @GetMapping("/personalInfo")
    public String personalInfo(Model model){
        User user = userService.findByLogin(AuthController.loggedUser.getLogin());
        model.addAttribute("user", user);
        return "/admin/personalInfo";
    }

    @GetMapping("/updateProfile-error")
    public String updateProfileError(Model model){
        model.addAttribute("error",errorMsg);
        model.addAttribute("user",AuthController.loggedUser);
        return "/admin/personalInfo";
    }
    @PostMapping("/updatePersonalInfo")
    public String updatePersonalInfo(Model model, User user){
        Optional<User> us1 = Optional.ofNullable(userService.findByLogin(user.getLogin()));
        if(us1.isPresent()){
            if(us1.get().getLogin() == AuthController.loggedUser.getLogin()){
                errorMsg = ErrorCase.userExists();
                return "redirect:/admin/updateProfile-error";
            }

        }
        if(!Validation.validateString(user.getFirstName()) || !Validation.validateString(user.getSurname())
                || !Validation.validateString(user.getPatronymic())){
            errorMsg = ErrorCase.errorName();
            return "redirect:/admin/updateProfile-error";
        }
        if(!Validation.validatePhone(user.getPhone())){
            errorMsg = ErrorCase.errorPhone();
            return "redirect:/admin/updateProfile-error";
        }
        if(!Validation.validateEmail(user.getLogin())){
            errorMsg = ErrorCase.errorPersonalEmail();
            return "redirect:/admin/updateProfile-error";
        }
        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            if(!Validation.validatePassword(user.getPassword())) {
                errorMsg = ErrorCase.errorPassword();
                return "redirect:/admin/updateProfile-error";
            }
        }
        User us = userService.updateUser(user);

        model.addAttribute("user", us);
        return "/admin/personalInfo";

    }
    @GetMapping("/lockUnlock")
    public String lockUnlock(Model model){
        List<User> users = adminService.findCustomersAndAppraisers();
        model.addAttribute("users",users);
        return "/admin/lockUnlock";
    }

    @GetMapping("block/{id}")
    public String block(@PathVariable("id") Long id){
        userService.userToggleBlocked(id);
        return "redirect:/admin/lockUnlock";
    }
    @GetMapping("/filterBlockedUsers")
    public String filterBlockedUsers(@RequestParam("filter") String filter, Model model){
        List<User> users = null;
        if(filter.equals("all")){
            users = adminService.findCustomersAndAppraisers();
        }
        if(filter.equals("active")){
            users = adminService.filterCustomersAndAppraisersByStatus(false);
        }
        if(filter.equals("locked")){
            users = adminService.filterCustomersAndAppraisersByStatus(true);
        }
        model.addAttribute("users",users);
        return "/admin/lockUnlock";
    }
    @GetMapping("/statistic")
    public String statistic(Model model){
        String appraiser = adminService.getBestAppraisers();
        String[] str = new String[2];
        if(appraiser.contains("#")){
            str = appraiser.split("#");
        }else{
            str[0] = appraiser;
            str[1] = "";
        }
        model.addAttribute("appraisers", str);
        model.addAttribute("chartData", adminService.getChartDataForSixMonth());
        return "/admin/statistic";
    }
    @GetMapping("/report")
    public String report(Model model){
        adminService.createReport();
        String appraiser = adminService.getBestAppraisers();
        String[] str = new String[2];
        if(appraiser.contains("#")){
            str = appraiser.split("#");
        }else{
            str[0] = appraiser;
            str[1] = "";
        }
        model.addAttribute("appraisers", str);
        model.addAttribute("chartData", adminService.getChartDataForSixMonth());
        model.addAttribute("report", "Отчет сохранён в корневую папку проекта");
        return "/admin/statistic";
    }
}
