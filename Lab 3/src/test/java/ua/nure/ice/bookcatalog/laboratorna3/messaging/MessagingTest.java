package ua.nure.ice.bookcatalog.laboratorna3.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;

import org.junit.jupiter.api.Test;

class MessagingTest {

    @Test
    void simpleMessageShouldReturnContent() {
        Message message = new SimpleMessage("Hello World");
        assertEquals("Hello World", message.getContent());
    }

    @Test
    void loggingDecoratorShouldReturnContent() {
        Message message = new SimpleMessage("Log Test");
        Message loggedMessage = new LoggingDecorator(message);
        
        // This will print to console, we just verify it doesn't modify the return string
        assertEquals("Log Test", loggedMessage.getContent());
    }

    @Test
    void encryptionDecoratorShouldEncodeBase64() {
        String original = "Secret";
        Message message = new SimpleMessage(original);
        Message encryptedMessage = new EncryptionDecorator(message);
        
        String encoded = Base64.getEncoder().encodeToString(original.getBytes());
        assertEquals("[ENCRYPTED] " + encoded, encryptedMessage.getContent());
    }

    @Test
    void timestampDecoratorShouldPrependTimestamp() {
        Message message = new SimpleMessage("Time Test");
        Message timestampedMessage = new TimestampDecorator(message);
        
        String result = timestampedMessage.getContent();
        assertTrue(result.startsWith("[20"));
        assertTrue(result.contains("] Time Test"));
    }

    @Test
    void signatureDecoratorShouldAppendSignature() {
        Message message = new SimpleMessage("Sign Test");
        Message signedMessage = new SignatureDecorator(message);
        
        String result = signedMessage.getContent();
        assertTrue(result.startsWith("Sign Test"));
        assertTrue(result.contains("[SIGNED: BookCatalog System]"));
    }
    
    @Test
    void decoratorsShouldStackProperly() {
        Message base = new SimpleMessage("Test");
        Message decorated = new SignatureDecorator(new EncryptionDecorator(base));
        
        String encodedTest = Base64.getEncoder().encodeToString("Test".getBytes());
        String expectedStart = "[ENCRYPTED] " + encodedTest;
        
        String result = decorated.getContent();
        assertTrue(result.startsWith(expectedStart));
        assertTrue(result.endsWith("[SIGNED: BookCatalog System]"));
    }
}
