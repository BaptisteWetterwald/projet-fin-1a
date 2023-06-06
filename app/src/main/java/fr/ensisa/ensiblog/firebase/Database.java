package fr.ensisa.ensiblog.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import fr.ensisa.ensiblog.models.Topic;

public class Database {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static Database instance;

    private static final String NAME_DB_TOPICS = "Database";

    private Database() {} // Singleton

    public static Database getInstance(){
        if (instance == null){
            instance = new Database();
        }
        return instance;
    }

    public boolean addTopic(Topic topic) {
        CollectionReference dbTopics = db.collection(NAME_DB_TOPICS);
        return dbTopics.add(topic).isSuccessful();
    }

    public Topic getTopic(String id) {
        CollectionReference dbTopics = db.collection(NAME_DB_TOPICS);
        return dbTopics.document(id).get().getResult().toObject(Topic.class);
    }

}
