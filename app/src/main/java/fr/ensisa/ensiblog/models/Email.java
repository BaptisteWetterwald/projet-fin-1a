package fr.ensisa.ensiblog.models;
public class Email {

    private final String address;

    public Email(String address) {
        this.address = address;
    }

    public static boolean isValid(String address){
        return true;
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
