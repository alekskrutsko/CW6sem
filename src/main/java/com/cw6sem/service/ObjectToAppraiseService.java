package com.cw6sem.service;

import com.cw6sem.entity.ObjectToAppraise;
import com.cw6sem.repository.AppraisalAgreementRepository;
import com.cw6sem.repository.ObjectToAppraiseRepository;
import com.cw6sem.repository.ObjectTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ObjectToAppraiseService {
    private  final ObjectToAppraiseRepository objectToAppraiseRepository;
    private final ObjectTypeRepository objectTypeRepository;
    private final AppraisalAgreementRepository appraisalAgreementRepository;

    public List<ObjectToAppraise> findAll(){
        return objectToAppraiseRepository.findAll();
    }
    public void save(ObjectToAppraise objectToAppraise){
        objectToAppraise.setObjectType(objectTypeRepository.findObjectTypeByType(objectToAppraise.getObjectType().getType()));
        objectToAppraiseRepository.save(objectToAppraise);
    }
    public boolean deleteObject(Long id){
        if(appraisalAgreementRepository.getByObjectId(id).isEmpty()){
            objectToAppraiseRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public ObjectToAppraise findByTypeAndDescription(ObjectToAppraise objectToAppraise){
        return objectToAppraiseRepository.findByTypeIdAndDescription(objectToAppraise.getObjectType().getType(), objectToAppraise.getDescription());
    }
    public void updateObject(Long id, String description){
        ObjectToAppraise objectToAppraise =objectToAppraiseRepository.getById(id);
        objectToAppraise.setDescription(description);
        objectToAppraiseRepository.save(objectToAppraise);
    }

}
