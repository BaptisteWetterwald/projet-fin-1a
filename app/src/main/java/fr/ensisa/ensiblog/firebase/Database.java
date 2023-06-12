package fr.ensisa.ensiblog.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.internal.cache.DiskLruCache;

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

    public interface AlreadyInCallback {
        void onResult(boolean alreadyExists);
    }

    public void onModif(String tableName, String field, Object value, EventListener<QuerySnapshot> listener){
        db.collection(tableName)
            .whereEqualTo(field, value)
            .addSnapshotListener(listener);
    }

    public void alreadyIn(String tableName, String[] fields, Object[] values, AlreadyInCallback callback) throws IllegalArgumentException {

        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values must have the same length");
        }

        if (fields.length == 0) {
            throw new IllegalArgumentException("Fields and values must not be empty");
        }

        Query query = db.collection(tableName);
        for (int i = 0; i < fields.length; i++) {
            query = query.whereEqualTo(fields[i], values[i]);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        // Return true if any matching documents are found, false otherwise
                        List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                        boolean alreadyExists = !documents.isEmpty();

                        // Invoke the callback with the result
                        callback.onResult(alreadyExists);
                    } else {
                        Log.w("n6a", "Error checking if document already exists: " + task.getException());
                        // Invoke the callback with a default value indicating an error
                        callback.onResult(false);
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
    public void update(String tableName, Object object, String[] fields, Object[] values) throws IllegalArgumentException {
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

    // Method to remove lines in the database with fields and values as parameters
    public Task<Void> removeFrom(String tableName, String[] fields, Object[] values) throws IllegalArgumentException {
        final TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        CollectionReference dbTable = db.collection(tableName);

        if (fields.length != values.length) {
            throw new IllegalArgumentException("Fields and values must have the same length");
        }

        if (fields.length == 0) {
            dbTable.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Task<Void>> deletionTasks = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        deletionTasks.add(documentSnapshot.getReference().delete());
                    }
                    Tasks.whenAll(deletionTasks).addOnCompleteListener(deletionTask -> {
                        if (deletionTask.isSuccessful()) {
                            taskCompletionSource.setResult(null);
                        } else {
                            taskCompletionSource.setException(Objects.requireNonNull(deletionTask.getException()));
                        }
                    });
                } else {
                    taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                }
            });
        } else {
            Query query = dbTable.whereEqualTo(fields[0], values[0]);

            for (int i = 1; i < fields.length; i++) {
                query = query.whereEqualTo(fields[i], values[i]);
            }

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<Task<Void>> deletionTasks = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                        deletionTasks.add(documentSnapshot.getReference().delete());
                    }
                    Tasks.whenAll(deletionTasks).addOnCompleteListener(deletionTask -> {
                        if (deletionTask.isSuccessful()) {
                            taskCompletionSource.setResult(null);
                        } else {
                            taskCompletionSource.setException(Objects.requireNonNull(deletionTask.getException()));
                        }
                    });
                } else {
                    taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
                }
            });
        }

        return taskCompletionSource.getTask();
    }



    public Task add(String tableName, Object object, Class objectClass) {

        CollectionReference dbTable = db.collection(tableName);

        // get instance of object class for the object to add

        return dbTable.add(Objects.requireNonNull((objectClass).cast(object)));
    }

    public void add(String tableName, Object object, Class objectClass, boolean unique) {

        CollectionReference dbTable = db.collection(tableName);

        List<String> fields = new ArrayList<String>();
        List<Object> values = new ArrayList<>();
        for (Field field:Objects.requireNonNull((objectClass).cast(object)).getClass().getDeclaredFields()) {
            field.setAccessible(true); // Set the field accessible (in case it's private or protected)
            try {
                Object obj = field.get(object);
                if(obj!=null){
                    fields.add(field.getName());
                    values.add(obj);
                }
            } catch (IllegalAccessException e) {}
            field.setAccessible(false);
        }
        String[] stringFields = new String[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            stringFields[i] = fields.get(i);
        }
        if(unique){
            instance.alreadyIn(tableName, stringFields, values.toArray(), alreadyExists -> {
                if (alreadyExists) {
                    // Do something if the topic already exists
                    Log.i("n6a","Topic already exists");
                } else {
                    Log.i("n6a","Topic does not exist, adding topic to DB");
                    dbTable.add(Objects.requireNonNull((objectClass).cast(object)));
                }
            });
        } else {
            Log.i("n6a","not unique constrainte");
            dbTable.add(Objects.requireNonNull((objectClass).cast(object)));
        }
    }

    public void add(String tableName, Object object, Class objectClass, String[] uniqueFields) {

        CollectionReference dbTable = db.collection(tableName);

        Object[] values = new Object[uniqueFields.length];
        for (int i=0;i<uniqueFields.length;i++) {
            try {
                Field field = object.getClass().getDeclaredField(uniqueFields[i]);
                field.setAccessible(true);
                values[i] = field.get(object);
                field.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {}
        }

        instance.alreadyIn(tableName, uniqueFields, values, alreadyExists -> {
            if (alreadyExists) {
                Log.i("n6a","Topic already exists");
            } else {
                Log.i("n6a","Topic does not exist, adding topic to DB");
                dbTable.add(Objects.requireNonNull((objectClass).cast(object)));
            }
        });
    }
}
