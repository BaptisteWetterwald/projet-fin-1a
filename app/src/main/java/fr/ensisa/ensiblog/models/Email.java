package fr.ensisa.ensiblog.models;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Class used to verify and get the Email of a user
 **/
public class Email implements Serializable {

    private String address;

    public Email(){
        // empty constructor required for firebase
    }

    public Email(String address) {
        this.address = address;
    }

    public static boolean isValid(String address){
        String emailRegex = "^[a-zA-Z]+\\.[a-zA-Z]+[0-9]?+@uha\\.fr$";
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
        // baptiste.wetterwald-ad@uha.fr should return : ["Baptiste", "Wetterwald-Ad"]
        // split with @
        String[] split = this.address.split("@");

        // split[0] should be baptiste.wetterwald-ad
        // split with .
        String[] names = split[0].split("\\.");

        // names[0] should be baptiste
        // names[1] should be wetterwald-ad

        // names[0] should be Baptiste
        names[0] = names[0].substring(0, 1).toUpperCase() + names[0].substring(1);

        // each word from names[1] (split with -) should be capitalized
        String[] names1 = names[1].split("-");
        for (int i = 0; i < names1.length; i++) {
            names1[i] = names1[i].substring(0, 1).toUpperCase() + names1[i].substring(1);
        }

        // names[1] should be Wetterwald-Ad
        names[1] = String.join("-", names1);

        return names;
    }

    public String firstName(){
        return names()[0];
    }

    public String lastName(){
        return names()[1];
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Email))
            return false;
        if (obj == this)
            return true;
        return this.getAddress().equals(((Email) obj).getAddress());
    }
}
