package fr.ensisa.ensiblog.models;

import java.io.File;

public class Image extends Content {

    public Image(){
        // empty constructor required for firebase
    }

    public Image(byte[] data) {
        setData(data);
    }

    public Image(File file) throws Exception {
        setData(convertObjectToBytes(file));
    }

}
