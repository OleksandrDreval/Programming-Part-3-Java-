package ua.nure.ice.bookcatalog.laboratorna2.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class DeliveryAddressTest {

    @Test
    void builderShouldCreateValidAddress() {
        DeliveryAddress address = new DeliveryAddress.Builder("Ukraine", "Kyiv", "Khreshchatyk", "1")
                .apartmentNumber("101")
                .postalCode("01001")
                .additionalDetails("Call upon arrival")
                .build();

        assertEquals("Ukraine", address.getCountry());
        assertEquals("Kyiv", address.getCity());
        assertEquals("Khreshchatyk", address.getStreet());
        assertEquals("1", address.getBuildingNumber());
        assertEquals("101", address.getApartmentNumber());
        assertEquals("01001", address.getPostalCode());
        assertEquals("Call upon arrival", address.getAdditionalDetails());
        
        String toString = address.toString();
        assertTrue(toString.contains("Khreshchatyk"));
        assertTrue(toString.contains("Kyiv"));
        assertTrue(toString.contains("Ukraine"));
        assertTrue(toString.contains("101"));
        assertTrue(toString.contains("Call upon arrival"));
        assertTrue(toString.contains("01001"));
    }

    @Test
    void builderShouldCreateAddressWithOnlyRequiredFields() {
        DeliveryAddress address = new DeliveryAddress.Builder("Ukraine", "Kyiv", "Khreshchatyk", "1").build();

        assertEquals("Ukraine", address.getCountry());
        assertEquals("Kyiv", address.getCity());
        assertEquals("Khreshchatyk", address.getStreet());
        assertEquals("1", address.getBuildingNumber());
        assertNull(address.getApartmentNumber());
        assertNull(address.getPostalCode());
        assertNull(address.getAdditionalDetails());
        
        String toString = address.toString();
        assertTrue(toString.contains("Khreshchatyk 1"));
        assertTrue(toString.contains("Kyiv, Ukraine"));
    }

    @Test
    void builderShouldThrowWhenCountryIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("", "Kyiv", "Khreshchatyk", "1"));
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder(null, "Kyiv", "Khreshchatyk", "1"));
    }

    @Test
    void builderShouldThrowWhenCityIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("Ukraine", "", "Khreshchatyk", "1"));
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("Ukraine", null, "Khreshchatyk", "1"));
    }

    @Test
    void builderShouldThrowWhenStreetIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("Ukraine", "Kyiv", "", "1"));
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("Ukraine", "Kyiv", null, "1"));
    }

    @Test
    void builderShouldThrowWhenBuildingIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("Ukraine", "Kyiv", "Khreshchatyk", ""));
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryAddress.Builder("Ukraine", "Kyiv", "Khreshchatyk", null));
    }
    
    @Test
    void builderShouldHandleEmptyOptionalFields() {
        DeliveryAddress address = new DeliveryAddress.Builder("Ukraine", "Kyiv", "Khreshchatyk", "1")
                .apartmentNumber("")
                .postalCode("")
                .additionalDetails("")
                .build();
                
        String toString = address.toString();
        assertFalse(toString.contains("apt."));
        assertFalse(toString.contains("Note:"));
    }
}
