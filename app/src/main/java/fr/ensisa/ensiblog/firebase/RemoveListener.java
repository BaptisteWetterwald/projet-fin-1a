package fr.ensisa.ensiblog.firebase;

public interface RemoveListener {
    void onRemoveComplete();
    void onRemoveFailed(Exception e);
}
