package com.skybooker.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FareSummary {

    private double baseFare;
    private double taxes;
    private double fuelSurcharge;
    private double mealCharges;
    private double baggageCharges;
    private double totalFare;

    // Calculate total fare
    public void calculateTotal() {
        this.totalFare = baseFare + taxes
                + fuelSurcharge + mealCharges
                + baggageCharges;
    }
}