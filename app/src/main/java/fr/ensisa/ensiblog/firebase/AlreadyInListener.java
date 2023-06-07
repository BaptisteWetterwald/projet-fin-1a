package fr.ensisa.ensiblog.firebase;

public interface AlreadyInListener {
    void onCheckComplete(boolean exists);
    void onCheckFailed(Exception e);
}
