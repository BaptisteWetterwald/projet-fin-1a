package fr.ensisa.ensiblog.models;
import java.util.regex.Pattern;
public class Email {

    private final String address;

    public Email(){
        // empty constructor required for firebase
    }

    public Email(String address) {
        this.address = address;
    }

    public static boolean isValid(String address){
        String emailRegex = "^[a-zA-Z]+\\.[a-zA-Z]+@uha\\.fr$";
        Pattern pattern = Pattern.compile(emailRegex);
        boolean isMatch = pattern.matcher(address).matches();
        return isMatch;
    }

    public String getAddress(){
        return this.address;
    }

    public String[] getNames(){
        String beforeAt = address.split("@")[0];
        return beforeAt.split(".");
    }

    public String getFirstName(){
        return getNames()[0];
    }

    public String getLastName(){
        return getNames()[1];
    }
}
