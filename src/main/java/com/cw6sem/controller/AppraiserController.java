package com.cw6sem.controller;

import com.cw6sem.domain.Status;
import com.cw6sem.entity.AppraisalAgreement;
import com.cw6sem.entity.ObjectToAppraise;
import com.cw6sem.entity.ObjectType;
import com.cw6sem.entity.User;
import com.cw6sem.service.*;
import com.cw6sem.validators.ErrorCase;
import com.cw6sem.validators.Validation;
import static com.cw6sem.validators.ErrorCase.errorMsg;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;



@Controller
@RequestMapping("/appraiser")
@PreAuthorize("hasAuthority('APPRAISER')")
@AllArgsConstructor
public class AppraiserController {
    private final AppraiserService appraiserService;
    private final UserService userService;
    private final AppraisalAgreementService appraisalAgreementService;
    private final ObjectToAppraiseServiсe objectToAppraiseServiсe;
    private final ObjectTypeService objectTypeService;

    @GetMapping("/appraiser-profile")
    public String profile(Model model){
        List<AppraisalAgreement> contracts = appraisalAgreementService.findAll();
        model.addAttribute("contracts",contracts);
        return "/appraiser/profile";
    }

    @GetMapping("/process/{id}")
    public String processContract(@PathVariable("id") Long id, Model model){
        Status status = appraisalAgreementService.findStatusById(id);
        if(status == Status.SIGNED){
            errorMsg = ErrorCase.contractSigned();
            return "redirect:/appraiser/process-error";
        }
        if(status == Status.TERMINATED){
            errorMsg = ErrorCase.contractTerminated();
            return "redirect:/appraiser/process-error";
        }
        if(status == Status.WAITFORCUSTOMER){
            errorMsg = ErrorCase.contractWaitForCustomer();
            return "redirect:/appraiser/process-error";
        }

        AppraisalAgreement appraisalAgreement = appraisalAgreementService.countStatById(id);
        model.addAttribute("appraisalAgreement",appraisalAgreement);
        model.addAttribute("priceFromAppraiser", appraisalAgreement.getAppraiserPrice().toString());
        return "/appraiser/method";
    }

    @GetMapping("/process-error")
    public String processError(Model model){
        List<AppraisalAgreement> contracts = appraisalAgreementService.findAll();
        model.addAttribute("contracts",contracts);
        model.addAttribute("error",errorMsg);
        return "/appraiser/profile";
    }

    @GetMapping("/reject/{id}")
    public String rejectContract(@PathVariable("id") Long id){
        Status status = appraisalAgreementService.findStatusById(id);
        if(status == Status.SIGNED){
            errorMsg = ErrorCase.contractSigned();
            return "redirect:/appraiser/process-error";
        }
        if(status == Status.TERMINATED){
            errorMsg = ErrorCase.contractTerminated();
            return "redirect:/appraiser/process-error";
        }
        appraiserService.terminateContract(id);
        return "redirect:/appraiser/appraiser-profile";
    }

    @GetMapping("/filterContracts")
    public String filterContracts(@RequestParam("filter") String filter, Model model){
        List<AppraisalAgreement> contracts = null;
        if(filter.equals("all")){  contracts = appraisalAgreementService.findAll();}
        if(filter.equals("signed")){  contracts = appraiserService.filterCustomersByStatus(Status.SIGNED);}
        if(filter.equals("terminated")){contracts = appraiserService.filterCustomersByStatus(Status.TERMINATED);}
        if(filter.equals("waiting")){contracts = appraiserService.filterCustomersByStatus(Status.WAITFORAPPRAISER);}
        model.addAttribute("contracts",contracts);
        return "/appraiser/profile";
    }
    @GetMapping("/filterCustomers")
    public String filterCustomers(@RequestParam("filter") String filter, Model model){
        List<User> users = null;
        if(filter.equals("all")){  users = appraiserService.findAllCustomers();}
        if(filter.equals("active")){  users = appraiserService.filterCustomersByStatus(false);}
        if(filter.equals("locked")){users = appraiserService.filterCustomersByStatus(true);}
        model.addAttribute("users",users);
        return "/appraiser/lockUnlock";
    }

