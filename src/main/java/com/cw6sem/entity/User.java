package com.cw6sem.entity;

import com.cw6sem.domain.Role;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String surname;
    private String patronymic;
    private String phone;
    private Role role;
    private boolean blocked;


    public String getStrRole(){
        if(role == Role.CUSTOMER){
            return "Заказчик";
        }else return "Оценщик";
    }
}