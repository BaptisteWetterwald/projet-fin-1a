package fr.ensisa.ensiblog.models;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Password {
    private String password;

    public static boolean isValid(String password){
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).*$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public Password(){
        // empty constructor required for firebase
    }
}
