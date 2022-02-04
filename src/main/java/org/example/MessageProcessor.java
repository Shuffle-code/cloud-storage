package org.example;

import org.example.model.CloudMessage;

public interface MessageProcessor {
    void processMessage(CloudMessage msg);
}
