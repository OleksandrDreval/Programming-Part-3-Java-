package ua.nure.ice.bookcatalog.practical4.messaging;

public class LoggingDecorator extends MessageDecorator {

    public LoggingDecorator(Message wrapper) {
        super(wrapper);
    }

    @Override
    public String getContent() {
        String content = super.getContent();
        // Simulate logging
        System.out.println("[LOG] Sending message: " + content);
        return content;
    }
}
