package fr.ensisa.ensiblog.firebase;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import fr.ensisa.ensiblog.models.Topic;

public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Database instance;

    private static final String NAME_DB_TOPICS = "Topics";

    private Database() {} // Singleton

    public static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    public boolean addTopic(Topic topic) {
        CollectionReference dbTopics = db.collection(NAME_DB_TOPICS);
        try {
            dbTopics.add(topic);
            Log.i("ENSIBLOG", "Success");
            return true;
        }
        catch (Exception e){
            Log.i("ENSIBLOG", e.getMessage());
            return false;
        }

    }

    public Topic getTopic(String name) {
        CollectionReference dbTopics = db.collection(NAME_DB_TOPICS);
        Query query = dbTopics.whereEqualTo("name",name);
        Log.i("ENSISABLOG",query.toString());
        Task<QuerySnapshot> querySnapshotTask= query.get();
        Log.i("ENSISABLOG",querySnapshotTask.toString());
        for (DocumentSnapshot doc : querySnapshotTask.getResult().getDocuments()) {
            Log.i("ENSISABLOG",doc.getId());
        }
        return null;
        //List<Topic> topics = querySnapshotTask.getResult().toObjects(Topic.class);
        //return topics.get(0);
        // return dbTopics.document(name).get().getResult().toObject(Topic.class);
    }

}
