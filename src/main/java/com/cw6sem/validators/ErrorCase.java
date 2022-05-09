package com.cw6sem.validators;

public class ErrorCase {
    public static String errorMsg;
    public static String errorAuthorization() {
        return "Неверный логин или пароль";
    }
    public static String errorEmail() {
        return "Неверный email";
    }
    public static String errorPhone() {return "Номер телефона введен не в формате +375(**)***-**-**";}
    public static String errorName() {return "Имя/Фамилия/Отчество должны начинаться с заглавной буквы и не должно содержать специальные символы";}
    public static String errorDouble() {return "Стоимость должна содержать только цифры и точку между ними, если необходимо (целочисленная часть не больше 8 символов, дробная не больше одного)";}
    public static String errorPassword() {return "Пароль должен быть в диапозоне 6-64 символов и содержать печатные символы без пробелов";}
    public static String errorPersonalEmail() {return "Проверьте почту!";}
    public static String userExists(){ return "Пользователь с таким логином уже существует";}
    public static String waitingForAppraiser(){ return "Дождитесь ответа оценщика";}
    public static String contractTerminated(){ return "Контракт расторгнут";}
    public static String contractSigned(){ return "Контракт подписан";}
    public static String contractWaitForCustomer(){ return "Ожидайте ответа заказчика";}
    public static String suchObjectExists(){ return "Объект с такими же параметрами уже существует";}
    public static String suchTypeExists(){ return "Тип уже существует";}
    public static String userAlreadyCustomer(){ return "Пользователь и так имеет роль Заказчика";}
    public static String userAlreadyAppraiser(){ return "Пользователь и так имеет роль Оценщика";}
    public static String thisObjectIsUsedInAnotherTable(){ return "Данный объек не может быть удален т.к. используется в другой таблице";}
    public static String thisTypeIsUsedInAnotherTable(){ return "Данный тип не может быть удален т.к. используется в другой таблице";}
}
