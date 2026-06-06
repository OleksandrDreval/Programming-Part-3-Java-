package ua.nure.ice.bookcatalog.laboratorna4.messaging;

public class SignatureDecorator extends MessageDecorator {

    public SignatureDecorator(Message wrapper) {
        super(wrapper);
    }

    @Override
    public String getContent() {
        String content = super.getContent();
        return content + "\n--\n[SIGNED: BookCatalog System]";
    }
}
