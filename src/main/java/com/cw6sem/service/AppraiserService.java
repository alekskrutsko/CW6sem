package com.cw6sem.service;

import com.cw6sem.controller.AuthController;
import com.cw6sem.domain.Role;
import com.cw6sem.domain.Status;
import com.cw6sem.entity.AppraisalAgreement;
import com.cw6sem.entity.Appraiser;
import com.cw6sem.entity.User;
import com.cw6sem.repository.AppraisalAgreementRepository;
import com.cw6sem.repository.AppraiserRepository;
import com.cw6sem.repository.UserRepository;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppraiserService {
    private final AppraiserRepository appraiserRepository;
    private final UserRepository userRepository;
    private final AppraisalAgreementRepository appraisalAgreementRepository;
    public List<User> findAllAppraisers(){
        return userRepository.findByRole(Role.APPRAISER);
    }
    public void saveFeedback(String login, String feedback){
        Appraiser appraiser = new Appraiser();
        appraiser.setUser(userRepository.findByLogin(login));
        appraiser.setFeedback(feedback);
        appraiserRepository.save(appraiser);
    }
    public List<AppraisalAgreement> filterCustomersByStatus(Status status){
        List<AppraisalAgreement> list = appraisalAgreementRepository.filterByStatus(status);
        return list.stream()
                .map(temp -> {
                    AppraisalAgreement obj = temp;
                    obj.setStrStatus(temp.getStatus());
                    return obj;
                }).collect(Collectors.toList());
    }
    public List<User> filterCustomersByStatus(boolean blocked){
        return userRepository.filterCustomersByStatus(blocked);
    }

    public List<User> findAllCustomers(){
        return userRepository.findByRole(Role.CUSTOMER);
    }

    public void terminateContract(Long id){
        AppraisalAgreement appraisalAgreement = appraisalAgreementRepository.getById(id);
        appraisalAgreement.setStatus(Status.TERMINATED);
        appraisalAgreementRepository.save(appraisalAgreement);
    }


}
