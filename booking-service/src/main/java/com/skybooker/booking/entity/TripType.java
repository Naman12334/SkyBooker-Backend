package com.skybooker.booking.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TripType {
    ONE_WAY,
    ROUND_TRIP;

    @JsonCreator
    public static TripType fromValue(String value) {
        for (TripType type : TripType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "Unknown TripType: " + value);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}