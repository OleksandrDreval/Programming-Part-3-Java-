package ua.nure.ice.bookcatalog.practice3.messaging;

public abstract class MessageDecorator implements Message {
    protected final Message wrapper;

    public MessageDecorator(Message wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public String getContent() {
        return wrapper.getContent();
    }
}
