package fr.ensisa.ensiblog.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import fr.ensisa.ensiblog.models.Topic;

public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Database instance;

    private Database() {
    } // Singleton

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void alreadyIn(String tableName, String[] fields, String[] values, final AlreadyInListener listener) throws IllegalArgumentException {
        CollectionReference dbTable = db.collection(tableName);

        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values must have the same length");
        }

        if (fields.length == 0) {
            throw new IllegalArgumentException("Fields and values must not be empty");
        }

        // Map fields and values to a query
        Query query = dbTable.whereEqualTo(fields[0], values[0]);
        for (int i = 1; i < fields.length; i++) {
            query = query.whereEqualTo(fields[i], values[i]);
        }

        // Execute the query
        Task<QuerySnapshot> task = query.get();
        task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    boolean exists = !querySnapshot.isEmpty();
                    listener.onCheckComplete(exists);
                } else {
                    listener.onCheckFailed(task.getException());
                }
            }
        });
    }

    public void remove(String tableName, Object object){
        CollectionReference dbTable = db.collection(tableName);
        dbTable.document(object.toString()).delete();
    }

    public void update(String tableName, Object old, Object newObject){
        CollectionReference dbTable = db.collection(tableName);
        dbTable.document(old.toString()).set(newObject);
    }


    public <T> Task<List<T>> get(String tableName, final Class<T> objectClass, String[] fields, Object[] values) throws IllegalArgumentException {
        final TaskCompletionSource<List<T>> taskCompletionSource = new TaskCompletionSource<>();

        CollectionReference dbTable = db.collection(tableName);

        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values must have the same length");
        }

        if (fields.length == 0) {
            dbTable.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<T> objects = querySnapshot.toObjects(objectClass);
                        taskCompletionSource.setResult(objects);
                    } else {
                        taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                    }
                }
            });
        } else {
            Query query = dbTable.whereEqualTo(fields[0], values[0]);

            for (int i = 1; i < fields.length; i++) {
                query = query.whereEqualTo(fields[i], values[i]);
            }

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        List<T> objects = querySnapshot.toObjects(objectClass);
                        taskCompletionSource.setResult(objects);
                    } else {
                        taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                    }
                }
            });
        }

        return taskCompletionSource.getTask();
    }

    // method to update lines in the database with fields and values parameters as new values and object as the object to update
    public void update(String tableName, Object object, String[] fields, String[] values) throws IllegalArgumentException {
        CollectionReference dbTable = db.collection(tableName);

        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values must have the same length");
        }

        if (fields.length == 0) {
            dbTable.get().addOnCompleteListener(task -> {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    documentSnapshot.getReference().set(object);
                }
            });
        } else {
            Query query = dbTable.whereEqualTo(fields[0], values[0]);
            for (int i = 1; i < fields.length; i++) {
                query = query.whereEqualTo(fields[i], values[i]);
            }
            query.get().addOnCompleteListener(task -> {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    documentSnapshot.getReference().set(object);
                }
            });
        }
    }

    // method to remove lines in the database with fields and values as parameters
    public void removeFrom(String tableName, String[] fields, String[] values) throws IllegalArgumentException {
        CollectionReference dbTable = db.collection(tableName);

        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values must have the same length");
        }

        if (fields.length == 0) {
            dbTable.get().addOnCompleteListener(task -> {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    documentSnapshot.getReference().delete();
                }
            });
        } else {
            Query query = dbTable.whereEqualTo(fields[0], values[0]);
            for (int i = 1; i < fields.length; i++) {
                query = query.whereEqualTo(fields[i], values[i]);
            }
            query.get().addOnCompleteListener(task -> {
                QuerySnapshot querySnapshot = task.getResult();
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    documentSnapshot.getReference().delete();
                }
            });
        }
    }

    public void add(String tableName, Object object, Class objectClass) {
        CollectionReference dbTable = db.collection(tableName);

        // get instance of object class for the object to add

        dbTable.add(Objects.requireNonNull((objectClass).cast(object))).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("n6a", "DocumentSnapshot added with ID: " + task.getResult().getId());
            } else {
                Log.w("n6a", "Error adding document", task.getException());
            }
        });
    }

}