    @GetMapping("/personalInfo")
    public String personalInfo(Model model){
        User user = userService.findByLogin(AuthController.loggedUser.getLogin());
        model.addAttribute("user", user);
        return "/appraiser/personalInfo";
    }

    @GetMapping("/updateProfile-error")
    public String updateProfileError(Model model){
        model.addAttribute("error",errorMsg);
        model.addAttribute("user",AuthController.loggedUser);
        return "/appraiser/personalInfo";
    }
    @PostMapping("/updatePersonalInfo")
    public String updatePersonalInfo(Model model, User user){
        Optional<User> us1 = Optional.ofNullable(userService.findByLogin(user.getLogin()));
        if(us1.isPresent()){
            if(us1.get().getLogin() == AuthController.loggedUser.getLogin()){
                errorMsg = ErrorCase.userExists();
                return "redirect:/appraiser/updateProfile-error";
            }

        }
        if(!Validation.validateString(user.getFirstName()) || !Validation.validateString(user.getSurname())
                || !Validation.validateString(user.getPatronymic())){
            errorMsg = ErrorCase.errorName();
            return "redirect:/appraiser/updateProfile-error";
        }
        if(!Validation.validatePhone(user.getPhone())){
            errorMsg = ErrorCase.errorPhone();
            return "redirect:/appraiser/updateProfile-error";
        }
        if(!Validation.validateEmail(user.getLogin())){
            errorMsg = ErrorCase.errorPersonalEmail();
            return "redirect:/appraiser/updateProfile-error";
        }
        if(!StringUtils.isNullOrEmpty(user.getPassword())){
            if(!Validation.validatePassword(user.getPassword())) {
                errorMsg = ErrorCase.errorPassword();
                return "redirect:/appraiser/updateProfile-error";
            }
        }
        User us = userService.updateUser(user);

        model.addAttribute("user", us);
        return "/appraiser/personalInfo";

    }

    @GetMapping("/lockUnlock")
    public String lockUnlock(Model model){
        List<User> users = appraiserService.findAllCustomers();
        model.addAttribute("users",users);
        return "/appraiser/lockUnlock";
    }

    @GetMapping("block/{id}")
    public String block(@PathVariable("id") Long id){
        userService.userToggleBlocked(id);
        return "redirect:/appraiser/lockUnlock";
    }
    @GetMapping("/objectsToAppraise")
    public String objectsToAppraise(Model model){
        List<ObjectToAppraise> objectsToAppraise = objectToAppraiseServiсe.findAll();
        List<ObjectType> objectsTypes = objectTypeService.findAll();
        model.addAttribute("objectsToAppraise",objectsToAppraise);
        model.addAttribute("objectsTypes",objectsTypes);
        model.addAttribute("objectToAppraise",new ObjectToAppraise());
        model.addAttribute("objectType",new ObjectType());
        return "/appraiser/objectsToAppraise";
    }
    @PostMapping("/addType")
    public String addType(@RequestParam("type") String type){

        if(objectTypeService.getObjectTypeByType(type).isPresent()) {
            errorMsg = ErrorCase.suchTypeExists();
            return "redirect:/appraiser/type-error";
        }
        objectTypeService.saveByType(type);

        return "redirect:/appraiser/objectsToAppraise";
    }
    @PostMapping("/alterType")
    public String alterTypeType(ObjectType objectType){
      objectTypeService.updateType(objectType.getId(), objectType.getType());
      return "redirect:/appraiser/objectsToAppraise";
    }

