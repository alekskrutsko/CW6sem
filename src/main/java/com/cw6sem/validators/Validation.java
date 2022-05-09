package com.cw6sem.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    private static Pattern patternName;
    private static Pattern patternDouble;
    private static Pattern patternEmail;
    private static Pattern patternPhone;
    private static Pattern patternPassword;
    private static Matcher matcher;

    private static final String NAME_PATTERN="([A-ZА-Я][a-zа-я]{1,30})";
    private static final String DOUBLE_PATTERN ="\\A[0-9]{1,8}(?:[.][0-9])?\\z";
    private static final String PASSWORD_PATTERN="[\\x21-\\x7E]{6,64}|^$";
    private static final String EMAIL_PATTERN =
            "^[0-9a-zA-Z]+([0-9a-zA-Z]*[-._+])*[0-9a-zA-Z]+@[0-9a-zA-Z]+([-.][0-9a-zA-Z]+)*([0-9a-zA-Z]*[.])[a-zA-Z]{2,6}$";
    private static final String PHONE_PATTERN =
            "\\+375\\(\\d{2}\\)\\d{3}-\\d{2}-\\d{2}";
    static {
        patternName= Pattern.compile(NAME_PATTERN);
        patternEmail= Pattern.compile(EMAIL_PATTERN);
        patternPhone= Pattern.compile(PHONE_PATTERN);
        patternPassword= Pattern.compile(PASSWORD_PATTERN);
        patternDouble = Pattern.compile(DOUBLE_PATTERN);
    }

    public static boolean validateString(final String hex) {
        matcher = patternName.matcher(hex);
        return matcher.matches();
    }
    public static boolean validateDouble(final String hex) {
        matcher = patternDouble.matcher(hex);
        return matcher.matches();
    }
    public static boolean validateEmail(final String hex) {
        matcher = patternEmail.matcher(hex);
        return matcher.matches();
    }
    public static boolean validatePhone(final String hex) {
        matcher = patternPhone.matcher(hex);
        return matcher.matches();
    }
    public static boolean validatePassword(final String hex) {
        matcher = patternPassword.matcher(hex);
        return matcher.matches();
    }
}
