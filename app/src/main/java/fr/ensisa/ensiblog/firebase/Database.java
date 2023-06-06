package fr.ensisa.ensiblog.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

import fr.ensisa.ensiblog.models.Topic;

public class Database {
    private FirebaseFirestore db;

    public Database(){
        db = FirebaseFirestore.getInstance();
    }

    public String addTopic(Topic topic){

        CollectionReference dbTopics = db.collection("Topics");

        dbTopics.add(topic).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(MainActivity.this, "Your Course has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to add course \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
