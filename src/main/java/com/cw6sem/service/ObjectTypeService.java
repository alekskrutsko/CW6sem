package com.cw6sem.service;

import com.cw6sem.entity.ObjectType;
import com.cw6sem.repository.ObjectToAppraiseRepository;
import com.cw6sem.repository.ObjectTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ObjectTypeService {
    private final ObjectTypeRepository objectTypeRepository;
    private final ObjectToAppraiseRepository objectToAppraiseRepository;
    public void saveByType(String  type){
        ObjectType objectType = new ObjectType();
        objectType.setType(type);
        objectTypeRepository.save(objectType);
    }
    public Optional<ObjectType> getObjectTypeByType(String type){
        return objectTypeRepository.getObjectTypeByType(type);
    }
    public List<ObjectType> findAll(){
        return objectTypeRepository.findAll();
    }
    public boolean deleteType(Long id){
        if(objectToAppraiseRepository.getByTypeId(id).isEmpty()){
            objectTypeRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public void updateType(Long id, String type){
        ObjectType objectType =objectTypeRepository.getById(id);
        objectType.setType(type);
        objectTypeRepository.save(objectType);
    }
}
