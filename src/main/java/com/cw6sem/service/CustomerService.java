package com.cw6sem.service;

import com.cw6sem.controller.AuthController;
import com.cw6sem.domain.Status;
import com.cw6sem.entity.AppraisalAgreement;
import com.cw6sem.entity.User;
import com.cw6sem.repository.AppraisalAgreementRepository;
import com.cw6sem.repository.ObjectToAppraiseRepository;
import com.cw6sem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {
    private final UserRepository userRepository;
    private final AppraisalAgreementRepository appraisalAgreementRepository;
    private final ObjectToAppraiseRepository objectsToAppraiseRepository;


    public void signContract(Long id){
        AppraisalAgreement appraisalAgreement = appraisalAgreementRepository.getById(id);
        LocalDate date = LocalDate.now();
        appraisalAgreement.setDateOfSigning(String.valueOf(date));
        appraisalAgreement.setStatus(Status.SIGNED);
        appraisalAgreementRepository.save(appraisalAgreement);
    }
    public void terminateContract(Long id){
        AppraisalAgreement appraisalAgreement = appraisalAgreementRepository.getById(id);
        appraisalAgreement.setStatus(Status.TERMINATED);
        appraisalAgreementRepository.save(appraisalAgreement);
    }
    public void newContract(AppraisalAgreement appraisalAgreement, String loggedUser){
        appraisalAgreement.setObjectToAppraise(objectsToAppraiseRepository.getById(appraisalAgreement.getObjectToAppraise().getId()));
        appraisalAgreement.setStatus(Status.WAITFORAPPRAISER);
        appraisalAgreement.setCustomer(userRepository.findByLogin(loggedUser));
        appraisalAgreementRepository.save(appraisalAgreement);
    }

    public List<List<Object>> getBarChart(){
        List<List<Object>> result =  appraisalAgreementRepository.barChart(AuthController.loggedUser.getId());
        return result;
    }
}
