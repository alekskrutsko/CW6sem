package com.cw6sem.service;

import com.cw6sem.controller.AuthController;
import com.cw6sem.domain.Status;
import com.cw6sem.entity.AppraisalAgreement;
import com.cw6sem.entity.Appraiser;
import com.cw6sem.repository.AppraisalAgreementRepository;
import com.cw6sem.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppraisalAgreementService {
    private final AppraisalAgreementRepository appraisalAgreementRepository;
    private final UserRepository userRepository;

    public List<AppraisalAgreement> findAllByEmail(String email){
        List<AppraisalAgreement> list = appraisalAgreementRepository.findAllByUser(email);
        return list.stream()
                .map(temp -> {
                    AppraisalAgreement obj = temp;
                    obj.setStrStatus(temp.getStatus());
                    return obj;
                }).collect(Collectors.toList());
    }
   public List<AppraisalAgreement> findAll(){
       List<AppraisalAgreement> list = appraisalAgreementRepository.findAll();
       return list.stream()
               .map(temp -> {
                   AppraisalAgreement obj = temp;
                   obj.setStrStatus(temp.getStatus());
                   return obj;
               }).collect(Collectors.toList());
   }

   public Status findStatusById(Long id){
        return appraisalAgreementRepository.findStatusById(id);
    }

    public List<AppraisalAgreement> findAllSuitable(String str){
        return appraisalAgreementRepository.findAllSuitable(str);
    }

    public AppraisalAgreement countStatById(Long id){
        AppraisalAgreement appraisalAgreement = appraisalAgreementRepository.getById(id);
        appraisalAgreement.setAppraiserPrice(appraisalAgreementRepository.countStat(appraisalAgreement.getObjectToAppraise().getDescription(),
                appraisalAgreement.getObjectToAppraise().getObjectType().getType()));
        return appraisalAgreement;
    }

    public void saveStatMethod(AppraisalAgreement appraisalAgreement){
        AppraisalAgreement appraisalAgreement1 = appraisalAgreementRepository.getById(appraisalAgreement.getId());
        Appraiser appraiser = new Appraiser();
        appraiser.setUser(userRepository.findByLogin(AuthController.loggedUser.getLogin()));
        appraisalAgreement1.setAppraiser(appraiser);
        appraisalAgreement1.setCommentFromAppraiser(appraisalAgreement.getCommentFromAppraiser());
        appraisalAgreement1.setAppraiserPrice(appraisalAgreement.getAppraiserPrice());
        appraisalAgreement1.setPriceForAppraisal(appraisalAgreement.getPriceForAppraisal());
        appraisalAgreement1.setStatus(Status.WAITFORCUSTOMER);
        appraisalAgreementRepository.save(appraisalAgreement1);
    }
    public void saveProfitMethod(AppraisalAgreement appraisalAgreement, String income, String capital){
        Double price = Double.parseDouble(income)/(Double.parseDouble(capital)/100);
        AppraisalAgreement appraisalAgreement1 = appraisalAgreementRepository.getById(appraisalAgreement.getId());
        Appraiser appraiser = new Appraiser();
        appraiser.setUser(userRepository.findByLogin(AuthController.loggedUser.getLogin()));
        appraisalAgreement1.setAppraiser(appraiser);
        appraisalAgreement1.setCommentFromAppraiser(appraisalAgreement.getCommentFromAppraiser());
        appraisalAgreement1.setPriceForAppraisal(appraisalAgreement.getPriceForAppraisal());
        appraisalAgreement1.setStatus(Status.WAITFORCUSTOMER);
        appraisalAgreement1.setAppraiserPrice(price);

        appraisalAgreementRepository.save(appraisalAgreement1);
    }
}
