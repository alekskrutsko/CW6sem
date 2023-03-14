package com.cw6sem.controller;


import com.cw6sem.domain.Status;
import com.cw6sem.entity.AppraisalAgreement;
import com.cw6sem.entity.ObjectToAppraise;
import com.cw6sem.entity.User;
import com.cw6sem.service.*;
import com.cw6sem.validators.ErrorCase;
import com.cw6sem.validators.Validation;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.cw6sem.validators.ErrorCase.errorMsg;

@Controller
@AllArgsConstructor
@RequestMapping("/customer")
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;
    private final AppraiserService appraiserService;
    private final AppraisalAgreementService appraisalAgreementService;
    private final ObjectToAppraiseService objectToAppraiseService;

    @GetMapping("/customer-profile")
    public String profile(Model model){
        List<AppraisalAgreement> contracts = appraisalAgreementService.findAllByEmail(AuthController.loggedUser.getLogin());
        model.addAttribute("contracts",contracts);
        return "/customer/profile";
    }
    @GetMapping("sign/{id}")
    public String signContract(@PathVariable("id") Long id){
        Status status = appraisalAgreementService.findStatusById(id);
        if(status == Status.SIGNED){
            errorMsg = ErrorCase.contractSigned();
            return "redirect:/customer/sign-error";
        }
        if(status == Status.WAITFORAPPRAISER){
            errorMsg = ErrorCase.waitingForAppraiser();
            return "redirect:/customer/sign-error";
        }
        if(status == Status.TERMINATED){
            errorMsg = ErrorCase.contractTerminated();
            return "redirect:/customer/sign-error";
        }

        customerService.signContract(id);

        return "redirect:/customer/customer-profile";
    }

    @GetMapping("/sign-error")
    public String signError(Model model){
        List<AppraisalAgreement> contracts = appraisalAgreementService.findAllByEmail(AuthController.loggedUser.getLogin());
        model.addAttribute("contracts",contracts);
        model.addAttribute("error",errorMsg);
        return "/customer/profile";
    }

    @GetMapping("terminate/{id}")
    public String terminateContract(@PathVariable("id") Long id){
        Status status = appraisalAgreementService.findStatusById(id);
        if(status == Status.SIGNED){
            errorMsg = ErrorCase.contractSigned();
            return "redirect:/customer/sign-error";
        }
        if(status == Status.TERMINATED){
            errorMsg = ErrorCase.contractTerminated();
            return "redirect:/customer/sign-error";
        }
        customerService.terminateContract(id);
        return "redirect:/customer/customer-profile";
    }

    @GetMapping("/newContract")
    public String newContract(Model model, AppraisalAgreement appraisalAgreement){
        List<ObjectToAppraise> objectsToAppraise = objectToAppraiseService.findAll();
        model.addAttribute("objectsToAppraise",objectsToAppraise);
        return "/customer/newContract";
    }
    @PostMapping("/createContract")
    public String createContract(@RequestParam("login")String loggedUser,@RequestParam("expPrice") String expectedPrice, AppraisalAgreement appraisalAgreement, Model model){
        if(!Validation.validateDouble(expectedPrice)){
            List<ObjectToAppraise> objectsToAppraise = objectToAppraiseService.findAll();
            model.addAttribute("objectsToAppraise",objectsToAppraise);
            model.addAttribute("error",ErrorCase.errorDouble());
            return "/customer/newContract";
        }
        appraisalAgreement.setExpectedPrice(Double.parseDouble(expectedPrice));
        customerService.newContract(appraisalAgreement, loggedUser);
        return "redirect:/customer/newContract";
    }
    @GetMapping("/viewSearch")
    public String viewSearch(Model model, AppraisalAgreement appraisalAgreement){
        List<AppraisalAgreement> contracts = appraisalAgreementService.findAllByEmail(AuthController.loggedUser.getLogin());
        model.addAttribute("contracts",contracts);
        return "/customer/viewSearch";
    }
    @PostMapping("/search")
    public String search(@Param("str")String str, Model model){
        List<AppraisalAgreement> contracts = appraisalAgreementService.findAllSuitable(str);
        model.addAttribute("contracts",contracts);
        return "/customer/viewSearch";
    }
    @GetMapping("/personalInfo")
    public String personalInfo(Model model){
        User user = userService.findByLogin(AuthController.loggedUser.getLogin());
        model.addAttribute("user", user);
        return "/customer/personalInfo";
    }

    @GetMapping("/updateProfile-error")
    public String updateProfileError(User user, Model model){
        model.addAttribute("error",errorMsg);
        model.addAttribute("user",AuthController.loggedUser);
        return "/customer/personalInfo";
    }
    @PostMapping("/updatePersonalInfo")
    public String updatePersonalInfo(Model model, User user){
        Optional<User> us1 = Optional.ofNullable(userService.findByLogin(user.getLogin()));
        if(us1.isPresent()){
            if(us1.get().getLogin() == AuthController.loggedUser.getLogin()){
                errorMsg = ErrorCase.userExists();
                return "redirect:/customer/updateProfile-error";
            }

        }
        if(!Validation.validateString(user.getFirstName()) || !Validation.validateString(user.getSurname())
                || !Validation.validateString(user.getPatronymic())){
            errorMsg = ErrorCase.errorName();
            return "redirect:/customer/updateProfile-error";
        }
        if(!Validation.validatePhone(user.getPhone())){
            errorMsg = ErrorCase.errorPhone();
            return "redirect:/customer/updateProfile-error";
        }
        if(!Validation.validateEmail(user.getLogin())){
            errorMsg = ErrorCase.errorPersonalEmail();
            return "redirect:/customer/updateProfile-error";
        }
        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            if(!Validation.validatePassword(user.getPassword())) {
                errorMsg = ErrorCase.errorPassword();
                return "redirect:/customer/updateProfile-error";
            }
        }
        AuthController.loggedUser = userService.updateUser(user);

        model.addAttribute("user", AuthController.loggedUser);
        return "/customer/personalInfo";

    }
    @GetMapping("/feedback")
    public String feedback(Model model){
        List<User> appraisers = appraiserService.findAllAppraisers();
        model.addAttribute("appraisers",appraisers);
        return "/customer/feedback";
    }
    @PostMapping("/customerFeedback")
    public String customerFeedback(@RequestParam("login")String login, @RequestParam("feedback")String feedback){
        if(!StringUtils.isNullOrEmpty(feedback)){
            appraiserService.saveFeedback(login, feedback);
        }
        return "redirect:/customer/feedback";
    }

    @GetMapping("/statistic")
    public String statistic(Model model){
        model.addAttribute("chartData", customerService.getBarChart());
        return "/customer/statistic";
    }
}
