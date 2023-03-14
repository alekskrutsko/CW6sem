package com.cw6sem.controller;


import com.cw6sem.domain.Role;
import com.cw6sem.entity.AppraisalAgreement;
import com.cw6sem.entity.Appraiser;
import com.cw6sem.entity.User;
import com.cw6sem.service.AdminService;
import com.cw6sem.service.AppraisalAgreementService;
import com.cw6sem.service.UserService;
import com.cw6sem.validators.ErrorCase;
import com.cw6sem.validators.Validation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static com.cw6sem.validators.ErrorCase.errorMsg;

@Controller
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AppraisalAgreementService appraisalAgreementService;
    private final AdminService adminService;
    public static User loggedUser;

    @GetMapping("/")
    public String authForm(User customer){
        return "/authorization/auth";
    }

    @PostMapping("/mainWindow")
    public String mainWindow(User user, Model model){
        User us = userService.findByEmailAndPassword(user.getLogin(),user.getPassword());
        if(us!=null && us.getRole() == Role.ADMINISTRATOR) {
            loggedUser = us;
            List<User> users = adminService.findCustomersAndAppraisers();
            List<Appraiser> appraisers = adminService.findAppraisersFeedback();
            model.addAttribute("users",users);
            model.addAttribute("appraisers",appraisers);
            return "/admin/profile";
        }
        else if(us!=null && us.getRole() == Role.APPRAISER) {
            loggedUser = us;
            List<AppraisalAgreement> contracts = appraisalAgreementService.findAll();
            model.addAttribute("contracts",contracts);
            return "/appraiser/profile";
        }
        else if(us!=null && us.getRole()  == Role.CUSTOMER){
            loggedUser = us;
            List<AppraisalAgreement> contracts = appraisalAgreementService.findAllByEmail(us.getLogin());
            model.addAttribute("contracts",contracts);
            return "/customer/profile";
        }
        errorMsg = ErrorCase.errorAuthorization();
        return "redirect:/auth-error";
    }
    @GetMapping("/auth-error")
    public String error(User user, Model model){
        model.addAttribute("error",errorMsg);
        return "/authorization/auth";
    }


    @PostMapping("/register")
    public String register(User user){
        if(userService.findByLogin(user.getLogin() )!= null){
            errorMsg = ErrorCase.userExists();
            return "redirect:/auth-error";
        }
        if(!Validation.validateString(user.getFirstName()) || !Validation.validateString(user.getSurname())
                || !Validation.validateString(user.getPatronymic())){
            errorMsg = ErrorCase.errorName();
            return "redirect:/auth-error";
        }
        if(!Validation.validatePhone(user.getPhone())){
            errorMsg = ErrorCase.errorPhone();
            return "redirect:/auth-error";
        }
        if(!Validation.validateEmail(user.getLogin())){
            errorMsg = ErrorCase.errorEmail();
            return "redirect:/auth-error";
        }
        if(!Validation.validatePassword(user.getPassword())){
            errorMsg = ErrorCase.errorPassword();
            return "redirect:/auth-error";
        }
        user.setRole(Role.CUSTOMER);
        userService.saveUser(user);
        loggedUser = user;
        return "/customer/profile";
    }
}
