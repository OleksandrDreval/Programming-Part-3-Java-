package ua.nure.ice.bookcatalog.laboratorna4.model.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;

@Embeddable
public class DeliveryAddress {
    private String country;
    private String city;
    private String street;
    private String buildingNumber;
    private String apartmentNumber;
    private String postalCode;
    private String additionalDetails;

    public DeliveryAddress() {}

    private DeliveryAddress(Builder builder) {
        this.country = builder.country;
        this.city = builder.city;
        this.street = builder.street;
        this.buildingNumber = builder.buildingNumber;
        this.apartmentNumber = builder.apartmentNumber;
        this.postalCode = builder.postalCode;
        this.additionalDetails = builder.additionalDetails;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(" ").append(buildingNumber);
        if (apartmentNumber != null && !apartmentNumber.isEmpty()) {
            sb.append(", apt. ").append(apartmentNumber);
        }
        sb.append("\n").append(city).append(", ").append(country);
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        if (additionalDetails != null && !additionalDetails.isEmpty()) {
            sb.append("\nNote: ").append(additionalDetails);
        }
        return sb.toString();
    }

    public static class Builder {
        
        private final String country;
        private final String city;
        private final String street;
        private final String buildingNumber;

        
        private String apartmentNumber;
        private String postalCode;
        private String additionalDetails;

        public Builder(String country, String city, String street, String buildingNumber) {
            if (country == null || country.trim().isEmpty()) {
                throw new IllegalArgumentException("Country cannot be null or empty");
            }
            if (city == null || city.trim().isEmpty()) {
                throw new IllegalArgumentException("City cannot be null or empty");
            }
            if (street == null || street.trim().isEmpty()) {
                throw new IllegalArgumentException("Street cannot be null or empty");
            }
            if (buildingNumber == null || buildingNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Building number cannot be null or empty");
            }

            this.country = country;
            this.city = city;
            this.street = street;
            this.buildingNumber = buildingNumber;
        }

        public Builder apartmentNumber(String apartmentNumber) {
            this.apartmentNumber = apartmentNumber;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder additionalDetails(String additionalDetails) {
            this.additionalDetails = additionalDetails;
            return this;
        }

        public DeliveryAddress build() {
            return new DeliveryAddress(this);
        }
    }
}
