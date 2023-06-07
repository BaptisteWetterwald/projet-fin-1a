package fr.ensisa.ensiblog.models;

import java.util.List;

public abstract class Content {
    private List<Byte> data;

    public Content(){
        // empty constructor required for firebase
    }

    public List<Byte> getData() {
        return data;
    }

    public void setData(List<Byte> data) {
        this.data = data;
    }

}

