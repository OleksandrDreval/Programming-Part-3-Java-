package ua.nure.ice.bookcatalog.laboratorna1.messaging;

public class SimpleMessage implements Message {
    private final String content;

    public SimpleMessage(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }
}