    @PostMapping("/addObjectToAppraise")
    public String addObjectToAppraise(@ModelAttribute ObjectToAppraise objectToAppraise){
        Optional<ObjectToAppraise> q = Optional.ofNullable(objectToAppraiseServiсe.findByTypeAndDescription(objectToAppraise));
        if(q.isPresent()){
            errorMsg = ErrorCase.suchObjectExists();
            return "redirect:/appraiser/type-error";
        }
        objectToAppraiseServiсe.save(objectToAppraise);
        return "redirect:/appraiser/objectsToAppraise";
    }

    @PostMapping("/alterObject")
    public String alterObjectToAppraise(ObjectToAppraise objectToAppraise){
        objectToAppraiseServiсe.updateObject(objectToAppraise.getId(), objectToAppraise.getDescription());
        return "redirect:/appraiser/objectsToAppraise";
    }
    @GetMapping("/deleteType/{id}")
    public String deleteType(@PathVariable("id") Long id){
        if(objectTypeService.deleteType(id)){
            return "redirect:/appraiser/objectsToAppraise";
        }
        errorMsg = ErrorCase.thisTypeIsUsedInAnotherTable();
        return "redirect:/appraiser/type-error";
    }
    @GetMapping("/deleteObject/{id}")
    public String deleteObject(@PathVariable("id") Long id){
        if(objectToAppraiseServiсe.deleteObject(id)){
            return "redirect:/appraiser/objectsToAppraise";
        }
        errorMsg = ErrorCase.thisObjectIsUsedInAnotherTable();
        return "redirect:/appraiser/type-error";
    }

    @GetMapping("/type-error")
    public String typeError(Model model){
        model.addAttribute("error",errorMsg);
        List<ObjectToAppraise> objectsToAppraise = objectToAppraiseServiсe.findAll();
        List<ObjectType> objectsTypes = objectTypeService.findAll();
        model.addAttribute("objectsToAppraise",objectsToAppraise);
        model.addAttribute("objectsTypes",objectsTypes);
        model.addAttribute("objectToAppraise",new ObjectToAppraise());
        model.addAttribute("objectType",new ObjectType());
        return "/appraiser/objectsToAppraise";
    }
    @PostMapping("/statMetod")
    public String statMetod(AppraisalAgreement appraisalAgreement,@RequestParam("priceFromAppraiser") String appraiserPrice, @RequestParam("appraisalPrice") String priceForAppraisal, Model model){
        if(!Validation.validateDouble(appraiserPrice) || !Validation.validateDouble(priceForAppraisal)){
            AppraisalAgreement agreement = appraisalAgreementService.countStatById(appraisalAgreement.getId());
            model.addAttribute("appraisalAgreement", agreement);
            model.addAttribute("priceFromAppraiser", agreement.getAppraiserPrice().toString());
            model.addAttribute("error",ErrorCase.errorDouble());
            return "/appraiser/method";

        }
        appraisalAgreement.setAppraiserPrice(Double.parseDouble(appraiserPrice));
        appraisalAgreement.setPriceForAppraisal(Double.parseDouble(priceForAppraisal));
        appraisalAgreementService.saveStatMethod(appraisalAgreement);
        return "redirect:/appraiser/appraiser-profile";
    }
    @PostMapping("/profitMetod")
    public String profitMetod(AppraisalAgreement appraisalAgreement, @RequestParam("income") String income, @RequestParam("capital") String capital, @RequestParam("appraisalPrice") String priceForAppraisal, Model model){
        if(!Validation.validateDouble(income) || !Validation.validateDouble(capital) || !Validation.validateDouble(priceForAppraisal)){
            AppraisalAgreement agreement = appraisalAgreementService.countStatById(appraisalAgreement.getId());
            model.addAttribute("appraisalAgreement", agreement);
            model.addAttribute("priceFromAppraiser", agreement.getAppraiserPrice().toString());
            model.addAttribute("error",ErrorCase.errorDouble());
            return "/appraiser/method";

        }
        appraisalAgreement.setPriceForAppraisal(Double.parseDouble(priceForAppraisal));
        appraisalAgreementService.saveProfitMethod(appraisalAgreement, income, capital);
        return "redirect:/appraiser/appraiser-profile";
    }

}
