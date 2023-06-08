package fr.ensisa.ensiblog.models;
public class Text extends Content {

    private String text;

    public Text(){
        // empty constructor required for firebase
    }

    public Text(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
