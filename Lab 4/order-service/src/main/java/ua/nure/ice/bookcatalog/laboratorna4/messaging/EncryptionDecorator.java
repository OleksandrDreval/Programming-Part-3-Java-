package ua.nure.ice.bookcatalog.laboratorna4.messaging;

import java.util.Base64;

public class EncryptionDecorator extends MessageDecorator {

    public EncryptionDecorator(Message wrapper) {
        super(wrapper);
    }

    @Override
    public String getContent() {
        String content = super.getContent();
        
        return "[ENCRYPTED] " + Base64.getEncoder().encodeToString(content.getBytes());
    }
}
