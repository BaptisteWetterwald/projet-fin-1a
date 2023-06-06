package fr.ensisa.ensiblog.firebase;

import fr.ensisa.ensiblog.models.Topic;

public interface TopicListener {
    void onTopicRetrieved(Topic topic);
    void onTopicRetrievalFailed(Exception e);
}
