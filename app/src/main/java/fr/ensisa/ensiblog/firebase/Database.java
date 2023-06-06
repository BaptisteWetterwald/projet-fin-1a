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

    private static final String NAME_DB_TOPICS = "Topics";

    private Database() {
    } // Singleton

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void alreadyIn(String tableName, String[] fields, String[] values, final AlreadyInListener listener) {
        CollectionReference dbTable = db.collection(tableName);

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

    public boolean addTopic(Topic topic) {
        CollectionReference dbTopics = db.collection(NAME_DB_TOPICS);
        try {
            dbTopics.add(topic);
            Log.i("n6a", "Success");
            return true;
        } catch (Exception e) {
            Log.i("n6a", e.getMessage());
            return false;
        }

    }

    public void getTopic(String name, final TopicListener listener) {
        CollectionReference dbTopics = db.collection(NAME_DB_TOPICS);
        Query query = dbTopics.whereEqualTo("name", name);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                if (!documents.isEmpty()) {
                    DocumentSnapshot document = documents.get(0);
                    Topic topic = document.toObject(Topic.class);
                    Log.i("n6a", topic.getName());
                    listener.onTopicRetrieved(topic);
                } else {
                    listener.onTopicRetrieved(null); // No matching topic found
                }
            } else {
                listener.onTopicRetrievalFailed(task.getException()); // Error occurred
            }
        });
    }

    public void removeFrom(String tableName, String[] fields, String[] values, final AlreadyInListener listener) {
        CollectionReference dbTable = db.collection(tableName);

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
                QuerySnapshot querySnapshot = task.getResult();
                boolean exists = !querySnapshot.isEmpty();
                if (exists) {
                    querySnapshot.getDocuments().get(0).getReference().delete();
                }
                listener.onCheckComplete(exists);
            }
        });
    }

    public void removeFrom(String tableName, Object object){
        CollectionReference dbTable = db.collection(tableName);
        dbTable.document(object.toString()).delete();
    }

    public void update(String tableName, Object object){
        CollectionReference dbTable = db.collection(tableName);
        dbTable.document(object.toString()).set(object);
    }

    public void update(String tableName, String[] fields, String[] values, Object object){
        CollectionReference dbTable = db.collection(tableName);
        Query query = dbTable.whereEqualTo(fields[0], values[0]);
        for (int i = 1; i < fields.length; i++) {
            query = query.whereEqualTo(fields[i], values[i]);
        }
        query.get().addOnCompleteListener(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            boolean exists = !querySnapshot.isEmpty();
            if (exists) {
                querySnapshot.getDocuments().get(0).getReference().set(object);
            }
        });
    }

    public <T> Task<List<T>> getObjects(String tableName, final Class<T> objectClass, String[] fields, Object[] values) {
        final TaskCompletionSource<List<T>> taskCompletionSource = new TaskCompletionSource<>();

        CollectionReference dbTable = db.collection(tableName);
        Query query = dbTable.orderBy("name", Query.Direction.ASCENDING);

        for (int i = 0; i < fields.length; i++) {
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

        return taskCompletionSource.getTask();
    }

}
