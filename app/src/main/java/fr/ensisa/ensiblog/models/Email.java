package fr.ensisa.ensiblog.models;
import java.util.regex.Pattern;
public class Email {

    private String address;

    public Email(){
        // empty constructor required for firebase
    }

    public Email(String address) {
        this.address = address;
    }

    public static boolean isValid(String address){
        String emailRegex = "^[a-zA-Z]+\\.[a-zA-Z]+@uha\\.fr$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(address).matches();
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String[] names(){
        String beforeAt = address.split("@")[0];
        return beforeAt.split("\\.");
    }

    public String firstName(){
        return names()[0];
    }

    public String lastName(){
        return names()[1];
    }
}
