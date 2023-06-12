package fr.ensisa.ensiblog.models.posts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;

public class Content {

    private String type = this.getClass().getSimpleName();
    private String data;

    public Content(){
        // empty constructor required for firebase
    }

    public Content(ContentType type, String data){
        this.type = type.getType();
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}

