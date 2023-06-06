package fr.ensisa.ensiblog.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
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

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
            }
        });
    }



}
