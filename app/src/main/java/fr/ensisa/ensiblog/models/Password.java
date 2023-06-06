package fr.ensisa.ensiblog.models;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Password {
    private String password;

    public static boolean isValid(String password){
        if (password.length() < 8) {
            return false;
        }
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).*$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public Password(){
        // empty constructor required for firebase
    }
}
