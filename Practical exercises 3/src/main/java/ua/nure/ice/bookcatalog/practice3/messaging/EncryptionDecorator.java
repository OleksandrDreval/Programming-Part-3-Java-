package ua.nure.ice.bookcatalog.practice3.messaging;

import java.util.Base64;

public class EncryptionDecorator extends MessageDecorator {

    public EncryptionDecorator(Message wrapper) {
        super(wrapper);
    }

    @Override
    public String getContent() {
        String content = super.getContent();
        // Simulate basic encryption with Base64
        return "[ENCRYPTED] " + Base64.getEncoder().encodeToString(content.getBytes());
    }
}
