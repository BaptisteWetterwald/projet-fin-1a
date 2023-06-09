package fr.ensisa.ensiblog.models.posts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;

public abstract class Content {
    private byte[] data;

    public Content(){
        // empty constructor required for firebase
    }

    public byte[] getData() {
        return data;
    }

    public void setData(Object o) throws Exception {
        this.data = this.convertObjectToBytes(o);
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] convertObjectToBytes(Object o) throws Exception {
    	// convert object to bytes
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(o);
            return bos.toByteArray();
        } catch (Exception e) {
            throw e;
        }
    }

}

