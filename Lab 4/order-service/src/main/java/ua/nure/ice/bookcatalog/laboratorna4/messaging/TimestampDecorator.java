package ua.nure.ice.bookcatalog.laboratorna4.messaging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimestampDecorator extends MessageDecorator {

    public TimestampDecorator(Message wrapper) {
        super(wrapper);
    }

    @Override
    public String getContent() {
        String content = super.getContent();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return "[" + timestamp + "] " + content;
    }
}
