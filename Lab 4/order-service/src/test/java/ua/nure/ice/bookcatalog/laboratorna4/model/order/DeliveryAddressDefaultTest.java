package ua.nure.ice.bookcatalog.laboratorna4.model.order;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeliveryAddressDefaultTest {

    @Test
    void testDefaultConstructor() {
        DeliveryAddress address = new DeliveryAddress();
        assertNull(address.getCountry());
        assertNull(address.getCity());
        assertNull(address.getStreet());
        assertNull(address.getBuildingNumber());
    }
}
